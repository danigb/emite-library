package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.bus.EmiteEventBus;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractXmppSession implements XmppSession {
    protected final EmiteEventBus eventBus;
    private SessionState state;

    public AbstractXmppSession(final EmiteEventBus eventBus) {
	this.eventBus = eventBus;
	state = SessionState.disconnected;
    }

    @Override
    public HandlerRegistration addIQHandler(final IQHandler handler) {
	return eventBus.addHandler(IQEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addMessageHandler(final MessageHandler handler) {
	return eventBus.addHandler(MessageEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addPresenceHandler(final PresenceHandler handler) {
	return eventBus.addHandler(PresenceEvent.getType(), handler);
    }

    public SessionState getSessionState() {
	return state;
    }

    protected void setSessionState(final SessionState state) {
	this.state = state;
	eventBus.fireEvent(new StateChangedEvent(state));
    }
}
