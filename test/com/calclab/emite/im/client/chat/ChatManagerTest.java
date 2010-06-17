package com.calclab.emite.im.client.chat;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.im.client.chat.Chat.ChatState;
import com.calclab.emite.xep.chatstate.client.ChatStateManager;
import com.calclab.emite.xtesting.handlers.MessageTestHandler;
import com.calclab.emite.xtesting.handlers.StateChangedTestHandler;
import com.calclab.suco.testing.events.MockedListener;

public class ChatManagerTest extends AbstractChatManagerTest {

    @Test
    public void chatStateDontFireOnChatCreatedButMustAfterOpenChat() {
	final Message message = new Message(OTHER, MYSELF, null);
	message.addChild("gone", ChatStateManager.XMLNS);

	final MockedListener<Chat> listener = addOnChatCreatedListener();
	session.receives(message);
	assertTrue(listener.isNotCalled());
	manager.open(OTHER);
	assertTrue(listener.isCalled());
    }

    @Test
    public void managerShouldCreateOneChatForSameResource() {
	final MockedListener<Chat> listener = addOnChatCreatedListener();
	session.receives(new Message(uri("source@domain/resource1"), MYSELF, "message 1"));
	session.receives(new Message(uri("source@domain/resource1"), MYSELF, "message 2"));
	assertEquals(1, listener.getCalledTimes());
    }

    @Test
    public void oneToOneChatsAreAlwaysReadyWhenCreated() {
	final Chat chat = manager.open(uri("other@domain/resource"));
	assertSame(ChatState.ready, chat.getChatState());
    }

    @Test
    public void roomInvitationsShouldDontFireOnChatCreated() {
	final MockedListener<Chat> listener = addOnChatCreatedListener();
	session.receives("<message to='" + MYSELF
		+ "' from='someroom@domain'><x xmlns='http://jabber.org/protocol/muc#user'>" + "<invite from='" + OTHER
		+ "'><reason>Join to our conversation</reason></invite>"
		+ "</x><x jid='someroom@domain' xmlns='jabber:x:conference' /></message>");
	assertTrue(listener.isNotCalled());
    }

    @Test
    public void roomInvitationsShouldDontFireOnChatCreatedButMustAfterOpenChat() {
	final MockedListener<Chat> listener = addOnChatCreatedListener();
	session.receives("<message to='" + MYSELF
		+ "' from='someroom@domain'><x xmlns='http://jabber.org/protocol/muc#user'>" + "<invite from='" + OTHER
		+ "'><reason>Join to our conversation</reason></invite>"
		+ "</x><x jid='someroom@domain' xmlns='jabber:x:conference' /></message>");
	assertTrue(listener.isNotCalled());
	manager.open(OTHER);
	assertTrue(listener.isCalled());
    }

    @Test
    public void shouldBeInitiatedByOtherIfMessageArrives() {
	session.receives("<message to='" + MYSELF + "' from='someone@domain'><body>the body</body></message>");
	final Chat chat = manager.open(uri("someone@domain"));
	assertFalse(chat.isInitiatedByMe());
    }

    @Test
    public void shouldBlockChatWhenClosingIt() {
	final Chat chat = manager.open(uri("other@domain/resource"));
	manager.close(chat);
	assertSame(ChatState.locked, chat.getChatState());
    }

    @Test
    public void shouldCloseChatWhenLoggedOut() {
	final Chat chat = manager.open(uri("name@domain/resouce"));

	final StateChangedTestHandler handler = new StateChangedTestHandler();
	chat.addStateChangedHandler(handler);
	session.logout();
	assertEquals(ChatState.locked, handler.getEventState());
    }

    @Test
    public void shouldEventIncommingMessages() {
	final Chat chat = manager.open(uri("someone@domain"));

	final MessageTestHandler handler = new MessageTestHandler();
	chat.addMessageReceivedHandler(handler);

	session.receives("<message type='chat' id='purplee8b92642' to='user@domain' "
		+ "from='someone@domain'><x xmlns='jabber:x:event'/><active"
		+ "xmlns='http://jabber.org/protocol/chatstates'/></message>");
	assertTrue(handler.hasEvent());
    }

    @Test
    public void shouldOpenDifferentsChatsForDifferentDomains() {
	final Chat chatCom = manager.open(uri("COM@domain.com"));

	final MessageTestHandler handlerCom = new MessageTestHandler();
	chatCom.addMessageReceivedHandler(handlerCom);
	assertFalse(handlerCom.hasEvent());

	final Chat chatOrg = manager.open(uri("ORG@domain.org"));
	final MessageTestHandler handlerOrg = new MessageTestHandler();
	chatOrg.addMessageReceivedHandler(handlerOrg);
	assertFalse(handlerOrg.hasEvent());

	session.receives(new Message(uri("COM@domain.com"), MYSELF, "message com 2"));
	assertTrue("com has one message", handlerCom.hasEvent());
	assertFalse("org has no message", handlerOrg.hasEvent());

    }

    @Test
    public void shouldReuseChatIfNotResouceSpecified() {
	final MockedListener<Chat> listener = addOnChatCreatedListener();
	session.receives(new Message(uri("source@domain"), MYSELF, "message 1"));
	session.receives(new Message(uri("source@domain/resource1"), MYSELF, "message 2"));
	assertTrue(listener.isCalled(1));
    }

    @Test
    public void shouldReuseChatWhenAnsweringWithDifferentResources() {
	final MockedListener<Chat> listener = addOnChatCreatedListener();
	final Chat chat = manager.open(uri("someone@domain"));
	assertTrue(listener.isCalledOnce());
	assertTrue(listener.isCalledWithSame(chat));
	session.receives(new Message(uri("someone@domain/resource"), MYSELF, "answer"));
	assertTrue(listener.isCalled(1));
    }

    private MockedListener<Chat> addOnChatCreatedListener() {
	final MockedListener<Chat> listener = new MockedListener<Chat>();
	manager.onChatCreated(listener);
	return listener;
    }

    @Override
    protected PairChatManager createChatManager() {
	final PairChatManager chatManagerDefault = new PairChatManager(session.getEventBus(), session);
	return chatManagerDefault;
    }
}
