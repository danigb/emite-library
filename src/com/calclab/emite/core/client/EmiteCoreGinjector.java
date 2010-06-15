package com.calclab.emite.core.client;

import com.calclab.emite.core.client.services.Services;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules( { EmiteCoreModule.class })
public interface EmiteCoreGinjector extends Ginjector {
    Services getServices();

    XmppSession getSession();
}
