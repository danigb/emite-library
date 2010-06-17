package com.calclab.emite.xep.chatstate.client;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.calclab.emite.core.client.events.DefaultEmiteEventBus;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.im.client.chat.PairChat;
import com.calclab.emite.xep.chatstate.client.ChatStateManager.ChatUserState;
import com.calclab.emite.xtesting.handlers.StateChangedTestHandler;

public class ChatStateTest {
    private static final XmppURI MYSELF = uri("self@domain/res");
    private static final XmppURI OTHER = uri("other@domain/otherRes");
    private PairChat pairChat;
    private ChatStateManager chatStateManager;
    private StateChangedTestHandler handler;

    @Before
    public void beforeTests() {
	final DefaultEmiteEventBus eventBus = new DefaultEmiteEventBus();
	pairChat = Mockito.mock(PairChat.class);
	Mockito.when(pairChat.getChatEventBus()).thenReturn(eventBus);
	chatStateManager = new ChatStateManager(pairChat);
	handler = new StateChangedTestHandler();
	chatStateManager.addChatUserStateChangedHandler(handler);
    }

    @Test
    public void shouldFireGone() {
	final Message message = new Message(MYSELF, OTHER, null);
	message.addChild("gone", ChatStateManager.XMLNS);
	chatStateManager.onMessageReceived(pairChat, message);
	assertTrue(handler.hasEvent());
	assertEquals(ChatUserState.gone, handler.getEventState());
    }

    @Test
    public void shouldFireOtherCompossing() {
	final Message message = new Message(MYSELF, OTHER, null);
	message.addChild("composing", ChatStateManager.XMLNS);
	chatStateManager.onMessageReceived(pairChat, message);
	assertEquals(ChatUserState.composing, handler.getEventState());
    }

    @Test
    public void shouldFireOtherCompossingAsGmailDo() {
	final Message message = new Message(MYSELF, OTHER, null);
	message.addChild("cha:composing", ChatStateManager.XMLNS);
	chatStateManager.onMessageReceived(pairChat, message);
	assertEquals(ChatUserState.composing, handler.getEventState());
    }

    @Test
    public void shouldFireOtherCompossingToWithoutResource() {
	final Message message = new Message(MYSELF, OTHER.getJID(), null);
	message.addChild("cha:composing", ChatStateManager.XMLNS);
	chatStateManager.onMessageReceived(pairChat, message);
	assertEquals(ChatUserState.composing, handler.getEventState());
    }

}
