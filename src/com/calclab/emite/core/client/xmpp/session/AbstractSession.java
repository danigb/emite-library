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

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.events.IQEvent;
import com.calclab.emite.core.client.events.IQHandler;
import com.calclab.emite.core.client.events.MessageEvent;
import com.calclab.emite.core.client.events.MessageHandler;
import com.calclab.emite.core.client.events.PresenceEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.suco.client.events.Listener;

/**
 * Session event plumbing.
 */
@Deprecated
public abstract class AbstractSession extends AbstractXmppSession implements Session {

    public AbstractSession(final EmiteEventBus eventBus) {
	super(eventBus);
    }

    @Override
    public void onIQ(final Listener<IQ> listener) {
	addIncomingIQHandler(new IQHandler() {
	    @Override
	    public void onPacket(final IQEvent event) {
		listener.onEvent(event.getIQ());
	    }
	});
    }

    public void onMessage(final Listener<Message> listener) {
	addIncomingMessageHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		listener.onEvent(event.getMessage());
	    }

	});
    }

    public void onPresence(final Listener<Presence> listener) {
	addIncomingPresenceHandler(new PresenceHandler() {
	    @Override
	    public void onIncomingPresence(final PresenceEvent event) {
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

    public void sendIQ(final String category, final IQ iq, final Listener<IPacket> listener) {
	sendIQ(category, iq, new IQResponseHandler() {
	    @Override
	    public void onIQ(final IQ iq) {
		listener.onEvent(iq);
	    }
	});
    }

}
