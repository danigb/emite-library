package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.xmpp.session.Session.State;
import com.google.gwt.event.shared.GwtEvent;

public class StateChangedEvent extends GwtEvent<StateChangedHandler> {

    private static final Type<StateChangedHandler> TYPE = new Type<StateChangedHandler>();

    public static Type<StateChangedHandler> getType() {
	return TYPE;
    }

    private final State state;

    public StateChangedEvent(final State state) {
	this.state = state;
    }

    @Override
    public Type<StateChangedHandler> getAssociatedType() {
	return TYPE;
    }

    public State getState() {
	return state;
    }

    @Override
    protected void dispatch(final StateChangedHandler handler) {
	handler.onStateChanged(this);
    }

}
