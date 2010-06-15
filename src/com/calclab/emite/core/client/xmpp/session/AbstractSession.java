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
package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.bus.EmiteEventBus;
import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Session event plumbing.
 */
public abstract class AbstractSession extends AbstractXmppSession implements Session {

    public AbstractSession(final EmiteEventBus eventBus) {
	super(eventBus);

    }

    @Override
    public HandlerRegistration addSessionStateChangedHandler(final StateChangedHandler handler) {
	return eventBus.addHandler(StateChangedEvent.getType(), handler);
    }

    public void login(final XmppURI uri, final String password) {
	login(new Credentials(uri, password, Credentials.ENCODING_NONE));
    }

    @Override
    public void onIQ(final Listener<IQ> listener) {
	addIQHandler(new IQHandler() {
	    @Override
	    public void onIQ(final IQEvent event) {
		listener.onEvent(event.getIQ());
	    }
	});
    }

    public void onMessage(final Listener<Message> listener) {
	addMessageHandler(new MessageHandler() {
	    @Override
	    public void onMessage(final MessageEvent event) {
		listener.onEvent(event.getMessage());
	    }
	});
    }

    public void onPresence(final Listener<Presence> listener) {
	addPresenceHandler(new PresenceHandler() {
	    @Override
	    public void onPresence(final PresenceEvent event) {
		listener.onEvent(event.getPresence());
	    }
	});
    }

    public void onStateChanged(final Listener<Session> listener) {
	addSessionStateChangedHandler(new StateChangedHandler() {
	    @Override
	    public void onStateChanged(final StateChangedEvent event) {
		listener.onEvent(AbstractSession.this);
	    }
	});
    }

    protected void fireIQ(final IQ iq) {
	eventBus.fireEvent(new IQEvent(iq));
    }

    protected void fireMessage(final Message message) {
	eventBus.fireEvent(new MessageEvent(message));
    }

    protected void firePresence(final Presence presence) {
	eventBus.fireEvent(new PresenceEvent(presence));
    }

}
