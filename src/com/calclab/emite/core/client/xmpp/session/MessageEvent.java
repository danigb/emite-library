package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.google.gwt.event.shared.GwtEvent;

public class MessageEvent extends GwtEvent<MessageHandler> {
    private static final Type<MessageHandler> TYPE = new Type<MessageHandler>();

    public static Type<MessageHandler> getType() {
	return TYPE;
    }

    private final Message message;

    public MessageEvent(final Message message) {
	this.message = message;
    }

    @Override
    public Type<MessageHandler> getAssociatedType() {
	return TYPE;
    }

    public Message getMessage() {
	return message;
    }

    @Override
    protected void dispatch(final MessageHandler handler) {
	handler.onMessage(this);
    }

}
