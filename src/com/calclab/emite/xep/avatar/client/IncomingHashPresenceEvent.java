package com.calclab.emite.xep.avatar.client;

import com.calclab.emite.core.client.events.PresenceEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;

public class IncomingHashPresenceEvent extends PresenceEvent {

    private static final Type<PresenceHandler> TYPE = new Type<PresenceHandler>();

    public static Type<PresenceHandler> getType() {
	return TYPE;
    }

    public IncomingHashPresenceEvent(final Presence presence) {
	super(TYPE, presence);
    }

}
