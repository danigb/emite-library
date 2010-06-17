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

import java.util.HashMap;

import com.calclab.emite.core.client.events.DefaultEmiteEventBus;
import com.calclab.emite.core.client.events.MessageEvent;
import com.calclab.emite.core.client.events.MessageHandler;
import com.calclab.emite.core.client.events.MessageReceivedEvent;
import com.calclab.emite.core.client.events.MessageSentEvent;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractChat implements Chat {

    protected final XmppURI uri;
    protected String state;
    private final XmppSession session;
    private final HashMap<Class<?>, Object> data;
    private final XmppURI starter;
    private final DefaultEmiteEventBus eventBus;

    public AbstractChat(final XmppSession session, final XmppURI uri, final XmppURI starter) {
	this.session = session;
	this.uri = uri;
	this.starter = starter;
	eventBus = new DefaultEmiteEventBus();
	data = new HashMap<Class<?>, Object>();
	state = ChatState.locked;
    }

    @Override
    public HandlerRegistration addBeforeReceiveMessageHandler(final MessageHandler handler) {
	return eventBus.addHandler(BeforeReceiveMessageEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addBeforeSendMessageHandler(final MessageHandler handler) {
	return eventBus.addHandler(BeforeSendMessageEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addMessageReceivedHandler(final MessageHandler handler) {
	return eventBus.addHandler(MessageReceivedEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addMessageSentHandler(final MessageHandler handler) {
	return eventBus.addHandler(MessageSentEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addStateChangedHandler(final StateChangedHandler handler) {
	return eventBus.addHandler(StateChangedEvent.getType(), handler);
    }

    @Override
    public String getChatState() {
	return state;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(final Class<T> type) {
	return (T) data.get(type);
    }

    @Deprecated
    public State getState() {
	if (state == ChatState.locked) {
	    return State.locked;
	} else {
	    return State.ready;
	}
    }

    public XmppURI getURI() {
	return uri;
    }

    public boolean isInitiatedByMe() {
	return starter.equals(session.getCurrentUser());
    }

    @Deprecated
    public void onBeforeReceive(final Listener<Message> listener) {
	addBeforeReceiveMessageHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		listener.onEvent(event.getMessage());
	    }
	});
    }

    @Deprecated
    public void onBeforeSend(final Listener<Message> listener) {
	addBeforeSendMessageHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		listener.onEvent(event.getMessage());
	    }
	});
    }

    @Deprecated
    public void onMessageReceived(final Listener<Message> listener) {
	addMessageReceivedHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		listener.onEvent(event.getMessage());
	    }
	});
    }

    @Deprecated
    public void onMessageSent(final Listener<Message> listener) {
	addMessageSentHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		listener.onEvent(event.getMessage());
	    }
	});
    }

    @Deprecated
    public void onStateChanged(final Listener<State> listener) {
	addStateChangedHandler(new StateChangedHandler() {
	    @Override
	    public void onStateChanged(final StateChangedEvent event) {
		listener.onEvent(getState());
	    }
	});
    }

    public void send(final Message message) {
	message.setFrom(session.getCurrentUser());
	eventBus.fireEvent(new BeforeSendMessageEvent(message));
	session.send(message);
	eventBus.fireEvent(new MessageSentEvent(message));
    }

    @Override
    public void setChatState(final String state) {
	if (this.state != state) {
	    this.state = state;
	    eventBus.fireEvent(new StateChangedEvent(state));
	}
    }

    @SuppressWarnings("unchecked")
    public <T> T setData(final Class<T> type, final T value) {
	return (T) data.put(type, value);
    }

    protected void receive(final Message message) {
	eventBus.fireEvent(new BeforeReceiveMessageEvent(message));
	eventBus.fireEvent(new MessageReceivedEvent(message));
    }

    @Deprecated
    protected void setState(final State state) {
	if (State.locked == state) {
	    setChatState(ChatState.locked);
	} else {
	    setChatState(ChatState.ready);
	}
    }
}
