/*
 *
 * ((e)) emite: A pure gwt (Google Web Toolkit) xmpp (jabber) library
 *
 * (c) 2008-2009 The emite development team (see CREDITS for details)
 * This file is part of emite.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.calclab.emite.im.client.presence;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.events.PresenceEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.session.XmppSession.SessionState;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.core.client.xmpp.stanzas.Presence.Type;
import com.calclab.emite.im.client.roster.RosterStateChangedEvent;
import com.calclab.emite.im.client.roster.Roster.RosterState;
import com.google.gwt.core.client.GWT;

/**
 * @see PresenceManager
 */
public class PresenceManagerImpl extends AbstractPresenceManager {
    static final Presence INITIAL_PRESENCE = new Presence(Type.unavailable, null, null);
    private final XmppSession session;

    public PresenceManagerImpl(final EmiteEventBus eventBus, final XmppSession session) {
	super(eventBus);
	this.session = session;
	setOwnPresence(INITIAL_PRESENCE);

	// Upon connecting to the server and becoming an active resource, a
	// client SHOULD request the roster before sending initial presence

	eventBus.addHandler(RosterStateChangedEvent.getType(), new StateChangedHandler() {
	    @Override
	    public void onStateChanged(final StateChangedEvent event) {
		if (event.is(RosterState.ready)) {
		    sendInitialPresence();
		}
	    }
	});

	session.addIncomingPresenceHandler(new PresenceHandler() {
	    @Override
	    public void onPresence(final PresenceEvent event) {
		final Presence presence = event.getPresence();
		final Type type = presence.getType();
		if (type == Type.probe) {
		    session.send(getOwnPresence());
		} else if (type == Type.error) {
		    // FIXME: what should we do
		    GWT.log("Error presence!!!", null);
		}
	    }
	});

	session.addSessionStateChangedHandler(new StateChangedHandler() {
	    @Override
	    public void onStateChanged(final StateChangedEvent event) {
		final String state = event.getState();
		if (state == SessionState.loggingOut) {
		    logOut(session.getCurrentUser());
		} else if (state == SessionState.disconnected) {
		    setOwnPresence(INITIAL_PRESENCE);
		}
	    }
	});

    }

    /**
     * Set the logged in user's presence. If the user is not logged in, the
     * presence is sent just after the initial presence
     * 
     * @see http://www.xmpp.org/rfcs/rfc3921.html#presence
     * 
     * @param presence
     */
    @Override
    public void changeOwnPresence(final Presence presence) {
	session.send(presence);
	setOwnPresence(presence);
    }

    /**
     * 5.1.5. Unavailable Presence (rfc 3921)
     * 
     * Before ending its session with a server, a client SHOULD gracefully
     * become unavailable by sending a final presence stanza that possesses no
     * 'to' attribute and that possesses a 'type' attribute whose value is
     * "unavailable" (optionally, the final presence stanza MAY contain one or
     * more <status/> elements specifying the reason why the user is no longer
     * available).
     * 
     * @param userURI
     */
    private void logOut(final XmppURI userURI) {
	final Presence presence = new Presence(Type.unavailable, userURI, null);
	session.send(presence);
	setOwnPresence(presence);
    }

    private void sendInitialPresence() {
	GWT.log("Sending initial presence");
	final Presence ownPresence = getOwnPresence();
	final Presence initialPresence = ownPresence != INITIAL_PRESENCE ? ownPresence : new Presence(session
		.getCurrentUser());
	session.send(initialPresence);
	setOwnPresence(initialPresence);
	session.setReady();

    }

}
