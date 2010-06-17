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

import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Handles the user's presence state. Provides event to know when it changes.
 * 
 * Its responsable of send the initial presence after session login.
 * 
 * @see http://xmpp.org/rfcs/rfc3921.html#presence
 */
public interface PresenceManager {
    /**
     * Add a handler to know when the current user's presence changed
     * 
     * @param handler
     *            the handler to be added
     * @return a handle registration object to detach the handler if needed
     */
    HandlerRegistration addOwnPresenceChangedHandler(PresenceHandler handler);

    /**
     * Change the current user presence
     */
    void changeOwnPresence(Presence presence);

    /**
     * Get the current user's presence
     * 
     * @return
     */
    Presence getOwnPresence();

    /**
     * Add a litener to the own's-presence-changed event
     * 
     * @see addOwnPresenceChangedHandler
     * @param listener
     */
    @Deprecated
    void onOwnPresenceChanged(Listener<Presence> listener);
}
