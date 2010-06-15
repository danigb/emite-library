package com.calclab.emite.core.client.xmpp.sasl;

import com.google.gwt.event.shared.EventHandler;

public interface AuthorizationHandler extends EventHandler {

    void onAuthorization(AuthorizationEvent event);

}
