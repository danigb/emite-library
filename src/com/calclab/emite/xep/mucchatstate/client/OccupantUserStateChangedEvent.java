package com.calclab.emite.xep.mucchatstate.client;

import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.google.gwt.event.shared.GwtEvent;

public class OccupantUserStateChangedEvent extends GwtEvent<OccupantUserStateChangedHandler> {

    private static final Type<OccupantUserStateChangedHandler> TYPE = new Type<OccupantUserStateChangedHandler>();

    public static Type<OccupantUserStateChangedHandler> getType() {
	return TYPE;
    }
    private final String state;
    private final XmppURI fromUri;

    public OccupantUserStateChangedEvent(final XmppURI fromUri, final String state) {
	this.fromUri = fromUri;
	this.state = state;
    }

    @Override
    public Type<OccupantUserStateChangedHandler> getAssociatedType() {
	return TYPE;
    }

    public XmppURI getFromUri() {
	return fromUri;
    }

    public String getState() {
	return state;
    }

    @Override
    protected void dispatch(final OccupantUserStateChangedHandler handler) {
	handler.onRoomUserStateChanged(this);
    }

}
