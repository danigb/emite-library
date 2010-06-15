package com.calclab.emite.im.client.chat;

import com.google.gwt.event.shared.GwtEvent;

public class ChatChangedEvent extends GwtEvent<ChatChangedHandler> {

    public static enum ChatChange {
	opened, closed, created
    }

    private static final Type<ChatChangedHandler> TYPE = new Type<ChatChangedHandler>();

    public static Type<ChatChangedHandler> getType() {
	return TYPE;
    }

    private final Chat chat;
    private final ChatChange change;

    public ChatChangedEvent(final ChatChange change, final Chat chat) {
	assert change != null : "ChatChange can't be null in ChatChangedEvent";
	assert chat != null : "Chat can't be null in ChatChangedEvent";
	this.change = change;
	this.chat = chat;
    }

    @Override
    public Type<ChatChangedHandler> getAssociatedType() {
	return TYPE;
    }

    public ChatChange getChange() {
	return change;
    }

    public Chat getChat() {
	return chat;
    }

    public boolean is(final ChatChange change) {
	return this.change == change;
    }

    @Override
    protected void dispatch(final ChatChangedHandler handler) {
	handler.onChatChanged(this);
    }

}
