package com.calclab.emite.core.client.xmpp.session;

import com.google.gwt.event.shared.EventHandler;

public interface StateChangedHandler extends EventHandler {
    void onStateChanged(StateChangedEvent event);
}
