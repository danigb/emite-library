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
package com.calclab.emite.xep.muc.client;

import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.im.client.chat.ChatManager;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * RoomManager: a ChatManager extended with room related methods. All the XXChat
 * methods inherit from ChatManager are Rooms in the RoomManager context.
 * 
 * @see ChatManager
 */
public interface RoomManager extends ChatManager {
    /**
     * Accept a room invitation. A new room is created (use addChatOpenHandler
     * to know when the room is created)
     * 
     * @param invitation
     *            the invitation to be accepted
     */
    void acceptRoomInvitation(RoomInvitation invitation);

    /**
     * Add a handler to know when a room invitation has arrived. Use the
     * acceptRoomInvitation to enter the room
     * 
     * @param handler
     *            the handler
     * @return a handler registration object to detach the handler.
     */
    HandlerRegistration addRoomInvitationReceievedHandler(RoomInvitationHandler handler);

    /**
     * Obtain the history options
     * 
     * @return
     */
    HistoryOptions getDefaultHistoryOptions();

    /**
     * Notify when a room invitation arrives
     * 
     * @param listener
     *            the listener to be informed
     */
    void onInvitationReceived(Listener<RoomInvitation> listener);

    /**
     * Open a new room (chat) with the given history options
     * 
     * @param uri
     *            the uri of the room to be open
     * @param historyOptions
     *            the history options to initialize this room
     * @return the opened room
     */
    Room open(final XmppURI uri, HistoryOptions historyOptions);

    /**
     * Change the default history options
     * 
     * @param historyOptions
     */
    void setDefaultHistoryOptions(HistoryOptions historyOptions);

}
