package com.calclab.emite.im.client.presence;

import com.calclab.emite.core.client.events.PresenceEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;

public class OwnPresenceChangedEvent extends PresenceEvent {

    private static final Type<PresenceHandler> TYPE = new Type<PresenceHandler>();

    public static Type<PresenceHandler> getType() {
	return TYPE;
    }

    public OwnPresenceChangedEvent(final Presence presence) {
	super(TYPE, presence);
    }

}
