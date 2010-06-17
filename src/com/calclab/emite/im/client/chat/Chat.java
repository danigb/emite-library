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
package com.calclab.emite.im.client.chat;

import com.calclab.emite.core.client.events.MessageHandler;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Defines a xmpp chat.
 * 
 * This interface is implemented by PairChat and Room.
 * 
 * @see PairChat, Room
 */
public interface Chat {

    /**
     * Possible (and extensible) chat conversation states
     */
    public static class ChatState {
	public static final String ready = "ready";
	public static final String locked = "locked";
    }

    /**
     * Possible conversation states.
     */
    @Deprecated
    public static enum State {
	ready, locked
    }

    /**
     * Add a handler to know when a message is received. It allows the listener
     * to modify the message just before the receive event (a kind of
     * interceptor in aop programming)
     * 
     * @param handler
     *            the message handler
     */
    public HandlerRegistration addBeforeReceiveMessageHandler(MessageHandler handler);

    /**
     * A a handler to know when a message is going to be sent. It allows the
     * listener to modify the message just before send it (a kind of interceptor
     * in aop programming)
     * 
     * @param handler
     *            the message handeler
     */
    public HandlerRegistration addBeforeSendMessageHandler(MessageHandler handler);

    /**
     * Add a handler to know when a message is received in this chat
     * 
     * @param handler
     * @return a handler registration object to detach the handler
     */
    public HandlerRegistration addMessageReceivedHandler(MessageHandler handler);

    /**
     * Add a handler to know when this chat has sent a message
     * 
     * @param handler
     *            the message handler
     * @return a handler registration object to detach the handler
     * 
     */
    public HandlerRegistration addMessageSentHandler(MessageHandler handler);

    /**
     * Get the current chat state
     * 
     * @return the current chat state
     */
    public String getChatState();

    /**
     * Get the associated object of class 'type'
     * 
     * @param <T>
     *            the class (key) of the associated object
     * @param type
     *            the class object itself
     * @return the associated object if any, null otherwise
     * @see setData
     */
    public <T> T getData(Class<T> type);

    public String getID();

    /**
     * @see getChatState
     * @return
     */
    @Deprecated
    public State getState();

    /**
     * Returns this conversation URI. If this conversation is a normal chat, the
     * uri is the JID of the other side user. If this conversation is a room,
     * the uri is a room URI in the form of
     * roomName@domainOfRoomService/userNickName
     * 
     * @return the conversation's URI
     */
    public XmppURI getURI();

    /**
     * Allows to know if a chat is initiated by the current user
     * 
     * @return Return true if you started the conversation. False otherwise
     */
    public boolean isInitiatedByMe();

    public void onBeforeReceive(Listener<Message> listener);

    /**
     * A listener to know when a message is going to be sent. It allows the
     * listener to modify the message just before send it (a kind of interceptor
     * in aop programming)
     * 
     * @see addBeforeSendMessageHandler
     * @param listener
     *            the listener
     */
    @Deprecated
    public void onBeforeSend(Listener<Message> listener);

    /**
     * @see addMessageReceivedHandler
     * @param listener
     */
    @Deprecated
    public void onMessageReceived(Listener<Message> listener);

    /**
     * @see addMessageSentHandler
     * @param listener
     */
    @Deprecated
    public void onMessageSent(Listener<Message> listener);

    /**
     * Add a listener to know when the chat state changed. You should send
     * messages while the state != ready
     * 
     * @param listener
     */
    public void onStateChanged(Listener<State> listener);

    /**
     * To make this chat send a message
     * 
     * @param message
     *            the message
     * @throws RuntimeException
     *             if chat state != ready
     */
    public void send(Message message);

    /**
     * Changes the current chat state. If will fire a StateChangedEvent
     * 
     * @param the
     *            new state
     */
    public void setChatState(String state);

    /**
     * Associate a object to this conversation.
     * 
     * @param <T>
     *            the class of the object
     * @param type
     *            the class object itself
     * @param data
     *            the object you want to associate
     * @return the object associated
     * @see getData
     */
    public <T> T setData(Class<T> type, T data);

    /**
     * Add a handler to know when the chat state changes
     * 
     * @param handler
     *            the handler
     * @return a handle registration object to remove this handler
     */
    HandlerRegistration addStateChangedHandler(StateChangedHandler handler);

}
