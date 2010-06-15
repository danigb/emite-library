package com.calclab.emite.core.client.xmpp.session;

import com.google.gwt.event.shared.EventHandler;

public interface IncomingPresenceHandler extends EventHandler {
    void onIncomingPresence(IncomingPresenceEvent event);
}
