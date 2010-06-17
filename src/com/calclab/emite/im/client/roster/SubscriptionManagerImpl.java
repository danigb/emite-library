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
package com.calclab.emite.im.client.roster;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.events.PresenceEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.packet.MatcherFactory;
import com.calclab.emite.core.client.packet.PacketMatcher;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.core.client.xmpp.stanzas.Presence.Type;
import com.calclab.suco.client.events.Listener;
import com.calclab.suco.client.events.Listener2;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @see SubscriptionManager
 */
public class SubscriptionManagerImpl implements SubscriptionManager {
    protected static final PacketMatcher FILTER_NICK = MatcherFactory.byNameAndXMLNS("nick",
	    "http://jabber.org/protocol/nick");
    private final XmppSession session;
    private final Roster roster;
    private final EmiteEventBus eventBus;

    public SubscriptionManagerImpl(final EmiteEventBus eventBus, final XmppSession session, final Roster roster) {
	this.eventBus = eventBus;
	this.session = session;
	this.roster = roster;

	session.addIncomingPresenceHandler(new PresenceHandler() {
	    @Override
	    public void onIncomingPresence(final PresenceEvent event) {
		final Presence presence = event.getPresence();
		{
		    if (presence.getType() == Type.subscribe) {
			final IPacket nick = presence.getFirstChild(FILTER_NICK);
			eventBus.fireEvent(new SubscriptionRequestedEvent(presence.getFrom(), nick.getText()));
		    }
		}
	    }
	});

	roster.onItemAdded(new Listener<RosterItem>() {
	    public void onEvent(final RosterItem item) {
		if (item.getSubscriptionState() == SubscriptionState.none) {
		    // && item.getAsk() == Type.subscribe) {
		    requestSubscribe(item.getJID());
		    item.setSubscriptionState(SubscriptionState.nonePendingIn);
		} else if (item.getSubscriptionState() == SubscriptionState.from) {
		    approveSubscriptionRequest(item.getJID(), item.getName());
		    item.setSubscriptionState(SubscriptionState.fromPendingOut);
		}
	    }
	});
    }

    public HandlerRegistration addSubscriptionRequestedHandler(final SubscriptionRequestedHandler handler) {
	return eventBus.addHandler(SubscriptionRequestedEvent.getType(), handler);
    }

    public void approveSubscriptionRequest(final XmppURI jid, String nick) {
	nick = nick != null ? nick : jid.getNode();
	final RosterItem item = roster.getItemByJID(jid);
	if (item == null) {
	    // add the item to the roster
	    roster.requestAddItem(jid, nick);
	    // request a subscription to that entity of the roster
	    requestSubscribe(jid);
	}
	// answer "subscribed" to the subscrition request
	session.send(new Presence(Type.subscribed, session.getCurrentUser(), jid.getJID()));
    }

    public void cancelSubscription(final XmppURI jid) {
	session.send(new Presence(Type.unsubscribed, session.getCurrentUser(), jid.getJID()));
    }

    public void onSubscriptionRequested(final Listener2<XmppURI, String> listener) {
	addSubscriptionRequestedHandler(new SubscriptionRequestedHandler() {
	    @Override
	    public void onSubscriptionRequested(final SubscriptionRequestedEvent event) {
		listener.onEvent(event.getFromUri(), event.getNickName());
	    }
	});
    }

    public void refuseSubscriptionRequest(final XmppURI jid) {
	session.send(new Presence(Type.unsubscribed, session.getCurrentUser(), jid.getJID()));
    }

    public void requestSubscribe(final XmppURI jid) {
	session.send(new Presence(Type.subscribe, session.getCurrentUser(), jid.getJID()));
    }

    public void unsubscribe(final XmppURI jid) {
	session.send(new Presence(Type.unsubscribe, session.getCurrentUser(), jid.getJID()));
    }

}
