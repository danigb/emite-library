package com.calclab.emite.im.client.chat;

import java.util.Collection;
import java.util.HashSet;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.im.client.chat.ChatChangedEvent.ChatChange;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractChatManager implements ChatManager {
    private final HashSet<Chat> chats;
    protected final XmppSession session;
    private final EmiteEventBus eventBus;

    public AbstractChatManager(final EmiteEventBus eventBus, final XmppSession session) {
	this.eventBus = eventBus;
	this.session = session;
	chats = new HashSet<Chat>();
    }

    @Override
    public HandlerRegistration addChatChangedHandler(final ChatChangedHandler handler) {
	return eventBus.addHandler(ChatChangedEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addChatClosedHandler(final ChatChangedHandler handler) {
	return eventBus.addHandler(ChatChangedEvent.getType(), new ChatChangedHandler() {
	    @Override
	    public void onChatChanged(final ChatChangedEvent event) {
		if (event.is(ChatChange.closed)) {
		    handler.onChatChanged(event);
		}
	    }
	});
    }

    @Override
    public HandlerRegistration addChatCreatedHandler(final ChatChangedHandler handler) {
	return eventBus.addHandler(ChatChangedEvent.getType(), new ChatChangedHandler() {
	    @Override
	    public void onChatChanged(final ChatChangedEvent event) {
		if (event.is(ChatChange.created)) {
		    handler.onChatChanged(event);
		}
	    }
	});
    }

    @Override
    public HandlerRegistration addChatOpenedHandler(final ChatChangedHandler handler) {
	return eventBus.addHandler(ChatChangedEvent.getType(), new ChatChangedHandler() {
	    @Override
	    public void onChatChanged(final ChatChangedEvent event) {
		{
		    if (event.is(ChatChange.opened)) {
			handler.onChatChanged(event);
		    }
		}
	    }
	});
    }

    @Override
    public void close(final Chat chat) {
	getChats().remove(chat);
	fireChatClosed(chat);
    }

    public abstract Chat getChat(XmppURI uri);

    public Collection<? extends Chat> getChats() {
	return chats;
    }

    public void onChatClosed(final Listener<Chat> listener) {
	addChatChangedHandler(new ChatChangedHandler() {
	    @Override
	    public void onChatChanged(final ChatChangedEvent event) {
		if (event.is(ChatChange.closed)) {
		    listener.onEvent(event.getChat());
		}
	    }
	});
    }

    public void onChatCreated(final Listener<Chat> listener) {
	addChatChangedHandler(new ChatChangedHandler() {
	    @Override
	    public void onChatChanged(final ChatChangedEvent event) {
		if (event.is(ChatChange.created)) {
		    listener.onEvent(event.getChat());
		}
	    }
	});
    }

    public void onChatOpened(final Listener<Chat> listener) {
	addChatChangedHandler(new ChatChangedHandler() {
	    @Override
	    public void onChatChanged(final ChatChangedEvent event) {
		if (event.is(ChatChange.opened)) {
		    listener.onEvent(event.getChat());
		}
	    }
	});
    }

    public Chat open(final XmppURI uri) {
	Chat chat = getChat(uri);
	if (chat == null) {
	    chat = createChat(uri, session.getCurrentUser());
	    addChat(chat);
	    fireChatCreated(chat);
	}
	fireChatOpened(chat);
	return chat;
    }

    protected void addChat(final Chat chat) {
	chats.add(chat);
    }

    protected abstract Chat createChat(XmppURI uri, XmppURI currentUser);

    protected void fireChatClosed(final Chat chat) {
	eventBus.fireEvent(new ChatChangedEvent(ChatChange.closed, chat));
    }

    protected void fireChatCreated(final Chat chat) {
	eventBus.fireEvent(new ChatChangedEvent(ChatChange.created, chat));
    }

    protected void fireChatOpened(final Chat chat) {
	eventBus.fireEvent(new ChatChangedEvent(ChatChange.opened, chat));
    }
}
