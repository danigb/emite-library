package com.calclab.emite.core.client.xmpp.session;

import com.google.gwt.event.shared.EventHandler;

public interface MessageHandler extends EventHandler {

    void onMessage(MessageEvent event);

}
