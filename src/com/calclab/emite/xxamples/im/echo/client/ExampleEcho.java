package com.calclab.emite.xxamples.im.echo.client;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;

import com.calclab.emite.browser.client.PageAssist;
import com.calclab.emite.core.client.events.MessageEvent;
import com.calclab.emite.core.client.events.MessageHandler;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.im.client.chat.Chat;
import com.calclab.emite.im.client.chat.ChatManager;
import com.calclab.suco.client.Suco;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExampleEcho implements EntryPoint {

    private VerticalPanel output;

    @Override
    public void onModuleLoad() {
	output = new VerticalPanel();
	RootPanel.get("app").add(output);

	log("Example echo chat");
	final String self = PageAssist.getMeta("emite.user");
	log("Current user: " + self);
	final String user = PageAssist.getMeta("emite.chat");
	log("Chat with user: " + user);

	final XmppSession session = Suco.get(XmppSession.class);

	session.addSessionStateChangedHandler(new StateChangedHandler() {
	    @Override
	    public void onStateChanged(final StateChangedEvent event) {
		final String state = session.getSessionState();
		log("Current state: " + state);
	    }
	});

	final ChatManager chatManager = Suco.get(ChatManager.class);
	final Chat chat = chatManager.open(uri(user));

	chat.addMessageReceivedHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		final Message msg = event.getMessage();
		final String body = msg.getBody();
		log("Message received: " + body);
		chat.send(new Message(body + " at: " + System.currentTimeMillis()));
	    }
	});

    }

    private void log(final String text) {
	output.add(new Label(text));
    }

}
