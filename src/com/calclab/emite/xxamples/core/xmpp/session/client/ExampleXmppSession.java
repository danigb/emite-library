package com.calclab.emite.xxamples.core.xmpp.session.client;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;

import com.calclab.emite.browser.client.EmiteBrowserGinjector;
import com.calclab.emite.core.client.EmiteCoreGinjector;
import com.calclab.emite.core.client.xmpp.session.Session;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Xmpp Session Example
 */
public class ExampleXmppSession implements EntryPoint {

    public static interface ExampleGinjector extends EmiteCoreGinjector, EmiteBrowserGinjector {

    }

    private VerticalPanel panel;

    public void onModuleLoad() {
	{
	    panel = new VerticalPanel();
	    RootPanel.get("app").add(panel);
	    log("Emite example: xmpp sessions");

	    /*
	     * We get the Session object. The most important object of Emite
	     * Core module.
	     */
	    GWT.log("Create session");
	    final ExampleGinjector gin = GWT.create(ExampleGinjector.class);
	    gin.getAutoConfig();
	    final Session session = gin.getSession();

	    /*
	     * We track session state changes. We can only send messages when
	     * the state == loggedIn.
	     */
	    session.onStateChanged(new Listener<Session>() {
		@Override
		public void onEvent(final Session session) {
		    final XmppSession.SessionState state = session.getSessionState();
		    if (state == XmppSession.SessionState.loggedIn) {
			log("We are now online");
			sendHelloWorldMessage(session);
		    } else if (state == XmppSession.SessionState.disconnected) {
			log("We are now offline");
		    } else {
			log("Current state: " + state);
		    }
		}
	    });

	    /*
	     * We show every incoming message in the GWT log console
	     */
	    session.onMessage(new Listener<Message>() {
		public void onEvent(final Message message) {
		    log("Messaged received from " + message.getFrom() + ":" + message.getBody());
		}
	    });

	    /*
	     * We show (log) every incoming presence stanzas
	     */
	    session.onPresence(new Listener<Presence>() {
		public void onEvent(final Presence presence) {
		    log("Presence received from " + presence.getFrom() + ": " + presence.toString());
		}
	    });
	}
    }

    private void log(final String text) {
	panel.add(new Label(text));
    }

    /**
     * The simplest way to send a message using the Session object
     */
    private void sendHelloWorldMessage(final Session session) {
	final Message message = new Message("hello world!", uri("everybody@world.org"));
	session.send(message);
    }
}
