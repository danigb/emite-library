/*
 *
 * ((e)) emite: A pure gwt (Google Web Toolkit) xmpp (jabber) library
 *
 * (c) 2008-2009 The emite development team (see CREDITS for details)
 * This file is part of emite.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.calclab.emite.xep.disco.client;

import com.calclab.emite.core.client.xmpp.session.Session;
import com.calclab.emite.core.client.xmpp.session.SessionComponent;
import com.calclab.suco.client.ioc.module.AbstractModule;
import com.calclab.suco.client.ioc.module.Factory;

/**
 * Implements XEP-0030: Service Discovery
 * 
 * This specification defines an XMPP protocol extension for discovering
 * information about other XMPP entities
 * 
 * @see http://www.xmpp.org/extensions/xep-0030.html
 * 
 *      NOT IMPLEMENTED
 * 
 */
public class DiscoveryModule extends AbstractModule {

    public DiscoveryModule() {
	super();
    }

    @Override
    public void onInstall() {
	register(SessionComponent.class, new Factory<DiscoveryManager>(DiscoveryManager.class) {
	    @Override
	    public DiscoveryManager create() {
		return new DiscoveryManager($(Session.class));
	    }
	});
    }
}
