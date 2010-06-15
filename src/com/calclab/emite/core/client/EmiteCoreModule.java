package com.calclab.emite.core.client;

import com.calclab.emite.core.client.bosh.BoshConnection;
import com.calclab.emite.core.client.bosh.Connection;
import com.calclab.emite.core.client.services.Services;
import com.calclab.emite.core.client.services.gwt.GWTServices;
import com.calclab.emite.core.client.xmpp.session.Session;
import com.calclab.emite.core.client.xmpp.session.SessionImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class EmiteCoreModule extends AbstractGinModule {

    @Override
    protected void configure() {
	bind(Services.class).to(GWTServices.class).in(Singleton.class);
	bind(Connection.class).to(BoshConnection.class).in(Singleton.class);
	bind(Session.class).to(SessionImpl.class).in(Singleton.class);
    }
}
