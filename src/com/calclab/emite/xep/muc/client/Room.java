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
package com.calclab.emite.xep.muc.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.calclab.emite.core.client.events.PresenceEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.events.ChangedEvent.ChangeAction;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.packet.MatcherFactory;
import com.calclab.emite.core.client.packet.PacketMatcher;
import com.calclab.emite.core.client.xmpp.datetime.XmppDateTime;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.BasicStanza;
import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.core.client.xmpp.stanzas.Presence.Show;
import com.calclab.emite.core.client.xmpp.stanzas.Presence.Type;
import com.calclab.emite.im.client.chat.AbstractChat;
import com.calclab.emite.im.client.chat.Chat;
import com.calclab.suco.client.events.Listener;
import com.calclab.suco.client.events.Listener2;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * A Room implementation. You can create rooms using RoomManager
 * 
 * @see RoomManager
 */
public class Room extends AbstractChat implements Chat {
    protected static final PacketMatcher ROOM_CREATED = MatcherFactory.byNameAndXMLNS("x",
	    "http://jabber.org/protocol/muc#user");
    protected final HashMap<XmppURI, Occupant> occupantsByURI;
    protected final XmppSession session;

    /**
     * Create a new room. The roomURI MUST include the nick (as the resource)
     * 
     * @param session
     *            the session
     * @param roomURI
     *            the room uri with the nick specified in the resource part
     */
    public Room(final XmppSession session, final XmppURI roomURI, final XmppURI starter,
	    final HistoryOptions historyOptions) {
	super(session, roomURI, starter);
	this.session = session;
	occupantsByURI = new LinkedHashMap<XmppURI, Occupant>();

	// @see http://www.xmpp.org/extensions/xep-0045.html#createroom

	session.addIncomingPresenceHandler(new PresenceHandler() {
	    @Override
	    public void onIncomingPresence(final PresenceEvent event) {
		final Presence presence = event.getPresence();
		final XmppURI occupantURI = presence.getFrom();
		if (roomURI.equalsNoResource(occupantURI)) {
		    handlePresence(occupantURI, presence);
		}
	    }
	});

	session.addStateChangedHandler(new StateChangedHandler() {

	    @Override
	    public void onStateChanged(final StateChangedEvent event) {
		final String state = event.getState();
		if (XmppSession.SessionState.loggedIn == state) {
		} else if (XmppSession.SessionState.loggingOut == state) {
		    close();
		} else if (XmppSession.SessionState.disconnected == state || XmppSession.SessionState.error == state) {
		    // TODO : add an error/out state ?
		    setChatState(ChatState.locked);
		}
	    }
	});

	session.send(createEnterPresence(historyOptions));
    }

    /**
     * Add a handler to know when a invitation has been sent
     * 
     * @param handler
     *            the handler
     * @return a handler registration object to detach the handler
     */
    public HandlerRegistration addRoomInvitationSentHandler(final RoomInvitationSentHandler handler) {
	return eventBus.addHandler(RoomInvitationSentEvent.getType(), handler);
    }

    /**
     * Add a handler to know when a room occupant has been added to this room
     * 
     * @param handler
     *            the handler
     * @return a handler registration object to detach the handler
     */
    public HandlerRegistration addRoomOccupantAddedHandler(final RoomOccupantsChangedHandler handler) {
	return addRoomOccupantsChangedHandler(new RoomOccupantsChangedHandler() {
	    @Override
	    public void onRoomOccupantsChanged(final RoomOccupantsChangedEvent event) {
		if (event.wasAdded()) {
		    handler.onRoomOccupantsChanged(event);
		}
	    }
	});
    }

    /**
     * Add a handler to know when a room occupant has been modified
     * 
     * @param handler
     *            the handler
     * @return a handler registration object to detach the handler
     */
    public HandlerRegistration addRoomOccupantModifiedHandler(final RoomOccupantsChangedHandler handler) {
	return addRoomOccupantsChangedHandler(new RoomOccupantsChangedHandler() {
	    @Override
	    public void onRoomOccupantsChanged(final RoomOccupantsChangedEvent event) {
		if (event.wasModified()) {
		    handler.onRoomOccupantsChanged(event);
		}
	    }
	});
    }

    /**
     * Add a handler to know when a room occupant has been removed from this
     * room
     * 
     * @param handler
     *            the handler
     * @return a handler registration object to detach the handler
     */
    public HandlerRegistration addRoomOccupantRemovedHandler(final RoomOccupantsChangedHandler handler) {
	return addRoomOccupantsChangedHandler(new RoomOccupantsChangedHandler() {
	    @Override
	    public void onRoomOccupantsChanged(final RoomOccupantsChangedEvent event) {
		if (event.wasRemoved()) {
		    handler.onRoomOccupantsChanged(event);
		}
	    }
	});
    }

    /**
     * Add a handler to know when a room occupant has changed (it can be added,
     * removed or modified)
     * 
     * @param handler
     *            the handler to be added
     * @return a handler registration object to detach the handler
     */
    public HandlerRegistration addRoomOccupantsChangedHandler(final RoomOccupantsChangedHandler handler) {
	return eventBus.addHandler(RoomOccupantsChangedEvent.getType(), handler);
    }

    /**
     * Add a handler to know when a room subject has changed
     * 
     * @param handler
     *            the handler
     * @return a handler registration object to detach the handler
     */
    public HandlerRegistration addRoomSubjectChangedHandler(final RoomSubjectChangedHandler handler) {
	return eventBus.addHandler(RoomSubjectChangedEvent.getType(), handler);
    }

    /**
     * Exit and locks the current room. This is done automatically when the
     * session logouts
     * 
     * @see http://www.xmpp.org/extensions/xep-0045.html#exit
     */
    public void close() {
	if (state == ChatState.ready) {
	    session.send(new Presence(Type.unavailable, null, getURI()));
	    setChatState(ChatState.locked);
	}
    }

    public String getID() {
	return uri.toString();
    }

    public Occupant getOccupantByURI(final XmppURI uri) {
	return occupantsByURI.get(uri);
    }

    public Collection<Occupant> getOccupants() {
	return occupantsByURI.values();
    }

    public int getOccupantsCount() {
	return occupantsByURI.size();
    }

    /**
     * To check if is an echo message
     * 
     * @param message
     * @return true if this message is a room echo
     */
    public boolean isComingFromMe(final Message message) {
	final String myNick = uri.getResource();
	final String messageNick = message.getFrom().getResource();
	return myNick.equals(messageNick);
    }

    /**
     * Add a listener to know when an invitation was sent
     * 
     * @see addRoomInvitationSentHandler
     */
    @Deprecated
    public void onInvitationSent(final Listener2<XmppURI, String> listener) {
	addRoomInvitationSentHandler(new RoomInvitationSentHandler() {
	    @Override
	    public void onRoomInvitationSent(final RoomInvitationSentEvent event) {
		listener.onEvent(event.getInvitedJid(), event.getReasonText());
	    }
	});
    }

    @Deprecated
    public void onOccupantAdded(final Listener<Occupant> listener) {
	addRoomOccupantAddedHandler(new RoomOccupantsChangedHandler() {
	    @Override
	    public void onRoomOccupantsChanged(final RoomOccupantsChangedEvent event) {
		listener.onEvent(event.getOccupant());
	    }
	});
    }

    @Deprecated
    public void onOccupantModified(final Listener<Occupant> listener) {
	addRoomOccupantModifiedHandler(new RoomOccupantsChangedHandler() {
	    @Override
	    public void onRoomOccupantsChanged(final RoomOccupantsChangedEvent event) {
		listener.onEvent(event.getOccupant());
	    }
	});
    }

    @Deprecated
    public void onOccupantRemoved(final Listener<Occupant> listener) {
	addRoomOccupantRemovedHandler(new RoomOccupantsChangedHandler() {
	    @Override
	    public void onRoomOccupantsChanged(final RoomOccupantsChangedEvent event) {
		listener.onEvent(event.getOccupant());
	    }
	});
    }

    @Deprecated
    public void onSubjectChanged(final Listener2<Occupant, String> listener) {
	addRoomSubjectChangedHandler(new RoomSubjectChangedHandler() {
	    @Override
	    public void onRoomSubjectChanged(final RoomSubjectChangedEvent event) {
		listener.onEvent(event.getOccupant(), event.getSubject());
	    }
	});
    }

    public void reEnter(final HistoryOptions historyOptions) {
	if (getChatState() == ChatState.locked) {
	    session.send(createEnterPresence(historyOptions));
	}
    }

    public void removeOccupant(final XmppURI uri) {
	final Occupant occupant = occupantsByURI.remove(uri);
	if (occupant != null) {
	    eventBus.fireEvent(new RoomOccupantsChangedEvent(ChangeAction.REMOVED, occupant));
	}
    }

    @Override
    public void send(final Message message) {
	message.setTo(getURI().getJID());
	message.setType(Message.Type.groupchat);
	super.send(message);
    }

    /**
     * 
     * http://www.xmpp.org/extensions/xep-0045.html#invite
     * 
     * @param userJid
     *            user to invite
     * @param reasonText
     *            reason for the invitation
     */
    public void sendInvitationTo(final XmppURI userJid, final String reasonText) {
	final BasicStanza message = new BasicStanza("message", null);
	message.setFrom(session.getCurrentUser());
	message.setTo(getURI().getJID());
	final IPacket x = message.addChild("x", "http://jabber.org/protocol/muc#user");
	final IPacket invite = x.addChild("invite", null);
	invite.setAttribute("to", userJid.toString());
	final IPacket reason = invite.addChild("reason", null);
	reason.setText(reasonText);
	session.send(message);
	eventBus.fireEvent(new RoomInvitationSentEvent(userJid, reasonText));
    }

    public Occupant setOccupantPresence(final XmppURI uri, final String affiliation, final String role,
	    final Show show, final String statusMessage) {
	Occupant occupant = getOccupantByURI(uri);
	if (occupant == null) {
	    occupant = new Occupant(uri, affiliation, role, show, statusMessage);
	    occupantsByURI.put(occupant.getURI(), occupant);
	    eventBus.fireEvent(new RoomOccupantsChangedEvent(ChangeAction.ADDED, occupant));
	} else {
	    occupant.setAffiliation(affiliation);
	    occupant.setRole(role);
	    occupant.setShow(show);
	    occupant.setStatusMessage(statusMessage);
	    eventBus.fireEvent(new RoomOccupantsChangedEvent(ChangeAction.MODIFIED, occupant));
	}
	return occupant;
    }

    /**
     * Update my status to other occupants.
     * 
     * @param statusMessage
     * @param show
     */
    public void setStatus(final String statusMessage, final Show show) {
	final Presence presence = Presence.build(statusMessage, show);
	presence.setTo(uri);
	// presence.addChild("x", "http://jabber.org/protocol/muc");
	// presence.setPriority(0);
	session.send(presence);
    }

    /**
     * http://www.xmpp.org/extensions/xep-0045.html#subject-mod
     * 
     * @param subjectText
     */
    public void setSubject(final String subjectText) {
	final BasicStanza message = new BasicStanza("message", null);
	message.setFrom(session.getCurrentUser());
	message.setTo(getURI().getJID());
	message.setType(Message.Type.groupchat.toString());
	final IPacket subject = message.addChild("subject", null);
	subject.setText(subjectText);
	session.send(message);
    }

    @Override
    public String toString() {
	return "ROOM: " + uri;
    }

    protected Presence createEnterPresence(final HistoryOptions historyOptions) {
	final Presence presence = new Presence(null, null, getURI());
	final IPacket x = presence.addChild("x", "http://jabber.org/protocol/muc");
	presence.setPriority(0);
	if (historyOptions != null) {
	    final IPacket h = x.addChild("history");
	    if (historyOptions.maxchars >= 0) {
		h.setAttribute("maxchars", Integer.toString(historyOptions.maxchars));
	    }
	    if (historyOptions.maxstanzas >= 0) {
		h.setAttribute("maxstanzas", Integer.toString(historyOptions.maxstanzas));
	    }
	    if (historyOptions.seconds >= 0) {
		h.setAttribute("seconds", Long.toString(historyOptions.seconds));
	    }
	    if (historyOptions.since != null) {
		h.setAttribute("since", XmppDateTime.formatXMPPDateTime(historyOptions.since));
	    }
	}
	return presence;
    }

    protected void handlePresence(final XmppURI occupantURI, final Presence presence) {
	final Type type = presence.getType();
	if (type == Type.error || type == Type.unavailable && occupantURI.equals(getURI())) {
	    // TODO : add an error/out state ?
	    setChatState(ChatState.locked);
	} else if (type == Type.unavailable) {
	    removeOccupant(occupantURI);
	} else {
	    final List<? extends IPacket> children = presence.getChildren(ROOM_CREATED);
	    for (final IPacket child : children) {
		final IPacket item = child.getFirstChild("item");
		final String affiliation = item.getAttribute("affiliation");
		final String role = item.getAttribute("role");
		setOccupantPresence(occupantURI, affiliation, role, presence.getShow(), presence.getStatus());
		if (isNewRoom(child)) {
		    requestCreateInstantRoom();
		} else {
		    if (state != ChatState.ready) {
			setChatState(ChatState.ready);
		    }
		}
	    }
	}
    }

    protected boolean isNewRoom(final IPacket xtension) {
	final String code = xtension.getFirstChild("status").getAttribute("code");
	return code != null && code.equals("201");
    }

    @Override
    /**
     * WARNING : breaking change, need to check (message.getBody() != null)
     */
    protected void receive(final Message message) {
	if (message.getSubject() != null) {
	    final Occupant occupant = occupantsByURI.get(message.getFrom());
	    eventBus.fireEvent(new RoomSubjectChangedEvent(occupant, message.getSubject()));
	}
	if (message.getType() == Message.Type.error) {
	    GWT.log("Received Error message :" + message);
	    setChatState(ChatState.locked);
	}
	super.receive(message);
    }

    protected void requestCreateInstantRoom() {
	final IQ iq = new IQ(IQ.Type.set, getURI().getJID());
	iq.addQuery("http://jabber.org/protocol/muc#owner").addChild("x", "jabber:x:data").With("type", "submit");
	session.sendIQ("rooms", iq, new Listener<IPacket>() {
	    public void onEvent(final IPacket received) {
		if (IQ.isSuccess(received)) {
		    setChatState(ChatState.ready);
		}
	    }
	});
    }
}
