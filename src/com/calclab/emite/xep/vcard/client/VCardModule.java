package com.calclab.emite.xep.vcard.client;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.suco.client.Suco;
import com.calclab.suco.client.ioc.decorator.Singleton;
import com.calclab.suco.client.ioc.module.AbstractModule;
import com.calclab.suco.client.ioc.module.Factory;
import com.google.gwt.core.client.EntryPoint;

public class VCardModule extends AbstractModule implements EntryPoint {

    @Override
    public void onModuleLoad() {
	Suco.install(this);
    }

    @Override
    protected void onInstall() {
	register(Singleton.class, new Factory<VCardManager>(VCardManager.class) {
	    @Override
	    public VCardManager create() {
		return new VCardManager($(EmiteEventBus.class), $(XmppSession.class));
	    }
	});
    }

}
