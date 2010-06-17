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

import com.calclab.emite.core.client.events.MessageEvent;
import com.calclab.emite.core.client.events.MessageHandler;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.session.XmppSession.SessionState;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.core.client.xmpp.stanzas.Message.Type;

/**
 * <p>
 * Default Chat implementation. Use Chat interface instead
 * </p>
 * 
 * <p>
 * About Chat ids: Other sender Uri plus thread identifies a chat (associated
 * with a chat panel in the UI). If no thread is specified, we join all messages
 * in one chat.
 * </p>
 * 
 * @see Chat
 */
public class PairChat extends AbstractChat {
    protected final String thread;
    private final String id;
    private XmppURI user;

    PairChat(final XmppSession session, final XmppURI other, final XmppURI starter, final String thread) {
	super(session, other, starter);
	this.thread = thread;
	id = generateChatID();

	setStateFromSessionState(session);

	session.addIncomingMessageHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		final Message message = event.getMessage();
		final XmppURI from = message.getFrom();
		if (from.equalsNoResource(uri)) {
		    receive(message);
		}
	    }
	});

	session.addStateChangedHandler(new StateChangedHandler() {
	    @Override
	    public void onStateChanged(final StateChangedEvent event) {
		setStateFromSessionState(session);
	    }
	});

    }

    @Override
    public boolean equals(final Object obj) {
	if (obj == null) {
	    return false;
	}
	if (this == obj) {
	    return true;
	}
	final PairChat other = (PairChat) obj;
	return id.equals(other.id);
    }

    public String getID() {
	return id;
    }

    public String getThread() {
	return thread;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (uri == null ? 0 : uri.hashCode());
	result = prime * result + (thread == null ? 0 : thread.hashCode());
	return result;
    }

    @Override
    public void send(final Message message) {
	message.setThread(thread);
	message.setType(Type.chat);
	message.setTo(uri);
	super.send(message);
    }

    @Override
    public String toString() {
	return id;
    }

    private String generateChatID() {
	return "chat: " + uri.toString() + "-" + thread;
    }

    private void setStateFromSessionState(final XmppSession session) {
	final String state = session.getSessionState();

	if (state == SessionState.loggedIn || state == SessionState.ready) {
	    final XmppURI currentUser = session.getCurrentUser();
	    if (user == null) {
		user = currentUser;
	    }
	    setChatState(currentUser.equalsNoResource(user) ? ChatState.ready : ChatState.locked);

	} else {
	    setChatState(ChatState.locked);
	}

    }

}
