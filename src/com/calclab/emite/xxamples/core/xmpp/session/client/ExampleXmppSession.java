package com.calclab.emite.xxamples.core.xmpp.session.client;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;

import com.calclab.emite.browser.client.EmiteBrowserGinjector;
import com.calclab.emite.core.client.EmiteCoreGinjector;
import com.calclab.emite.core.client.events.MessageEvent;
import com.calclab.emite.core.client.events.MessageHandler;
import com.calclab.emite.core.client.events.PresenceEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
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
	    final XmppSession session = gin.getSession();

	    /*
	     * We track session state changes. We can only send messages when
	     * the state == loggedIn.
	     */
	    session.addStateChangedHandler(new StateChangedHandler() {
		@Override
		public void onStateChanged(final StateChangedEvent event) {
		    final String state = event.getState();
		    if (state == XmppSession.SessionState.loggedIn) {
			log("We are now online");
			// The simplest way to send a messsage
			final Message message = new Message("hello world!", uri("everybody@world.org"));
			session.send(message);
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
	    session.addIncomingMessageHandler(new MessageHandler() {
		@Override
		public void onPacketEvent(final MessageEvent event) {
		    final Message message = event.getMessage();
		    log("Messaged received from " + message.getFrom() + ":" + message.getBody());
		}

	    });

	    /*
	     * We show (log) every incoming presence stanzas
	     */
	    session.addIncomingPresenceHandler(new PresenceHandler() {
		@Override
		public void onIncomingPresence(final PresenceEvent event) {
		    final Presence presence = event.getPresence();
		    log("Presence received from " + presence.getFrom() + ": " + presence.toString());
		}
	    });
	}
    }

    private void log(final String text) {
	panel.add(new Label(text));
    }

}
