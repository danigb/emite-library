package com.calclab.emite.xxamples.im.chat.client;

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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExampleIMChat implements EntryPoint {

    private VerticalPanel output;
    private TextBox input;

    @Override
    public void onModuleLoad() {
	createUI();

	log("Example IM Chat");
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
	input.addChangeHandler(new ChangeHandler() {
	    @Override
	    public void onChange(final ChangeEvent event) {
		final String msg = input.getText();
		log("Message sent: " + msg);
		final Chat chat = chatManager.open(uri(user));
		chat.send(new Message(msg));
		input.setText("");
	    }
	});

	final Chat chat = chatManager.open(uri(user));

	chat.addMessageReceivedHandler(new MessageHandler() {
	    @Override
	    public void onPacketEvent(final MessageEvent event) {
		log("Message received: " + event.getMessage().getBody());
	    }
	});

    }

    private void createUI() {
	final DockPanel dock = new DockPanel();
	input = new TextBox();
	dock.add(input, DockPanel.SOUTH);
	output = new VerticalPanel();
	dock.add(output, DockPanel.SOUTH);
	RootPanel.get("app").add(dock);
    }

    private void log(final String text) {
	output.add(new Label(text));
    }

}
