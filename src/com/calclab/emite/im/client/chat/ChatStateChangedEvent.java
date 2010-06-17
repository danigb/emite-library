package com.calclab.emite.im.client.chat;

import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;

public class ChatStateChangedEvent extends StateChangedEvent {

    private static final Type<StateChangedHandler> TYPE = new Type<StateChangedHandler>();

    public static Type<StateChangedHandler> getType() {
	return TYPE;
    }

    protected ChatStateChangedEvent(final String state) {
	super(TYPE, state);
    }

}
