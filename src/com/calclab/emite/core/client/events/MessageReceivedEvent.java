package com.calclab.emite.core.client.events;

import com.calclab.emite.core.client.xmpp.stanzas.Message;

public class MessageReceivedEvent extends MessageEvent {
    private static final Type<MessageHandler> TYPE = new Type<MessageHandler>();

    public static Type<MessageHandler> getType() {
	return TYPE;
    }

    public MessageReceivedEvent(final Message message) {
	super(TYPE, message);
    }

}
