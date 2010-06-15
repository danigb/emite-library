package com.calclab.emite.core.client.xmpp.sasl;

import com.calclab.emite.core.client.xmpp.session.Credentials;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.google.gwt.event.shared.GwtEvent;

public class AuthorizationEvent extends GwtEvent<AuthorizationHandler> {
    private static final Type<AuthorizationHandler> TYPE = new Type<AuthorizationHandler>();

    public static Type<AuthorizationHandler> getType() {
	return TYPE;
    }

    private final boolean succeed;
    private final Credentials credentials;

    /**
     * Build a failed authorization event
     */
    public AuthorizationEvent() {
	this(false, null);
    }

    /**
     * Build a succeeded authorization event with the current credentials
     * 
     * @param uri
     *            the uri of the authorized user
     */
    public AuthorizationEvent(final Credentials credentials) {
	this(true, credentials);
    }

    private AuthorizationEvent(final boolean succeed, final Credentials credentials) {
	this.succeed = succeed;
	this.credentials = credentials;
    }

    @Override
    public Type<AuthorizationHandler> getAssociatedType() {
	return TYPE;
    }

    public Credentials getCredentials() {
	return credentials;
    }

    public XmppURI getXmppUri() {
	return credentials.getXmppUri();
    }

    public boolean isSucceed() {
	return succeed;
    }

    @Override
    protected void dispatch(final AuthorizationHandler handler) {
	handler.onAuthorization(this);
    }

}
