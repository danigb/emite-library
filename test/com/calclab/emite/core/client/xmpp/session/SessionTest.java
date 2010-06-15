package com.calclab.emite.core.client.xmpp.session;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;
import static com.calclab.suco.testing.events.Eventito.anyListener;
import static com.calclab.suco.testing.events.Eventito.fire;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.calclab.emite.core.client.events.DefaultEmiteEventBus;
import com.calclab.emite.core.client.packet.Packet;
import com.calclab.emite.core.client.xmpp.resource.ResourceBindingManager;
import com.calclab.emite.core.client.xmpp.sasl.AuthorizationEvent;
import com.calclab.emite.core.client.xmpp.sasl.SASLManager;
import com.calclab.emite.core.client.xmpp.session.XmppSession.SessionState;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.xtesting.ConnectionTester;
import com.calclab.suco.client.events.Listener;
import com.calclab.suco.testing.events.MockedListener;

public class SessionTest {

    private DefaultXmppSession session;
    private SASLManager saslManager;
    private ResourceBindingManager bindingManager;
    private IMSessionManager iMSessionManager;
    private ConnectionTester connection;
    private DefaultEmiteEventBus eventBus;

    @Before
    public void beforeTest() {
	connection = new ConnectionTester();
	eventBus = new DefaultEmiteEventBus();
	saslManager = mock(SASLManager.class);
	bindingManager = mock(ResourceBindingManager.class);
	iMSessionManager = mock(IMSessionManager.class);
	session = new DefaultXmppSession(eventBus, connection, saslManager, bindingManager, iMSessionManager);

    }

    @Test
    public void shouldConnectOnLogin() {
	assertFalse(connection.isConnected());
	session.login(uri("name@domain/resource"), "password");
	assertTrue(connection.isConnected());
    }

    @Test
    public void shouldEventMessages() {
	final MockedListener<Message> listener = new MockedListener<Message>();
	session.onMessage(listener);
	connection.receives(new Packet("message"));
	assertTrue(listener.isCalledOnce());
    }

    @Test
    public void shouldEventPresences() {
	final MockedListener<Presence> listener = new MockedListener<Presence>();
	session.onPresence(listener);
	connection.receives(new Packet("presence"));
	assertTrue(listener.isCalledOnce());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldEventStateChanges() {
	final Listener<Session> listener = mock(Listener.class);
	session.onStateChanged(listener);
	session.setSessionState(SessionState.ready);
	verify(listener).onEvent(same(session));
    }

    @Test
    public void shouldHandleFailedAuthorizationResult() {
	connection.connect();
	eventBus.fireEvent(new AuthorizationEvent());
	assertFalse(connection.isConnected());
    }

    @Test
    public void shouldHandleSucceedAuthorizationResult() {
	eventBus.fireEvent(new AuthorizationEvent(
		new Credentials(uri("node@domain"), "pass", Credentials.ENCODING_NONE)));

	assertEquals(SessionState.authorized, session.getSessionState());
	assertTrue(connection.isStreamRestarted());
	verify(bindingManager).bindResource(anyString());
    }

    @Test
    public void shouldLoginWhenSessionCreated() {

	final MockedListener<Session> onStateChanged = new MockedListener<Session>();
	session.onStateChanged(onStateChanged);

	createSession(uri("name@domain/resource"));
	assertTrue(onStateChanged.isCalledWithEquals(session));
    }

    @Test
    public void shouldQueueOutcomingStanzas() {
	assertEquals(0, connection.getSentSize());
	session.send(new Message("the Message", uri("other@domain")));
	assertEquals(0, connection.getSentSize());
	createSession(uri("name@domain/resource"));
	session.setReady();
	assertEquals(1, connection.getSentSize());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRequestSessionWhenBinded() {
	final XmppURI uri = uri("name@domain/resource");
	fire(uri).when(bindingManager).onBinded(anyListener());
	verify(iMSessionManager).requestSession(same(uri));
    }

    @Test
    public void shouldStopAndDisconnectWhenLoggedOut() {
    }

    @SuppressWarnings("unchecked")
    private void createSession(final XmppURI uri) {
	fire(uri).when(iMSessionManager).onSessionCreated(anyListener());
    }
}
