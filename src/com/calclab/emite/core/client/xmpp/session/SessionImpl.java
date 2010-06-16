package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.bosh.StreamSettings;
import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.suco.client.events.Listener;
import com.google.inject.Inject;

public class SessionImpl extends AbstractSession implements Session {

    private final XmppSession delegate;

    @Inject
    public SessionImpl(final EmiteEventBus eventBus, final XmppSession delegate) {
	super(eventBus);
	this.delegate = delegate;
    }

    @Override
    public XmppURI getCurrentUser() {
	return delegate.getCurrentUser();
    }

    @Override
    public boolean isLoggedIn() {
	return delegate.isLoggedIn();
    }

    @Override
    public void login(final Credentials credentials) {
	delegate.login(credentials);
    }

    @Override
    public void logout() {
	delegate.logout();
    }

    @Override
    public StreamSettings pause() {
	return delegate.pause();
    }

    @Override
    public void resume(final XmppURI userURI, final StreamSettings settings) {
	delegate.resume(userURI, settings);
    }

    @Override
    public void send(final IPacket stanza) {
	delegate.send(stanza);
    }

    @Override
    public void sendIQ(final String category, final IQ iq, final Listener<IPacket> listener) {
	delegate.sendIQ(category, iq, listener);
    }

    @Override
    public void setReady() {
	delegate.setReady();
    }

}
