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

import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.im.client.chat.Chat;
import com.calclab.suco.client.events.Event;
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

    private ChatState ownState;
    private ChatState otherState;
    private final Chat chat;
    private NegotiationStatus negotiationStatus;
    private final Event<ChatState> onChatStateChanged;

    final Listener<Message> doBeforeSend = new Listener<Message>() {
	public void onEvent(final Message message) {
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
    };

    public ChatStateManager(final Chat chat) {
	this.chat = chat;
	onChatStateChanged = new Event<ChatState>("chatStateManager:onChatStateChanged");
	negotiationStatus = NegotiationStatus.notStarted;
	chat.onMessageReceived(new Listener<Message>() {
	    public void onEvent(final Message message) {
		onMessageReceived(chat, message);
	    }
	});
    }

    public NegotiationStatus getNegotiationStatus() {
	return negotiationStatus;
    }

    public ChatState getOtherState() {
	return otherState;
    }

    public ChatState getOwnState() {
	return ownState;
    }

    public void onChatStateChanged(final Listener<ChatState> listener) {
	onChatStateChanged.add(listener);
    }

    public void setOwnState(final ChatState chatState) {
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

    private void sendStateMessage(final ChatState chatState) {
	final Message message = new Message(null, chat.getURI(), null);
	message.addChild(chatState.toString(), XMLNS);
	chat.send(message);
    }

    protected void onMessageReceived(final Chat chat, final Message message) {
	for (int i = 0; i < ChatState.values().length; i++) {
	    final ChatState chatState = ChatState.values()[i];
	    final String typeSt = chatState.toString();
	    if (message.hasChild(typeSt) || message.hasChild("cha:" + typeSt)) {
		otherState = chatState;
		if (negotiationStatus.equals(NegotiationStatus.notStarted)) {
		    sendStateMessage(ChatState.active);
		}
		if (chatState.equals(ChatState.gone)) {
		    negotiationStatus = NegotiationStatus.notStarted;
		} else {
		    negotiationStatus = NegotiationStatus.accepted;
		}
		GWT.log("Receiver other chat status: " + typeSt, null);
		onChatStateChanged.fire(chatState);
	    }
	}
    }
}
