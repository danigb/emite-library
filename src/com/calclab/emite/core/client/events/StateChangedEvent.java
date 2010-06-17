package com.calclab.emite.core.client.events;

import com.google.gwt.event.shared.GwtEvent;

public abstract class StateChangedEvent extends GwtEvent<StateChangedHandler> {

    private final String state;
    private final Type<StateChangedHandler> type;

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
