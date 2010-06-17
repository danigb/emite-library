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

import java.util.HashMap;
import java.util.Map;

import com.calclab.emite.core.client.events.MessageEvent;
import com.calclab.emite.core.client.events.MessageHandler;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.packet.NoPacket;
import com.calclab.emite.core.client.packet.PacketMatcher;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.xep.chatstate.client.ChatStateManager;
import com.calclab.emite.xep.chatstate.client.ChatStateManager.ChatState;
import com.calclab.emite.xep.chatstate.client.ChatStateManager.ChatUserState;
import com.calclab.emite.xep.muc.client.Room;
import com.calclab.suco.client.events.Listener2;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;

/**
 * XEP-0085: Chat State Notifications
 * http://www.xmpp.org/extensions/xep-0085.html (Version: 1.2)
 */
public class RoomChatStateManager {

    /*
     * REUSING ChatStateManager.ChatState
     * http://xmpp.org/extensions/xep-0085.html#bizrules-groupchat # A client
     * SHOULD NOT generate <gone/> notifications. # A client SHOULD ignore
     * <gone/> notifications received from other room occupants.
     */

    public static final String XMLNS = "http://jabber.org/protocol/chatstates";

    private String ownState;
    private final Map<XmppURI, String> othersState;
    private final Room room;
    private final int inactiveDelay = 120 * 1000; // 2 minutes
    private final int pauseDelay = 10 * 1000; // 10 secondes

    PacketMatcher bodySubjectThreadMatchter = new PacketMatcher() {
	public boolean matches(final IPacket packet) {
	    final String nn = packet.getName();
	    return "body".equals(nn) || "subject".equals(nn) || "thread".equals(nn);
	}
    };

    private final Timer inactiveTimer = new Timer() {
	@Override
	public void run() {
	    setOwnUserState(ChatUserState.inactive);
	}
    };

    private final Timer pauseTimer = new Timer() {
	@Override
	public void run() {
	    setOwnUserState(ChatUserState.pause);
	}
    };

    public RoomChatStateManager(final Room room) {
	this.room = room;
	othersState = new HashMap<XmppURI, String>();
	room.setData(RoomChatStateManager.class, this);

	room.addBeforeSendMessageHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		decorateOutcomingMessage(event.getMessage());
	    }
	});

	room.addMessageReceivedHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		final Message message = event.getMessage();
		final String state = ChatStateManager.getStateFromMessage(message);
		if (state != null) {
		    final XmppURI from = message.getFrom();
		    GWT.log("Received chat status " + state + " from " + from, null);
		    othersState.put(from, state);
		    room.getChatEventBus().fireEvent(new OccupantUserStateChangedEvent(message.getFrom(), state));
		}
	    }
	});

    }

    /**
     * Add a handler to know when a occupant user state has changed
     * 
     * @param handler
     *            the handler
     * @return a handler registration object to detach the given handler
     */
    public HandlerRegistration addOccupantUserStateChangedHandler(final OccupantUserStateChangedHandler handler) {
	return room.getChatEventBus().addHandler(OccupantUserStateChangedEvent.getType(), handler);
    }

    /**
     * @see getOtherUserState
     * @param occupantUri
     * @return
     */
    @Deprecated
    public ChatState getOtherState(final XmppURI occupantUri) {
	return ChatState.valueOf(getOtherUserState(occupantUri));
    }

    public String getOtherUserState(final XmppURI occupantUri) {
	final String state = othersState.get(occupantUri);
	return state == null ? ChatUserState.active : state;

    }

    @Deprecated
    public ChatState getOwnState() {
	return ownState != null ? ChatState.valueOf(ChatState.class, ownState) : null;
    }

    public String getOwnUserState() {
	return ownState;
    }

    /**
     * @see addOccupantUserStateChangedHandler
     * @param listener
     */
    @Deprecated
    public void onChatStateChanged(final Listener2<XmppURI, ChatState> listener) {
	addOccupantUserStateChangedHandler(new OccupantUserStateChangedHandler() {
	    @Override
	    public void onRoomUserStateChanged(final OccupantUserStateChangedEvent event) {
		final ChatState state = event.getState() != null ? ChatState.valueOf(event.getState()) : null;
		listener.onEvent(event.getFromUri(), state);
	    }
	});
    }

    @Deprecated
    public void setOwnState(final ChatState chatState) {
	setOwnUserState(chatState.toString());
    }

    public void setOwnUserState(final String state) {
	// From XEP: a client MUST NOT send a second instance of any given
	// standalone notification (i.e., a standalone notification MUST be
	// followed by a different state, not repetition of the same state).
	// However, every content message SHOULD contain an <active/>
	// notification.
	if (ownState == null || !ownState.equals(state)) {
	    ownState = state;
	    GWT.log("Setting own status to: " + state.toString(), null);
	    final Message message = new Message(null, room.getURI(), null);
	    message.addChild(state.toString(), XMLNS);
	    room.send(message);
	}
	if (ownState == ChatUserState.composing) {
	    pauseTimer.schedule(pauseDelay);
	}
    }

    /**
     * From http://xmpp.org/extensions/xep-0085.html#bizrules-syntax
     * <ul>
     * <li>A message stanza MUST NOT contain more than one child element
     * qualified by the 'http://jabber.org/protocol/chatstates' namespace.
     * 
     * <li>A message stanza that contains standard instant messaging content
     * SHOULD NOT contain a chat state notification extension other than
     * <active/>, where "standard instant messaging content" is taken to mean
     * the <body/>, <subject/>, and <thread/> child elements defined in XMPP IM
     * [7] or any other child element that would lead the recipient to treat the
     * stanza as an instant message as explained in Message Stanza Profiles [8].
     * 
     * <li>A message stanza that does not contain standard messaging content and
     * is intended to specify only the chat state MUST NOT contain any child
     * elements other than the chat state notification extension, which SHOULD
     * be a state other than <active/>; however, if threads are used (see below)
     * then the standalone notification MUST also contain the <thread/> element.
     * </ul>
     */
    private void decorateOutcomingMessage(final Message message) {
	final boolean alreadyWithState = ChatStateManager.getStateFromMessage(message) != null;
	if (!alreadyWithState && ownState != ChatUserState.active
		&& NoPacket.INSTANCE != message.getFirstChild(bodySubjectThreadMatchter)) {
	    if (ownState == ChatUserState.composing) {
		pauseTimer.cancel();
	    }

	    GWT.log("Setting own status to: " + ownState + " because we send a body or a subject", null);
	    ownState = ChatUserState.active;
	    message.addChild(ChatUserState.active, XMLNS);
	}
	if (ownState != ChatUserState.inactive) {
	    inactiveTimer.schedule(inactiveDelay);
	}
    }

}
