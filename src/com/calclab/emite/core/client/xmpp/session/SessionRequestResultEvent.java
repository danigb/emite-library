package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.google.gwt.event.shared.GwtEvent;

public class SessionRequestResultEvent extends GwtEvent<SessionRequestResultHandler> {

    private static final Type<SessionRequestResultHandler> TYPE = new Type<SessionRequestResultHandler>();

    public static Type<SessionRequestResultHandler> getType() {
	return TYPE;
    }
    private final boolean succeed;

    private final XmppURI uri;

    public SessionRequestResultEvent(final XmppURI uri) {
	this(true, uri);
    }

    private SessionRequestResultEvent(final boolean succeed, final XmppURI uri) {
	this.succeed = succeed;
	this.uri = uri;
    }

    @Override
    public Type<SessionRequestResultHandler> getAssociatedType() {
	return TYPE;
    }

    public XmppURI getUri() {
	return uri;
    }

    public boolean isSucceed() {
	return succeed;
    }

    @Override
    protected void dispatch(final SessionRequestResultHandler handler) {
	handler.onSessionRequestResult(this);
    }

}
