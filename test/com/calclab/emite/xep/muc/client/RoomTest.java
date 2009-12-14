package com.calclab.emite.xep.muc.client;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.im.client.chat.AbstractChat;
import com.calclab.emite.im.client.chat.AbstractChatTest;
import com.calclab.emite.im.client.chat.Chat;
import com.calclab.emite.im.client.chat.Chat.State;
import com.calclab.emite.testing.MockedSession;
import com.calclab.suco.testing.events.MockedListener;
import com.calclab.suco.testing.events.MockedListener2;

public class RoomTest extends AbstractChatTest {

    private Room room;
    private XmppURI userURI;
    private XmppURI roomURI;
    private MockedSession session;

    @Before
    public void beforeTests() {
	userURI = uri("user@domain/res");
	roomURI = uri("room@domain/nick");
	session = new MockedSession(userURI);
	room = new Room(session, roomURI, userURI);
    }

    @Override
    public AbstractChat getChat() {
	return room;
    }

    @Test
    public void shouldAddOccupantAndFireListeners() {
	final MockedListener<Collection<Occupant>> listener = new MockedListener<Collection<Occupant>>();
	room.onOccupantsChanged(listener);
	final XmppURI uri = uri("room@domain/name");
	final Occupant occupant = room.setOccupantPresence(uri, "aff", "role");
	assertTrue(listener.isCalledOnce());
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
	final MockedListener<State> stateChanged = new MockedListener<Chat.State>();
	room.onStateChanged(stateChanged);
	openInstantRoom(roomURI);
	assertTrue(stateChanged.isCalledOnce());
    }

    @Test
    public void shouldExitAndLockTheRoomWhenLoggedOut() {
	openInstantRoom(roomURI);
	session.logout();
	assertEquals(Chat.State.locked, room.getState());
	session.verifySent("<presence to='room@domain/nick' type='unavailable'/>");
    }

    @Test
    public void shouldFireListenersWhenMessage() {
	final MockedListener<Message> listener = new MockedListener<Message>();
	room.onMessageReceived(listener);
	final Message message = new Message(uri("someone@domain/res"), uri("room@domain"), "message");
	room.receive(message);
	assertTrue(listener.isCalledWithEquals(message));
    }

    @Test
    public void shouldFireListenersWhenSubjectChange() {
	final MockedListener<Message> messageListener = new MockedListener<Message>();
	room.onMessageReceived(messageListener);
	final MockedListener2<Occupant, String> subjectListener = new MockedListener2<Occupant, String>();
	room.onSubjectChanged(subjectListener);

	final XmppURI occupantURI = uri("someone@domain/res");
	room.receive(new Message(occupantURI, uri("room@domain"), null).Subject("the subject"));
	assertEquals(1, subjectListener.getCalledTimes());
	final Occupant occupant = room.getOccupantByURI(occupantURI);
	assertTrue(subjectListener.isCalledWithSame(occupant, "the subject"));
	assertEquals(0, messageListener.getCalledTimes());
    }

    @Test
    public void shouldRemoveOccupant() {
	final MockedListener<Collection<Occupant>> listener = new MockedListener<Collection<Occupant>>();
	room.onOccupantsChanged(listener);
	final XmppURI uri = uri("room@domain/name");
	room.setOccupantPresence(uri, "owner", "participant");
	assertEquals(1, room.getOccupantsCount());
	room.removeOccupant(uri);
	assertEquals(0, room.getOccupantsCount());
	assertEquals(2, listener.getCalledTimes());
	assertNull(room.getOccupantByURI(uri));
    }

    @Test
    public void shouldSendRoomInvitation() {
	room.sendInvitationTo("otherUser@domain/resource", "this is the reason");
	session.verifySent("<message from='" + userURI + "' to='" + roomURI.getJID()
		+ "'><x xmlns='http://jabber.org/protocol/muc#user'>"
		+ "<invite to='otherUser@domain/resource'><reason>this is the reason</reason></invite></x></message>");
    }

    @Test
    public void shouldSendRoomPresenceWhenCreated() {
	session.verifySent("<presence to='room@domain/nick'><x xmlns='http://jabber.org/protocol/muc' /></presence>");
    }

    @Test
    public void shouldUpdateOccupantAndFireListeners() {
	final MockedListener<Occupant> listener = new MockedListener<Occupant>();
	room.onOccupantModified(listener);
	final XmppURI uri = uri("room@domain/name");
	final Occupant occupant = room.setOccupantPresence(uri, "owner", "participant");
	final Occupant occupant2 = room.setOccupantPresence(uri, "admin", "moderator");
	assertEquals(1, listener.getCalledTimes());
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
