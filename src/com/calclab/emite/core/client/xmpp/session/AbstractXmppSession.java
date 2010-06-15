package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractXmppSession implements XmppSession {
    protected final EmiteEventBus eventBus;
    private String state;

    public AbstractXmppSession(final EmiteEventBus eventBus) {
	this.eventBus = eventBus;
	state = SessionState.disconnected;
    }

    @Override
    public HandlerRegistration addIncomingIQHandler(final IncomingIQHandler handler) {
	return eventBus.addHandler(IncomingIQEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addIncomingMessageHandler(final IncomingMessageHandler handler) {
	return eventBus.addHandler(IncomingMessageEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addIncomingPresenceHandler(final IncomingPresenceHandler handler) {
	return eventBus.addHandler(IncomingPresenceEvent.getType(), handler);
    }

    public String getSessionState() {
	return state;
    }

    protected void setSessionState(final String state) {
	this.state = state;
	eventBus.fireEvent(new StateChangedEvent(state));
    }
}
