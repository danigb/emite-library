package com.calclab.emite.im.client.presence;

import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.google.gwt.event.shared.GwtEvent;

public class OwnPresenceChangedEvent extends GwtEvent<OwnPresenceChangedHandler> {

    private static final Type<OwnPresenceChangedHandler> TYPE = new Type<OwnPresenceChangedHandler>();

    public static Type<OwnPresenceChangedHandler> getType() {
	return TYPE;
    }

    private final Presence presence;

    public OwnPresenceChangedEvent(final Presence presence) {
	this.presence = presence;
    }

    @Override
    public Type<OwnPresenceChangedHandler> getAssociatedType() {
	return TYPE;
    }

    public Presence getPresence() {
	return presence;
    }

    @Override
    protected void dispatch(final OwnPresenceChangedHandler handler) {
	handler.onOwnPresenceChanged(this);
    }

}
