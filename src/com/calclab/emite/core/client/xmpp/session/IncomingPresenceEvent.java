package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.events.PresenceEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;

public class IncomingPresenceEvent extends PresenceEvent {

    private static final Type<PresenceHandler> TYPE = new Type<PresenceHandler>();

    public static Type<PresenceHandler> getType() {
	return TYPE;
    }

    public IncomingPresenceEvent(final Presence presence) {
	super(TYPE, presence);
    }

}
