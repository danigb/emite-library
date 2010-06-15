package com.calclab.emite.core.client.xmpp.session;

import com.google.gwt.event.shared.GwtEvent;

public class StateChangedEvent extends GwtEvent<StateChangedHandler> {

    private static final Type<StateChangedHandler> TYPE = new Type<StateChangedHandler>();

    public static Type<StateChangedHandler> getType() {
	return TYPE;
    }

    private final XmppSession.SessionState state;

    public StateChangedEvent(final XmppSession.SessionState state) {
	this.state = state;
    }

    @Override
    public Type<StateChangedHandler> getAssociatedType() {
	return TYPE;
    }

    public XmppSession.SessionState getState() {
	return state;
    }

    @Override
    protected void dispatch(final StateChangedHandler handler) {
	handler.onStateChanged(this);
    }

}
