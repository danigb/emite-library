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

import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.suco.client.events.Listener;

/**
 * The most important object in Xmpp emite module. You can login, send and
 * receive stanzas. It also allows you to pause and resume the session.
 */
@Deprecated
public interface Session extends XmppSession {

    /**
     * The given listener is called when a IQ <b>of type 'get' or 'set'</b> is
     * received
     * 
     * @param listener
     */
    public abstract void onIQ(Listener<IQ> listener);

    /**
     * The given listener is called when a message stanza has arrived
     * 
     * @param listener
     * 
     */
    public abstract void onMessage(final Listener<Message> listener);

    /**
     * The given listener is called when a presence stanza has arrived
     * 
     * @param listener
     */
    public abstract void onPresence(final Listener<Presence> listener);

    /**
     * The given listener is called when the session changed it's state
     * 
     * @param listener
     */
    public abstract void onStateChanged(final Listener<Session> listener);

}
