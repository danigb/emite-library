package com.calclab.emite.xtesting.handlers;

import com.calclab.emite.im.client.chat.Chat;
import com.calclab.emite.im.client.chat.ChatChangedEvent;
import com.calclab.emite.im.client.chat.ChatChangedHandler;

public class ChatChangedTestHandler extends ChangedTestHandler<ChatChangedEvent> implements ChatChangedHandler {

    public Chat getChat() {
	return hasEvent() ? event.getChat() : null;
    }

    @Override
    public void onChatChanged(final ChatChangedEvent event) {
	setEvent(event);
    }

}
