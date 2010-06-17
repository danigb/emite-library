package com.calclab.emite.im.client.chat;

import com.calclab.emite.core.client.events.ChangedEvent;

public class ChatChangedEvent extends ChangedEvent<ChatChangedHandler> {

    private static final Type<ChatChangedHandler> TYPE = new Type<ChatChangedHandler>();

    public static Type<ChatChangedHandler> getType() {
	return TYPE;
    }

    private final Chat chat;

    public ChatChangedEvent(final String changeType, final Chat chat) {
	super(TYPE, changeType);
	assert chat != null : "Chat can't be null in ChatChangedEvent";
	this.chat = chat;
    }

    public Chat getChat() {
	return chat;
    }

    @Override
    protected void dispatch(final ChatChangedHandler handler) {
	handler.onChatChanged(this);
    }

}
