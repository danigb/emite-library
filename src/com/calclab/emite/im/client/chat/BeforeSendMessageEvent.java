package com.calclab.emite.im.client.chat;

import com.calclab.emite.core.client.events.MessageEvent;
import com.calclab.emite.core.client.events.MessageHandler;
import com.calclab.emite.core.client.xmpp.stanzas.Message;

public class BeforeSendMessageEvent extends MessageEvent {

    private static final Type<MessageHandler> TYPE = new Type<MessageHandler>();

    public static Type<MessageHandler> getType() {
	return TYPE;
    }

    public BeforeSendMessageEvent(final Message message) {
	super(TYPE, message);
    }

}
