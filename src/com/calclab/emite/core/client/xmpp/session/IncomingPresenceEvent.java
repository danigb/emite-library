package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.google.gwt.event.shared.GwtEvent;

public class IncomingPresenceEvent extends GwtEvent<IncomingPresenceHandler> {

    private static final Type<IncomingPresenceHandler> TYPE = new Type<IncomingPresenceHandler>();

    public static Type<IncomingPresenceHandler> getType() {
	return TYPE;
    }

    private final Presence presence;

    public IncomingPresenceEvent(final Presence presence) {
	this.presence = presence;
    }

    @Override
    public Type<IncomingPresenceHandler> getAssociatedType() {
	return TYPE;
    }

    public Presence getPresence() {
	return presence;
    }

    @Override
    protected void dispatch(final IncomingPresenceHandler handler) {
	handler.onIncomingPresence(this);
    }

}
