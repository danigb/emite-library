package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.google.gwt.event.shared.GwtEvent;

public class PresenceEvent extends GwtEvent<PresenceHandler> {

    private static final Type<PresenceHandler> TYPE = new Type<PresenceHandler>();

    public static Type<PresenceHandler> getType() {
	return TYPE;
    }

    private final Presence presence;

    public PresenceEvent(final Presence presence) {
	this.presence = presence;
    }

    @Override
    public Type<PresenceHandler> getAssociatedType() {
	return TYPE;
    }

    public Presence getPresence() {
	return presence;
    }

    @Override
    protected void dispatch(final PresenceHandler handler) {
	handler.onPresence(this);
    }

}
