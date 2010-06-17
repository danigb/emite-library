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
package com.calclab.emite.xep.chatstate.client;

import com.calclab.emite.core.client.events.MessageEvent;
import com.calclab.emite.core.client.events.MessageHandler;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.packet.NoPacket;
import com.calclab.emite.core.client.packet.PacketMatcher;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.im.client.chat.Chat;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.core.client.GWT;

/**
 * XEP-0085: Chat State Notifications
 * 
 * An XMPP protocol extension for communicating the status of a user in a chat
 * session, thus indicating whether a chat partner is actively engaged in the
 * chat, composing a message, temporarily paused, inactive, or gone. The
 * protocol can be used in the context of a one-to-one chat session or a
 * multi-user chat room.
 * 
 * http://www.xmpp.org/extensions/xep-0085.html (Version: 1.2)
 */
public class ChatStateManager {
    /**
     * GTW handles better Strings than enums... Also strings are extensible
     * 
     * @see ChatUserState
     */
    @Deprecated
    public static enum ChatState {
	active, composing, pause, inactive, gone
    }

    /**
     * Possible (and extensible) chat user states.
     * 
     * In essence, chat state notifications can be thought of as a form of
     * chat-specific presence
     * 
     */
    public static class ChatUserState {
	/**
	 * User is actively participating in the chat session.
	 */
	public static final String active = "active";
	/**
	 * User is composing a message.
	 */
	public static final String composing = "composing";
	/**
	 * User had been composing but now has stopped.
	 */
	public static final String pause = "pause";
	/**
	 * User has not been actively participating in the chat session.
	 */
	public static final String inactive = "inactive";
	/**
	 * User has effectively ended their participation in the chat session.
	 */
	public static final String gone = "gone";
    }

    public static enum NegotiationStatus {
	notStarted, started, rejected, accepted
    }

    public static final String XMLNS = "http://jabber.org/protocol/chatstates";
    private static final PacketMatcher CHATSTATES_FILTER = new PacketMatcher() {
	@Override
	public boolean matches(final IPacket packet) {
	    final String xmlns = packet.getAttribute("xmlns");
	    return xmlns != null && xmlns.equals(XMLNS);
	}
    };

    private String ownState;
    private String otherState;
    private final Chat chat;
    private NegotiationStatus negotiationStatus;

    public ChatStateManager(final Chat chat) {
	this.chat = chat;
	negotiationStatus = NegotiationStatus.notStarted;

	chat.setData(ChatStateManager.class, this);
	chat.addBeforeSendMessageHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		decorateMessage(event.getMessage());
	    }
	});

	chat.addMessageReceivedHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		onMessageReceived(chat, event.getMessage());
	    }
	});

    }

    public void addChatUserStateChangedHandler(final StateChangedHandler handler) {
	chat.getChatEventBus().addHandler(ChatUserStateChangedEvent.getType(), handler);
    }

    public NegotiationStatus getNegotiationStatus() {
	return negotiationStatus;
    }

    @Deprecated
    public ChatState getOtherState() {
	return otherState != null ? ChatState.valueOf(ChatState.class, otherState) : null;
    }

    public String getOtherUserState() {
	return otherState;
    }

    @Deprecated
    public ChatState getOwnState() {
	return ownState != null ? ChatState.valueOf(ChatState.class, ownState) : null;
    }

    public String getOwnUserState() {
	return ownState;
    }

    @Deprecated
    public void onChatStateChanged(final Listener<ChatState> listener) {
	addChatUserStateChangedHandler(new StateChangedHandler() {
	    @Override
	    public void onStateChanged(final StateChangedEvent event) {
		listener.onEvent(getOtherState());
	    }

	});
    }

    public void setOwnChatUserState(final String chatState) {
	// From XEP: a client MUST NOT send a second instance of any given
	// standalone notification (i.e., a standalone notification MUST be
	// followed by a different state, not repetition of the same state).
	// However, every content message SHOULD contain an <active/>
	// notification.
	if (negotiationStatus.equals(NegotiationStatus.accepted)) {
	    if (ownState == null || !ownState.equals(chatState)) {
		ownState = chatState;
		GWT.log("Setting own status to: " + chatState.toString(), null);
		sendStateMessage(chatState);
	    }
	}
    }

    /**
     * @see setOwnChatUserState
     * @param chatState
     */
    @Deprecated
    public void setOwnState(final ChatState chatState) {
	setOwnChatUserState(chatState.toString());
    }

    private void decorateMessage(final Message message) {
	switch (negotiationStatus) {
	case notStarted:
	    negotiationStatus = NegotiationStatus.started;
	case accepted:
	    boolean alreadyWithState = false;
	    for (int i = 0; i < ChatState.values().length; i++) {
		if (message.hasChild(ChatState.values()[i].toString())) {
		    alreadyWithState = true;
		}
	    }
	    if (!alreadyWithState) {
		message.addChild(ChatState.active.toString(), XMLNS);
	    }
	    break;
	case rejected:
	case started:
	    // do nothing
	    break;
	}
    }

    private void sendStateMessage(final String chatState) {
	final Message message = new Message(null, chat.getURI(), null);
	message.addChild(chatState, XMLNS);
	chat.send(message);
    }

    protected void onMessageReceived(final Chat chat, final Message message) {
	final IPacket stateChild = message.getFirstChild(CHATSTATES_FILTER);
	if (stateChild != NoPacket.INSTANCE) {
	    String state = stateChild.getName();
	    if (state.startsWith("cha:")) {
		state = state.substring(4);
	    }
	    otherState = state;
	    if (negotiationStatus.equals(NegotiationStatus.notStarted)) {
		sendStateMessage(ChatUserState.active);
	    }
	    if (otherState.equals(ChatUserState.gone)) {
		negotiationStatus = NegotiationStatus.notStarted;
	    } else {
		negotiationStatus = NegotiationStatus.accepted;
	    }
	    chat.getChatEventBus().fireEvent(new ChatUserStateChangedEvent(otherState));
	}
    }
}
