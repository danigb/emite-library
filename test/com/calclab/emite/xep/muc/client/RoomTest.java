package com.calclab.emite.xep.muc.client;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.calclab.emite.core.client.events.ChangedEvent.ChangeAction;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.core.client.xmpp.stanzas.Presence.Show;
import com.calclab.emite.im.client.chat.AbstractChat;
import com.calclab.emite.im.client.chat.AbstractChatTest;
import com.calclab.emite.im.client.chat.Chat.ChatState;
import com.calclab.emite.xtesting.SessionTester;
import com.calclab.emite.xtesting.handlers.MessageTestHandler;
import com.calclab.emite.xtesting.handlers.RoomOccupantsChangedTestHandler;
import com.calclab.emite.xtesting.handlers.RoomSubjectChangedTestHandler;
import com.calclab.emite.xtesting.handlers.StateChangedTestHandler;

public class RoomTest extends AbstractChatTest {

    private Room room;
    private XmppURI userURI;
    private XmppURI roomURI;
    private SessionTester session;

    @Before
    public void beforeTests() {
	userURI = uri("user@domain/res");
	roomURI = uri("room@domain/nick");
	session = new SessionTester(userURI);
	room = new Room(session, roomURI, userURI, null);
    }

    @Override
    public AbstractChat getChat() {
	return room;
    }

    @Test
    public void shouldAddOccupantAndFireListeners() {
	final RoomOccupantsChangedTestHandler handler = new RoomOccupantsChangedTestHandler();
	room.addRoomOccupantsChangedHandler(handler);

	final XmppURI uri = uri("room@domain/name");
	final Occupant occupant = room.setOccupantPresence(uri, "aff", "role", Show.unknown, null);
	assertTrue(handler.isCalledOnce());
	assertEquals(ChangeAction.ADDED, handler.getChangeType());
	final Occupant result = room.getOccupantByURI(uri);
	assertEquals(occupant, result);
    }

    @Test
    public void shouldChangeSubject() {
	room.setSubject("Some subject");
	session.verifySent("<message type=\"groupchat\" from=\"" + userURI + "\" to=\"" + room.getURI().getJID()
		+ "\"><subject>Some subject</subject></message></body>");
    }

    @Test
    public void shouldCreateInstantRooms() {
	final StateChangedTestHandler handler = new StateChangedTestHandler();
	room.addStateChangedHandler(handler);
	openInstantRoom(roomURI);
	assertTrue(handler.isCalledOnce());
	assertEquals(ChatState.ready, handler.getState());
    }

    @Test
    public void shouldExitAndLockTheRoomWhenLoggedOut() {
	openInstantRoom(roomURI);
	session.logout();
	assertEquals(ChatState.locked, room.getChatState());
	session.verifySent("<presence to='room@domain/nick' type='unavailable'/>");
    }

    @Test
    public void shouldFireListenersWhenMessage() {
	final MessageTestHandler handler = new MessageTestHandler();
	room.addMessageReceivedHandler(handler);

	final Message message = new Message(uri("someone@domain/res"), uri("room@domain"), "message");
	room.receive(message);
	assertEquals(message, handler.getMessage());
    }

    @Test
    public void shouldFireListenersWhenSubjectChange() {
	final RoomSubjectChangedTestHandler handler = new RoomSubjectChangedTestHandler();
	room.addRoomSubjectChangedHandler(handler);

	final XmppURI occupantURI = uri("someone@domain/res");
	room.receive(new Message(occupantURI, uri("room@domain"), null).Subject("the subject"));
	assertTrue(handler.isCalledOnce());
	final Occupant occupant = room.getOccupantByURI(occupantURI);
	assertEquals("the subject", handler.getSubject());
	assertSame(occupant, handler.getOccupant());
    }

    @Test
    public void shouldRemoveOccupant() {
	final RoomOccupantsChangedTestHandler handler = new RoomOccupantsChangedTestHandler();
	room.addRoomOccupantsChangedHandler(handler);

	final XmppURI uri = uri("room@domain/name");
	room.setOccupantPresence(uri, "owner", "participant", Show.notSpecified, null);
	assertEquals(1, room.getOccupantsCount());
	room.removeOccupant(uri);
	assertEquals(0, room.getOccupantsCount());
	assertEquals(ChangeAction.REMOVED, handler.getChangeType());
	assertNull(room.getOccupantByURI(uri));
    }

    @Test
    public void shouldSendRoomInvitation() {
	room.sendInvitationTo(uri("otherUser@domain/resource"), "this is the reason");
	session.verifySent("<message from='" + userURI + "' to='" + roomURI.getJID()
		+ "'><x xmlns='http://jabber.org/protocol/muc#user'>"
		+ "<invite to='otheruser@domain/resource'><reason>this is the reason</reason></invite></x></message>");
    }

    @Test
    public void shouldSendRoomPresenceWhenCreated() {
	session.verifySent("<presence to='room@domain/nick'><x xmlns='http://jabber.org/protocol/muc' /></presence>");
    }

    @Test
    public void shouldUpdateOccupantAndFireListeners() {
	final RoomOccupantsChangedTestHandler handler = new RoomOccupantsChangedTestHandler();
	room.addRoomOccupantsChangedHandler(handler);
	final XmppURI uri = uri("room@domain/name");
	final Occupant occupant = room.setOccupantPresence(uri, "owner", "participant", Show.notSpecified, null);
	final Occupant occupant2 = room.setOccupantPresence(uri, "admin", "moderator", Show.notSpecified, null);
	assertEquals(ChangeAction.MODIFIED, handler.getChangeType());
	assertSame(occupant, occupant2);
    }

    private void openInstantRoom(final XmppURI room) {
	session.receives("<presence to='user@domain/res' from='" + room + "'>"
		+ "<x xmlns='http://jabber.org/protocol/muc#user'>"
		+ "<item affiliation='owner' role='moderator'/><status code='201'/></x></presence>");
	session.verifyIQSent("<iq to='" + room.getJID() + "' type='set'>"
		+ "<query xmlns='http://jabber.org/protocol/muc#owner'>"
		+ "<x xmlns='jabber:x:data' type='submit'/></query></iq>");
	session.answerSuccess();
    }

}
