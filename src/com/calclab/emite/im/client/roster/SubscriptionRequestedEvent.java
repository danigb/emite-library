package com.calclab.emite.im.client.roster;

import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.google.gwt.event.shared.GwtEvent;

public class SubscriptionRequestedEvent extends GwtEvent<SubscriptionRequestedHandler> {

    private static final Type<SubscriptionRequestedHandler> TYPE = new Type<SubscriptionRequestedHandler>();

    public static Type<SubscriptionRequestedHandler> getType() {
	return TYPE;
    }

    private final XmppURI fromUri;
    private final String nickName;

    public SubscriptionRequestedEvent(final XmppURI fromUri, final String nickName) {
	this.fromUri = fromUri;
	this.nickName = nickName;
    }

    @Override
    public Type<SubscriptionRequestedHandler> getAssociatedType() {
	return TYPE;
    }

    public XmppURI getFromUri() {
	return fromUri;
    }

    public String getNickName() {
	return nickName;
    }

    @Override
    protected void dispatch(final SubscriptionRequestedHandler handler) {
	handler.onSubscriptionRequested(this);
    }

}
