package com.calclab.emite.xep.chatstate.client;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;

import org.junit.Before;
import org.junit.Test;

import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.im.client.chat.Chat;
import com.calclab.emite.im.client.chat.PairChatManager;
import com.calclab.emite.xep.chatstate.client.ChatStateManager.ChatUserState;
import com.calclab.emite.xtesting.SessionTester;

public class ChatStateManagerTests {
    private static final XmppURI MYSELF = uri("self@domain/res");
    private static final XmppURI OTHER = uri("other@domain/other");

    private PairChatManager chatManager;
    private Chat chat;
    private ChatStateManager chatStateManager;
    private SessionTester session;

    @Before
    public void beforeTests() {
	session = new SessionTester();
	chatManager = new PairChatManager(session.getEventBus(), session);
	session.setLoggedIn(MYSELF);
	final StateManager stateManager = new StateManager(chatManager);
	chat = chatManager.open(OTHER);
	chatStateManager = stateManager.getChatState(chat);
    }

    @Test
    public void closeChatWithoutStartConversationMustNotThrowNPE() {
	// This was throwing a NPE:
	chatManager.close(chat);
    }

    @Test
    public void shouldNotRepiteState() {
	session.receives("<message from='other@domain/other' to='self@domain/res' type='chat'>"
		+ "<active xmlns='http://jabber.org/protocol/chatstates'/></message>");
	chatStateManager.setOwnChatUserState(ChatUserState.composing);
	chatStateManager.setOwnChatUserState(ChatUserState.composing);
	chatStateManager.setOwnChatUserState(ChatUserState.composing);
	session.verifySent("<message from='self@domain/res' to='other@domain/other' type='chat'>"
		+ "<composing xmlns='http://jabber.org/protocol/chatstates'/></message>");
	session.verifyNotSent("<message><composing/><active/></message>");
    }

    @Test
    public void shouldNotSendStateIfNegotiationNotAccepted() {
	chatStateManager.setOwnChatUserState(ChatUserState.composing);
	session.verifySentNothing();
    }

    @Test
    public void shouldSendActiveIfTheOtherStartNegotiation() {
	session.receives("<message from='other@domain/other' to='self@domain/res' type='chat'>"
		+ "<active xmlns='http://jabber.org/protocol/chatstates'/></message>");
	session.verifySent("<message from='self@domain/res' to='other@domain/other' type='chat'>"
		+ "<active xmlns='http://jabber.org/protocol/chatstates'/></message>");
    }

    @Test
    public void shouldSendStateIfNegotiationAccepted() {
	session.receives("<message from='other@domain/other' to='self@domain/res' type='chat'>"
		+ "<active xmlns='http://jabber.org/protocol/chatstates'/></message>");
	chatStateManager.setOwnChatUserState(ChatUserState.composing);
	session.verifySent("<message from='self@domain/res' to='other@domain/other' type='chat'>"
		+ "<composing xmlns='http://jabber.org/protocol/chatstates'/></message>");
    }

    @Test
    public void shouldSendTwoStateIfDiferent() {
	session.receives("<message from='other@domain/other' to='self@domain/res' type='chat'>"
		+ "<active xmlns='http://jabber.org/protocol/chatstates'/></message>");
	chatStateManager.setOwnChatUserState(ChatUserState.composing);
	chatStateManager.setOwnChatUserState(ChatUserState.pause);
	session.verifySent("<message from='self@domain/res' to='other@domain/other' type='chat'>"
		+ "<composing xmlns='http://jabber.org/protocol/chatstates'/></message>"
		+ "<message from='self@domain/res' to='other@domain/other' type='chat'>"
		+ "<pause xmlns='http://jabber.org/protocol/chatstates'/></message>");
    }

    @Test
    public void shouldStartStateAfterNegotiation() {
	chat.send(new Message("test message"));
	session.receives("<message from='other@domain/other' to='self@domain/res' type='chat'>"
		+ "<active xmlns='http://jabber.org/protocol/chatstates'/></message>");
	final Message message = new Message(MYSELF, OTHER, "test message");
	message.addChild(ChatUserState.active.toString(), ChatStateManager.XMLNS);
	chatStateManager.setOwnChatUserState(ChatStateManager.ChatUserState.composing);
	session.verifySent(message.toString() + "<message from='self@domain/res' to='other@domain/other' type='chat'>"
		+ "<composing xmlns='http://jabber.org/protocol/chatstates'/></message>");
    }

    @Test
    public void shouldStartStateNegotiation() {
	chat.send(new Message("test message"));
	chat.send(new Message("test message"));
	session.verifySent("<message><active xmlns='http://jabber.org/protocol/chatstates' /></message>");
    }

    @Test
    public void shouldStartStateNegotiationOnce() {
	chat.send(new Message("message1"));
	chat.send(new Message("message2"));
	session.verifySent("<message><body>message1</body><active /></message>");
	session.verifySent("<message><body>message2</body></message>");
	session.verifyNotSent("<message><body>message2</body><active /></message>");
    }

    @Test
    public void shouldStopAfterGone() {
	session
		.receives("<message from='other@domain/other' to='self@domain/res' type='chat'><active xmlns='http://jabber.org/protocol/chatstates' /></message>");
	session
		.receives("<message from='other@domain/other' to='self@domain/res' type='chat'><gone xmlns='http://jabber.org/protocol/chatstates' /></message>");
	chatStateManager.setOwnChatUserState(ChatStateManager.ChatUserState.composing);
	chatStateManager.setOwnChatUserState(ChatStateManager.ChatUserState.pause);
	session.verifySent("<message><active /></message>");
	session.verifyNotSent("<message><composing /></message>");
	session.verifyNotSent("<message><pause /></message>");
	session.verifyNotSent("<message><gone /></message>");
    }

}
