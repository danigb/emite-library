package com.calclab.emite.core.client.events;

import com.calclab.emite.core.client.xmpp.stanzas.Message;

public class MessageTestHandler implements MessageHandler {

    private MessageEvent event;

    public MessageTestHandler() {
	event = null;
    }

    public MessageEvent getEvent() {
	return event;
    }

    public Message getEventMessage() {
	return event != null ? event.getMessage() : null;
    }

    public boolean hasEvent() {
	return event != null;
    }

    @Override
    public void onPacketEvent(final MessageEvent event) {
	this.event = event;
    }

}
