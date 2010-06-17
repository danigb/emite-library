/*
 *
 * ((e)) emite: A pure gwt (Google Web Toolkit) xmpp (jabber) library
 *
 * (c) 2008-2010 The emite development team (see CREDITS for details)
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
package com.calclab.emite.xep.mucchatstate.client;

import com.calclab.emite.im.client.chat.ChatChangedEvent;
import com.calclab.emite.im.client.chat.ChatChangedHandler;
import com.calclab.emite.xep.muc.client.Room;
import com.calclab.emite.xep.muc.client.RoomManager;
import com.google.gwt.core.client.GWT;

/**
 * XEP-0085: Chat State Notifications
 * http://www.xmpp.org/extensions/xep-0085.html (Version: 1.2)
 * 
 * Chat state in MUC rooms. Note: Use of chat state notifications in the context
 * of groupchat can result in multicasting of such notifications. Forewarned is
 * forearmed.
 * 
 */
public class MUCChatStateManager {

    public MUCChatStateManager(final RoomManager chatManager) {

	chatManager.addChatChangedHandler(new ChatChangedHandler() {
	    @Override
	    public void onChatChanged(final ChatChangedEvent event) {
		if (event.isCreated()) {
		    getRoomOccupantsChatStateManager((Room) event.getChat());
		} else if (event.isClosed()) {
		    event.getChat().setData(RoomChatStateManager.class, null);
		}
	    }
	});
    }

    public RoomChatStateManager getRoomOccupantsChatStateManager(final Room room) {
	RoomChatStateManager stateManager = room.getData(RoomChatStateManager.class);
	if (stateManager == null) {
	    stateManager = createChatState(room);
	}
	return stateManager;
    }

    private RoomChatStateManager createChatState(final Room room) {
	GWT.log("Adding chat state to chat: " + room.getID(), null);
	final RoomChatStateManager stateManager = new RoomChatStateManager(room);
	return stateManager;
    }

}
