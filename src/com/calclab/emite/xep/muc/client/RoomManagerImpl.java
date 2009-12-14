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

import java.util.HashMap;

import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.packet.MatcherFactory;
import com.calclab.emite.core.client.packet.NoPacket;
import com.calclab.emite.core.client.packet.PacketMatcher;
import com.calclab.emite.core.client.xmpp.session.Session;
import com.calclab.emite.core.client.xmpp.stanzas.BasicStanza;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.Stanza;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.im.client.chat.PairChatManager;
import com.calclab.emite.im.client.chat.Chat;
import com.calclab.suco.client.events.Event;
import com.calclab.suco.client.events.Listener;

public class RoomManagerImpl extends PairChatManager implements RoomManager {

    private static final PacketMatcher FILTER_X = MatcherFactory.byNameAndXMLNS("x",
	    "http://jabber.org/protocol/muc#user");
    private static final PacketMatcher FILTER_INVITE = MatcherFactory.byName("invite");
    private final HashMap<XmppURI, Room> rooms;
    private final Event<RoomInvitation> onInvitationReceived;

    public RoomManagerImpl(final Session session) {
	super(session);
	this.onInvitationReceived = new Event<RoomInvitation>("roomManager:onInvitationReceived");
	this.rooms = new HashMap<XmppURI, Room>();

    }

    @Override
    public void close(final Chat whatToClose) {
	final Room room = rooms.remove(whatToClose.getURI().getJID());
	if (room != null) {
	    room.close();
	    super.close(room);
	}
    }

    public void onInvitationReceived(final Listener<RoomInvitation> listener) {
	onInvitationReceived.add(listener);
    }

    @Override
    public Room open(final XmppURI roomURI) {
	Room room = rooms.get(roomURI.getJID());
	if (room == null) {
	    room = new Room(session, roomURI, session.getCurrentUser());
	    rooms.put(roomURI.getJID(), room);
	    chats.add(room);
	    onChatCreated.fire(room);
	}
	return room;
    }

    @Override
    protected void eventMessage(final Message message) {
	IPacket child;
	if (message.getType() == Message.Type.groupchat) {
	    final Room room = rooms.get(message.getFrom().getJID());
	    if (room != null) {
		room.receive(message);
	    }
	} else if ((child = message.getFirstChild(FILTER_X).getFirstChild(FILTER_INVITE)) != NoPacket.INSTANCE) {
	    handleRoomInvitation(message.getFrom(), new BasicStanza(child));
	}

    }

    private void handleRoomInvitation(final XmppURI roomURI, final Stanza invitation) {
	onInvitationReceived.fire(new RoomInvitation(invitation.getFrom(), roomURI, invitation.getFirstChild("reason")
		.getText()));
    }

}
