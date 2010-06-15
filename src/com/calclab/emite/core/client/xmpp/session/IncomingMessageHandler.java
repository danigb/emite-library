package com.calclab.emite.core.client.xmpp.session;

import com.google.gwt.event.shared.EventHandler;

public interface IncomingMessageHandler extends EventHandler {

    void onIncomingMessage(IncomingMessageEvent event);

}
