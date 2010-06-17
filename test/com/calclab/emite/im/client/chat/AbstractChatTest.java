package com.calclab.emite.im.client.chat;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.im.client.chat.Chat.ChatState;
import com.calclab.emite.xtesting.SessionTester;
import com.calclab.emite.xtesting.handlers.MessageTestHandler;

public abstract class AbstractChatTest {
    protected final SessionTester session;

    public AbstractChatTest() {
	session = new SessionTester();
    }

    public abstract AbstractChat getChat();

    @Test
    public void shouldInterceptIncomingMessages() {
	final AbstractChat chat = getChat();

	final MessageTestHandler handler = new MessageTestHandler();
	chat.addBeforeReceiveMessageHandler(handler);

	final Message message = new Message("body");
	chat.receive(message);
	assertTrue(handler.hasEvent());
    }

    @Test
    public void shouldInterceptOutcomingMessages() {
	final AbstractChat chat = getChat();
	final MessageTestHandler handler = new MessageTestHandler();
	chat.addBeforeSendMessageHandler(handler);
	final Message message = new Message("body");
	chat.send(message);
	assertTrue(handler.hasEvent());
    }

    @Test
    public void shouldNotSendMessagesWhenStatusIsNotReady() {
	final AbstractChat chat = getChat();
	chat.setChatState(ChatState.locked);
	chat.send(new Message("a message"));
	session.verifyNotSent("<message />");
    }

    @Test
    public void shouldSetNullData() {
	getChat().setData(null, null);
    }
}
