package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.google.gwt.event.shared.GwtEvent;

public class IncomingMessageEvent extends GwtEvent<IncomingMessageHandler> {
    private static final Type<IncomingMessageHandler> TYPE = new Type<IncomingMessageHandler>();

    public static Type<IncomingMessageHandler> getType() {
	return TYPE;
    }

    private final Message message;

    public IncomingMessageEvent(final Message message) {
	this.message = message;
    }

    @Override
    public Type<IncomingMessageHandler> getAssociatedType() {
	return TYPE;
    }

    public Message getMessage() {
	return message;
    }

    @Override
    protected void dispatch(final IncomingMessageHandler handler) {
	handler.onIncomingMessage(this);
    }

}
