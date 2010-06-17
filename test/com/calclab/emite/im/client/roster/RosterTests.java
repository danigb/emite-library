package com.calclab.emite.im.client.roster;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.calclab.emite.core.client.events.ChangedEvent.ChangeAction;
import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.IQ.Type;
import com.calclab.emite.xtesting.SessionTester;
import com.calclab.emite.xtesting.handlers.RosterItemChangedTestHandler;
import com.calclab.emite.xtesting.handlers.StateChangedTestHandler;

public class RosterTests {

    private SessionTester session;
    private Roster roster;

    @Test
    public void addRosterStep1_shouldRequestAddItem() {
	roster.requestAddItem(uri("friend@domain/anyResource"), "MyFriend", "Group1", "Group2");
	session.verifyIQSent("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='MyFriend'><group>Group1</group><group>Group2</group>"
		+ "</item></query></iq>");
    }

    @Test
    public void addRosterStep2_shouldAddItemFireListenerAndSendResponse() {

	final RosterItemChangedTestHandler handler = new RosterItemChangedTestHandler();
	roster.addRosterItemChangedHandler(handler);

	session.receives("<iq type='set' from='someone@domain' id='theId'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='MyFriend'><group>Group1</group><group>Group2</group>"
		+ "</item></query></iq>");
	assertTrue(handler.hasEvent());
	assertEquals(1, roster.getItems().size());
	final RosterItem item = asList(roster.getItems()).get(0);
	assertEquals("friend@domain", item.getJID().toString());
	assertEquals("MyFriend", item.getName());
	assertEquals(2, item.getGroups().size());
	session.verifySent("<iq type='result' to='someone@domain' id='theId'/>");
    }

    @Before
    public void beforeTests() {
	session = new SessionTester();
	roster = new XmppRoster(session.getEventBus(), session);
    }

    @Test
    public void shouldFindRosterItemByJID() {
	shouldRequestRosterOnLogin();
	session.answer(serverRoster());

	final RosterItem item = roster.getItemByJID(uri("romeo@example.net"));
	assertNotNull(item);
	assertSame(item, roster.getItemByJID(uri("romeo@example.net/RESOURCE")));
    }

    @Test
    public void shouldFireEventOnlyWhenRosterReady() {

	final StateChangedTestHandler handler = new StateChangedTestHandler();
	roster.addRosterStateChangedHandler(handler);

	shouldRequestRosterOnLogin();
	session.answer(new IQ(Type.error));
	assertFalse(handler.hasEvent());
    }

    @Test
    public void shouldFireEventWhenRosterReady() {
	final StateChangedTestHandler handler = new StateChangedTestHandler();
	roster.addRosterStateChangedHandler(handler);

	shouldRequestRosterOnLogin();
	session.answer(serverRoster());
	assertTrue(handler.isCalledOnce());
    }

    @Test
    public void shouldHandleInitialPresence() {
	session.receives("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='MyFriend' /></query></iq>");

	final RosterItemChangedTestHandler handler = new RosterItemChangedTestHandler();
	roster.addRosterItemChangedHandler(handler);

	session.receives("<presence from='friend@domain' />");
	final RosterItem item = roster.getItemByJID(uri("friend@domain"));
	assertTrue(handler.isCalledOnce());
	assertEquals(ChangeAction.MODIFIED, handler.getChangeType());
	assertSame(item, handler.getItem());
	assertEquals(true, item.isAvailable());
    }

    @Test
    public void shouldHandlePresenceInformationOnRosterItems() {
	final RosterItemChangedTestHandler handler = new RosterItemChangedTestHandler();
	roster.addRosterItemChangedHandler(handler);

	session.receives("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='MyFriend' /></query></iq>");
	session.receives("<presence from='friend@domain'>"
		+ "<show>dnd</show><status>message</status><priority>3</priority></presence>");
	final RosterItem item = roster.getItemByJID(uri("friend@domain"));
	assertEquals(ChangeAction.MODIFIED, handler.getChangeType());
	assertSame(item, handler.getItem());
	assertEquals(Presence.Show.dnd, item.getShow());
	assertEquals("message", item.getStatus());
    }

    @Test
    public void shouldHandleUnavailablePresence() {
	final RosterItemChangedTestHandler handler = new RosterItemChangedTestHandler();
	roster.addRosterItemChangedHandler(handler);

	session.receives("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='MyFriend' /></query></iq>");
	session.receives("<presence type='unavailable' from='friend@domain' />");
	final RosterItem item = roster.getItemByJID(uri("friend@domain"));
	assertEquals(ChangeAction.MODIFIED, handler.getChangeType());
	assertEquals(item, handler.getItem());
	assertEquals(false, item.isAvailable());

    }

    @Test
    public void shouldRemoveItems() {
	final RosterItemChangedTestHandler handler = new RosterItemChangedTestHandler();
	roster.addRosterItemChangedHandler(handler);

	session.receives("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='MyFriend'><group>Group1</group><group>Group2</group>"
		+ "</item></query></iq>");
	assertEquals(1, roster.getItems().size());
	assertEquals(3, roster.getGroupNames().size());
	session.receives("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' subscription='remove' name='MyFriend'><group>Group1</group>"
		+ "</item></query></iq>");
	assertEquals(ChangeAction.REMOVED, handler.getChangeType());
	assertEquals(0, roster.getItems().size());
	assertEquals(1, roster.getGroupNames().size());
    }

    @Test
    public void shouldRequestRemoveItem() {
	roster.removeItem(uri("friend@domain"));
	session.verifyNotSent("<iq />");
	session.receives("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='MyFriend'><group>Group1</group><group>Group2</group>"
		+ "</item></query></iq>");
	roster.removeItem(uri("friend@domain"));
	session.verifyIQSent("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' subscription='remove'/></query></iq>");
    }

    @Test
    public void shouldRequestRosterOnLogin() {
	session.setLoggedIn("user@domain/resource");
	session.verifyIQSent("<iq type='get' ><query xmlns='jabber:iq:roster'/></iq>");
    }

    @Test
    public void shouldRequestUpdateItem() {
	session.receives("<iq type='set' id='theId'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='MyFriend'><group>Group1</group><group>Group2</group>"
		+ "</item></query></iq>");
	session.verifySent("<iq type='result' id='theId' />");

	final RosterItem item = roster.getItemByJID(uri("friend@domain"));
	assertNotNull(item);
	roster.updateItem(uri("no@one"), "name", "group");
	roster.updateItem(uri("friend@domain"), "MyOldFriend", "Group1", "Group3");
	session.verifyIQSent("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='MyOldFriend'><group>Group1</group><group>Group3</group>"
		+ "</item></query></iq>");
    }

    @Test
    public void shouldRetrieveItemsByGroup() {
	shouldRequestRosterOnLogin();
	session.answer(serverRoster());
	assertEquals(2, roster.getItemsByGroup("Friends").size());
	assertEquals(1, roster.getItemsByGroup("Work").size());
	assertEquals(1, roster.getItemsByGroup("X").size());
    }

    @Test
    public void shouldRetrieveTheGroups() {
	shouldRequestRosterOnLogin();
	session.answer(serverRoster());
	final Set<String> groups = roster.getGroupNames();
	assertNotNull(groups);
	assertEquals(4, groups.size());
	assertTrue(groups.contains("Friends"));
	assertTrue(groups.contains("X"));
	assertTrue(groups.contains("Work"));
    }

    @Test
    public void shouldSetPresenceUnavailableOnRosterReception() {
	session.receives("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='MyFriend' /></query></iq>");
	final RosterItem item = roster.getItemByJID(uri("friend@domain"));
	assertNotNull(item);
	// FIXME: not sure of this
	// assertEquals(Presence.Type.unavailable,
	// item.getPresence().getType());
    }

    @Test
    public void shouldUpdateItem() {

	final RosterItemChangedTestHandler handler = new RosterItemChangedTestHandler();
	roster.addRosterItemChangedHandler(handler);

	session.receives("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='Friend1'><group>GG1</group><group>GG2</group>"
		+ "</item></query></iq>");
	session.receives("<iq type='set'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='friend@domain' name='Friend2'><group>HH1</group><group>HH2</group>"
		+ "</item></query></iq>");
	assertEquals(ChangeAction.MODIFIED, handler.getChangeType());
	assertEquals(1, roster.getItems().size());
	assertEquals(3, roster.getGroupNames().size());
	assertTrue(roster.getGroupNames().contains("HH1"));
	assertTrue(roster.getGroupNames().contains("HH2"));
    }

    private <T> ArrayList<T> asList(final Collection<? extends T> items) {
	final ArrayList<T> array = new ArrayList<T>();
	array.addAll(items);
	return array;
    }

    private String serverRoster() {
	return "<iq to='juliet@example.com/balcony' type='result'><query xmlns='jabber:iq:roster'>"
		+ "<item jid='romeo@example.net' name='R' subscription='both'><group>Friends</group><group>X</group></item>"
		+ "<item jid='mercutio@example.org' name='M' subscription='from'> <group>Friends</group></item>"
		+ "<item jid='benvolio@example.org' name='B' subscription='both'><group>Work</group></item>"
		+ "</query></iq>";
    }
}
