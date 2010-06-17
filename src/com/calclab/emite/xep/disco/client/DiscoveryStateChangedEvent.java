package com.calclab.emite.xep.disco.client;

import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;

public class DiscoveryStateChangedEvent extends StateChangedEvent {

    private static final Type<StateChangedHandler> TYPE = new Type<StateChangedHandler>();

    public static Type<StateChangedHandler> getType() {
	return TYPE;
    }

    protected DiscoveryStateChangedEvent(final String state) {
	super(TYPE, state);
    }

}
