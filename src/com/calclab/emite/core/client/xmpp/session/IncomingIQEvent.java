package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.google.gwt.event.shared.GwtEvent;

public class IncomingIQEvent extends GwtEvent<IncomingIQHandler> {

    private static final Type<IncomingIQHandler> TYPE = new Type<IncomingIQHandler>();

    public static Type<IncomingIQHandler> getType() {
	return TYPE;
    }

    private final IQ iq;

    public IncomingIQEvent(final IQ iq) {
	this.iq = iq;
    }

    @Override
    public Type<IncomingIQHandler> getAssociatedType() {
	return getType();
    }

    public IQ getIQ() {
	return iq;
    }

    @Override
    protected void dispatch(final IncomingIQHandler handler) {
	handler.onIQ(this);
    }

}
