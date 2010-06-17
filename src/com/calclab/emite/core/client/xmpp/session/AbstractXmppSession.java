package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.events.IQEvent;
import com.calclab.emite.core.client.events.IQHandler;
import com.calclab.emite.core.client.events.MessageHandler;
import com.calclab.emite.core.client.events.MessageReceivedEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractXmppSession implements XmppSession {
    protected final EmiteEventBus eventBus;
    private String state;

    public AbstractXmppSession(final EmiteEventBus eventBus) {
	this.eventBus = eventBus;
	state = SessionState.disconnected;
    }

    @Override
    public HandlerRegistration addIncomingIQHandler(final IQHandler handler) {
	return eventBus.addHandler(IQEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addIncomingMessageHandler(final MessageHandler handler) {
	return eventBus.addHandler(MessageReceivedEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addIncomingPresenceHandler(final PresenceHandler handler) {
	return eventBus.addHandler(IncomingPresenceEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addSessionStateChangedHandler(final StateChangedHandler handler) {
	return eventBus.addHandler(SessionStateChangedEvent.getType(), handler);
    }

    public String getSessionState() {
	return state;
    }

    @Override
    public void login(final XmppURI uri, final String password) {
	login(new Credentials(uri, password, Credentials.ENCODING_NONE));
    }

    protected void fireIQ(final IQ iq) {
	eventBus.fireEvent(new IQEvent(iq));
    }

    protected void fireMessage(final Message message) {
	eventBus.fireEvent(new MessageReceivedEvent(message));
    }

    protected void firePresence(final Presence presence) {
	eventBus.fireEvent(new IncomingPresenceEvent(presence));
    }

    protected void setSessionState(final String state) {
	this.state = state;
	eventBus.fireEvent(new SessionStateChangedEvent(state));
    }
}
