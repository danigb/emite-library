package com.calclab.emite.core.client.xmpp.session;

import com.google.gwt.event.shared.EventHandler;

public interface IncomingIQHandler extends EventHandler {

    void onIQ(IncomingIQEvent event);

}
