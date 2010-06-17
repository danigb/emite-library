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
package com.calclab.emite.xep.avatar.client;

import java.util.List;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.events.PresenceEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.packet.MatcherFactory;
import com.calclab.emite.core.client.packet.PacketMatcher;
import com.calclab.emite.core.client.xmpp.session.IQResponseHandler;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.core.client.xmpp.stanzas.IQ.Type;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * XEP-0153: vCard-Based Avatars (Version 1.0)
 */
public class AvatarManager {
    private static final PacketMatcher FILTER_X = MatcherFactory.byName("x");
    private static final String VCARD = "vCard";
    private static final String XMLNS = "vcard-temp";
    private static final String PHOTO = "PHOTO";
    private static final String TYPE = "TYPE";
    private static final String BINVAL = "BINVAL";
    private final XmppSession session;
    private final EmiteEventBus eventBus;

    public AvatarManager(final EmiteEventBus eventBus, final XmppSession session) {
	this.eventBus = eventBus;
	this.session = session;

	session.addIncomingPresenceHandler(new PresenceHandler() {
	    @Override
	    public void onIncomingPresence(final PresenceEvent event) {
		final Presence presence = event.getPresence();
		final List<? extends IPacket> children = presence.getChildren(FILTER_X);
		for (final IPacket child : children) {
		    if (child.hasAttribute("xmlns", XMLNS + ":x:update")) {
			eventBus.fireEvent(new IncomingHashPresenceEvent(presence));
		    }
		}

	    }
	});
    }

    /**
     * Add a handler to know when a avatar vcard has arrived
     * 
     * @param handler
     *            the handler
     * @return a handler registration to detach the handler
     */
    public HandlerRegistration addIncomingAvatarVCardHandler(final IncomingAvatarVCardHandler handler) {
	return eventBus.addHandler(IncomingAvatarVCardEvent.getType(), handler);
    }

    /**
     * Add a handler to know when a hash presence has arrived
     * 
     * @param handler
     * @return a handler registration object to detach the handler
     */
    public HandlerRegistration addIncomingHashPresenceHandler(final PresenceHandler handler) {
	return eventBus.addHandler(IncomingHashPresenceEvent.getType(), handler);
    }

    /**
     * @see addHashPresenceHandler
     * @param listener
     */
    @Deprecated
    public void onHashPresenceReceived(final Listener<Presence> listener) {
	addIncomingHashPresenceHandler(new PresenceHandler() {
	    @Override
	    public void onIncomingPresence(final PresenceEvent event) {
		listener.onEvent(event.getPresence());
	    }
	});
    }

    public void onVCardReceived(final Listener<AvatarVCard> listener) {
	addIncomingAvatarVCardHandler(new IncomingAvatarVCardHandler() {
	    @Override
	    public void onIncomingAvatarVCard(final IncomingAvatarVCardEvent event) {
		listener.onEvent(event.getAvatarVCard());
	    }
	});
    }

    /**
     * When the recipient's client receives the hash of the avatar image, it
     * SHOULD check the hash to determine if it already has a cached copy of
     * that avatar image. If not, it retrieves the sender's full vCard in
     * accordance with the protocol flow described in XEP-0054 (note that this
     * request is sent to the user's bare JID, not full JID):
     * 
     * @param otherJID
     */
    public void requestVCard(final XmppURI otherJID) {
	final IQ iq = new IQ(Type.get, otherJID);
	iq.addChild(VCARD, XMLNS);
	session.sendIQ("avatar", iq, new IQResponseHandler() {
	    public void onIQ(final IQ received) {
		if (received.hasAttribute("type", "result") && received.hasChild(VCARD)
			&& received.hasAttribute("to", session.getCurrentUser().toString())) {
		    final XmppURI from = XmppURI.jid(received.getAttribute("from"));
		    final IPacket photo = received.getFirstChild(VCARD).getFirstChild(PHOTO);
		    final String photoType = photo.getFirstChild(TYPE).getText();
		    final String photoBinval = photo.getFirstChild(BINVAL).getText();
		    final AvatarVCard avatar = new AvatarVCard(from, null, photoType, photoBinval);
		    eventBus.fireEvent(new IncomingAvatarVCardEvent(avatar));
		}

	    }
	});
    }

    public void setVCardAvatar(final String photoBinary) {
	final IQ iq = new IQ(Type.set, null);
	final IPacket vcard = iq.addChild(VCARD, XMLNS);
	vcard.With("xdbns", XMLNS).With("prodid", "-//HandGen//NONSGML vGen v1.0//EN");
	vcard.setAttribute("xdbns", XMLNS);
	vcard.setAttribute("prodid", "-//HandGen//NONSGML vGen v1.0//EN");
	vcard.setAttribute("version", "2.0");
	vcard.addChild(PHOTO, null).addChild(BINVAL, null).setText(photoBinary);
	session.sendIQ("avatar", iq, new IQResponseHandler() {
	    public void onIQ(final IQ received) {
		if (IQ.isSuccess(received)) {

		}
	    }
	});
    }

}
