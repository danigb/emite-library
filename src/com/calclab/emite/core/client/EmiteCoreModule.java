package com.calclab.emite.core.client;

import com.calclab.emite.core.client.bosh.BoshConnection;
import com.calclab.emite.core.client.bus.DefaultEmiteEventBus;
import com.calclab.emite.core.client.bus.EmiteEventBus;
import com.calclab.emite.core.client.conn.Connection;
import com.calclab.emite.core.client.services.Services;
import com.calclab.emite.core.client.services.gwt.GWTServices;
import com.calclab.emite.core.client.xmpp.session.Session;
import com.calclab.emite.core.client.xmpp.session.DefaultXmppSession;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class EmiteCoreModule extends AbstractGinModule {

    @Override
    protected void configure() {
	bind(Services.class).to(GWTServices.class).in(Singleton.class);
	bind(Connection.class).to(BoshConnection.class).in(Singleton.class);
	bind(Session.class).to(DefaultXmppSession.class).in(Singleton.class);
	bind(EmiteEventBus.class).to(DefaultEmiteEventBus.class).in(Singleton.class);
    }
}
