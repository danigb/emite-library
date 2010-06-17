package com.calclab.emite.im.client.chat;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.im.client.chat.Chat.ChatState;
import com.calclab.emite.xtesting.SessionTester;
import com.calclab.emite.xtesting.handlers.MessageTestHandler;
import com.calclab.emite.xtesting.handlers.StateChangedTestHandler;

public class ChatTest extends AbstractChatTest {
    private static final XmppURI CHAT_URI = uri("other@domain/other");
    private static final XmppURI USER_URI = uri("self@domain/res");
    private PairChat pairChat;
    private SessionTester session;

    @Before
    public void beforeTests() {
	session = new SessionTester(USER_URI);
	pairChat = new PairChat(session, CHAT_URI, USER_URI, "theThread");
    }

    @Override
    public AbstractChat getChat() {
	return pairChat;
    }

    @Test
    public void shouldBeReadyIfSessionLogedIn() {
	final PairChat aChat = new PairChat(session, uri("someone@domain"), USER_URI, null);
	assertEquals(ChatState.ready, aChat.getChatState());
    }

    @Test
    public void shouldLockIfLogout() {
	final StateChangedTestHandler handler = new StateChangedTestHandler();
	pairChat.addStateChangedHandler(handler);
	session.logout();
	assertEquals(ChatState.locked, handler.getState());
	session.login(USER_URI, "");
	assertEquals(ChatState.ready, handler.getState());
    }

    @Test
    public void shouldLockIfReLoginWithDifferentJID() {
	session.logout();
	session.login(uri("differentUser@domain"), "");
	assertEquals(ChatState.locked, pairChat.getChatState());

    }

    @Test
    public void shouldReceiveMessages() {
	final MessageTestHandler handler = new MessageTestHandler();
	pairChat.addMessageReceivedHandler(handler);
	session.receives(new Message(CHAT_URI, USER_URI, "the body"));
	assertTrue("should receive messages", handler.hasEvent());
	assertEquals("the body", handler.getMessage().getBody());
    }

    @Test
    public void shouldSendNoThreadWhenNotSpecified() {
	final AbstractChat noThreadChat = new PairChat(session, CHAT_URI, USER_URI, null);
	noThreadChat.setChatState(ChatState.ready);
	noThreadChat.send(new Message("the message"));
	session.verifySent("<message from='self@domain/res' to='other@domain/other' "
		+ "type='chat'><body>the message</body></message>");
    }

    @Test
    public void shouldSendThreadWhenSpecified() {
	pairChat.send(new Message("the message"));
	session.verifySent("<message from='self@domain/res' to='other@domain/other' type='chat'>"
		+ "<body>the message</body><thread>theThread</thread></message>");
    }

    @Test
    public void shouldSendValidChatMessages() {
	pairChat.send(new Message(uri("from@uri"), uri("to@uri"), "this is the body").Thread("otherThread").Type(
		Message.Type.groupchat));
	session.verifySent("<message from='self@domain/res' to='other@domain/other' type='chat'>"
		+ "<body>this is the body</body><thread>theThread</thread></message>");
    }

    @Test
    public void shouldUnlockIfReloginWithSameJID() {
	session.logout();
	session.login(XmppURI.uri(USER_URI.getNode(), USER_URI.getHost(), "different_resource"), "");
	assertEquals(ChatState.ready, pairChat.getChatState());
    }

    @Test
    public void shoultEscapeMessageBody() {
	pairChat.send(new Message("&"));
	session.verifySent("<message from='self@domain/res' to='other@domain/other' type='chat'>"
		+ "<body>&amp;</body><thread>theThread</thread></message>");
    }

}
