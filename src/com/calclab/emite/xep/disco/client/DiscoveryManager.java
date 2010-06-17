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

import java.util.ArrayList;
import java.util.List;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.packet.MatcherFactory;
import com.calclab.emite.core.client.packet.PacketMatcher;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.core.client.xmpp.stanzas.IQ.Type;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

public class DiscoveryManager {
    public static class DiscoveryState {
	public static final String ready = "ready";
	public static final String notReady = "notReady";
    }
    private final PacketMatcher filterQuery;
    private ArrayList<Feature> features;
    private ArrayList<Identity> identities;
    private final XmppSession session;
    private String state;
    private final EmiteEventBus eventBus;

    public DiscoveryManager(final EmiteEventBus eventBus, final XmppSession session) {
	this.eventBus = eventBus;
	this.session = session;
	filterQuery = MatcherFactory.byNameAndXMLNS("query", "http://jabber.org/protocol/disco#info");

	session.addSessionStateChangedHandler(new StateChangedHandler() {
	    @Override
	    public void onStateChanged(final StateChangedEvent event) {
		if (session.getSessionState() == XmppSession.SessionState.loggedIn) {
		    sendDiscoQuery(session.getCurrentUser());
		}
	    }
	});

	state = DiscoveryState.notReady;
    }

    public HandlerRegistration addDiscoveryStateChangedHandler(final StateChangedHandler handler) {
	return eventBus.addHandler(DiscoveryStateChangedEvent.getType(), handler);
    }

    public ArrayList<Feature> getFeatures() {
	return features;
    }

    public ArrayList<Identity> getIdentities() {
	return identities;
    }

    public String getState() {
	return state;
    }

    public void sendDiscoQuery(final XmppURI uri) {
	final IQ iq = new IQ(Type.get, uri.getHostURI());
	iq.addQuery("http://jabber.org/protocol/disco#info");
	session.sendIQ("disco", iq, new Listener<IPacket>() {
	    public void onEvent(final IPacket response) {
		final IPacket query = response.getFirstChild(filterQuery);
		processIdentity(query.getChildren(MatcherFactory.byName("identity")));
		processFeatures(query.getChildren(MatcherFactory.byName("features")));
		setState(DiscoveryState.ready);
	    }
	});
    }

    private void processFeatures(final List<? extends IPacket> children) {
	features = new ArrayList<Feature>();
	for (final IPacket child : children) {
	    features.add(Feature.fromPacket(child));
	}
    }

    private void processIdentity(final List<? extends IPacket> children) {
	identities = new ArrayList<Identity>();
	for (final IPacket child : children) {
	    identities.add(Identity.fromPacket(child));
	}
    }

    protected void setState(final String state) {
	assert state != null : "The discovery state can't be null";
	this.state = state;
	eventBus.fireEvent(new DiscoveryStateChangedEvent(state));
    }
}
