package com.calclab.emite.reconnect.client;

import com.calclab.emite.core.client.conn.ConnectionEvent;
import com.calclab.emite.core.client.conn.ConnectionHandler;
import com.calclab.emite.core.client.conn.XmppConnection;
import com.calclab.emite.core.client.conn.ConnectionEvent.EventType;
import com.calclab.emite.core.client.xmpp.sasl.AuthorizationEvent;
import com.calclab.emite.core.client.xmpp.sasl.AuthorizationHandler;
import com.calclab.emite.core.client.xmpp.sasl.SASLManager;
import com.calclab.emite.core.client.xmpp.session.Credentials;
import com.calclab.emite.core.client.xmpp.session.Session;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;

public class SessionReconnect {
    private boolean shouldReconnect;
    private Credentials lastSuccessfulCredentials;
    protected int reconnectionAttempts;

    public SessionReconnect(final XmppConnection connection, final Session session, final SASLManager saslManager) {
	shouldReconnect = false;
	reconnectionAttempts = 0;
	GWT.log("RECONNECT BEHAVIOUR");

	saslManager.addAuthorizationHandler(new AuthorizationHandler() {
	    @Override
	    public void onAuthorization(final AuthorizationEvent event) {
		if (event.isSucceed()) {
		    lastSuccessfulCredentials = event.getCredentials();
		}
	    }
	});

	session.onStateChanged(new Listener<Session>() {
	    @Override
	    public void onEvent(final Session session) {
		final XmppSession.SessionState sessionState = session.getSessionState();
		if (sessionState == XmppSession.SessionState.connecting) {
		    shouldReconnect = false;
		} else if (sessionState == XmppSession.SessionState.disconnected && shouldReconnect) {
		    if (lastSuccessfulCredentials != null) {
			final double seconds = Math.pow(2, reconnectionAttempts - 1);
			new Timer() {
			    @Override
			    public void run() {
				GWT.log("Reconnecting...");
				if (shouldReconnect) {
				    shouldReconnect = false;
				    session.login(lastSuccessfulCredentials);
				}
			    }
			}.schedule((int) (1000 * seconds));
			GWT.log("Reconnecting in " + seconds + " seconds.");
		    }
		} else if (sessionState == XmppSession.SessionState.ready) {
		    GWT.log("CLEAR RECONNECTION ATTEMPS");
		    reconnectionAttempts = 0;
		}
	    }
	});

	connection.addConnectionHandler(new ConnectionHandler() {
	    @Override
	    public void onStateChanged(final ConnectionEvent event) {
		// TODO: new semantics, refactor
		if (event.is(EventType.error)) {
		    shouldReconnect();
		} else if (event.is(EventType.beforeRetry)) {
		    shouldReconnect();

		}
	    }
	});

    }

    private void shouldReconnect() {
	shouldReconnect = true;
	reconnectionAttempts++;
    }
}
