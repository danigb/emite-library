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
package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.xmpp.session.Session.State;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.suco.client.events.Listener;

/**
 * A simple component that sets the session ready after logged in. This
 * component is removed if InstantMessagingModule used.
 */
public class SessionReady {

    public SessionReady(final Session session) {
	session.onStateChanged(new Listener<State>() {
	    public void onEvent(final State state) {
		if (state == State.loggedIn) {
		    session.send(new Presence());
		    session.setReady();
		}
	    }
	});
    }

}
