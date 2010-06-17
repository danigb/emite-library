package com.calclab.emite.core.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class StateChangedEvent extends GwtEvent<StateChangedHandler> {

    private static final Type<StateChangedHandler> TYPE = new Type<StateChangedHandler>();

    public static Type<StateChangedHandler> getType() {
	return TYPE;
    }
    private final String state;

    private final Type<StateChangedHandler> type;

    public StateChangedEvent(final String state) {
	this(TYPE, state);
    }

    protected StateChangedEvent(final Type<StateChangedHandler> type, final String state) {
	this.type = type;
	this.state = state;
    }

    @Override
    public Type<StateChangedHandler> getAssociatedType() {
	return type;
    }

    public String getState() {
	return state;
    }

    @Override
    protected void dispatch(final StateChangedHandler handler) {
	handler.onStateChanged(this);
    }

}
