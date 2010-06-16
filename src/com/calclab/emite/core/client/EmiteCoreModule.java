package com.calclab.emite.core.client;

import com.calclab.emite.core.client.bosh.BoshConnection;
import com.calclab.emite.core.client.bosh.XmppBoshConnection;
import com.calclab.emite.core.client.conn.Connection;
import com.calclab.emite.core.client.conn.XmppConnection;
import com.calclab.emite.core.client.events.DefaultEmiteEventBus;
import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.services.Services;
import com.calclab.emite.core.client.services.gwt.GWTServices;
import com.calclab.emite.core.client.xmpp.session.DefaultXmppSession;
import com.calclab.emite.core.client.xmpp.session.Session;
import com.calclab.emite.core.client.xmpp.session.SessionImpl;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class EmiteCoreModule extends AbstractGinModule {

    @Override
    protected void configure() {
	bind(Services.class).to(GWTServices.class).in(Singleton.class);
	bind(XmppConnection.class).to(XmppBoshConnection.class).in(Singleton.class);
	bind(XmppSession.class).to(DefaultXmppSession.class).in(Singleton.class);
	bind(EmiteEventBus.class).to(DefaultEmiteEventBus.class).in(Singleton.class);

	// deprecated
	bind(Session.class).to(SessionImpl.class).in(Singleton.class);
	bind(Connection.class).to(BoshConnection.class).in(Singleton.class);
    }
}
