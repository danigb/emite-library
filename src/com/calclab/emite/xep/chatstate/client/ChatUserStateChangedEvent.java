package com.calclab.emite.xep.chatstate.client;

import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;

public class ChatUserStateChangedEvent extends StateChangedEvent {

    private static final Type<StateChangedHandler> TYPE = new Type<StateChangedHandler>();

    public static Type<StateChangedHandler> getType() {
	return TYPE;
    }

    protected ChatUserStateChangedEvent(final String state) {
	super(TYPE, state);
    }

}
