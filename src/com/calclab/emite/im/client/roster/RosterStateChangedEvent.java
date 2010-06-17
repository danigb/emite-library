package com.calclab.emite.im.client.roster;

import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;

public class RosterStateChangedEvent extends StateChangedEvent {

    private static final Type<StateChangedHandler> TYPE = new Type<StateChangedHandler>();

    public static Type<StateChangedHandler> getType() {
	return TYPE;
    }

    public RosterStateChangedEvent(final String state) {
	super(TYPE, state);
    }

}
