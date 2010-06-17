package com.calclab.emite.xep.vcard.client;

import com.google.gwt.event.shared.GwtEvent;

public class VCardResponseEvent extends GwtEvent<VCardResponseHandler> {

    private static final Type<VCardResponseHandler> TYPE = new Type<VCardResponseHandler>();

    public static Type<VCardResponseHandler> getType() {
	return TYPE;
    }

    private final VCardResponse response;

    public VCardResponseEvent(final VCardResponse response) {
	this.response = response;
    }

    @Override
    public Type<VCardResponseHandler> getAssociatedType() {
	return TYPE;
    }

    public VCardResponse getResponse() {
	return response;
    }

    @Override
    protected void dispatch(final VCardResponseHandler handler) {
	handler.onVCardResponse(this);
    }

}
