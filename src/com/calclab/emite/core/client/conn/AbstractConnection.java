package com.calclab.emite.core.client.conn;

import com.calclab.emite.core.client.bus.EmiteEventBus;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.suco.client.events.Listener;
import com.calclab.suco.client.events.Listener0;
import com.calclab.suco.client.events.Listener2;

/**
 * An abstract connection. It has all the boilerplate
 * 
 */
public abstract class AbstractConnection extends AbstractXmppConnection {

    public AbstractConnection(final EmiteEventBus eventBus) {
	super(eventBus);
    }

    public void onConnected(final Listener0 listener) {
	addConnectionHandler(new ConnectionHandler() {
	    @Override
	    public void onStateChanged(final ConnectionEvent event) {
		if (event.is(ConnectionEvent.EventType.connected)) {
		    listener.onEvent();
		}
	    }
	});
    }

    public void onDisconnected(final Listener<String> listener) {
	addConnectionHandler(new ConnectionHandler() {
	    @Override
	    public void onStateChanged(final ConnectionEvent event) {
		if (event.is(ConnectionEvent.EventType.disconnected)) {
		    listener.onEvent(event.getText());
		}
	    }
	});
    }

    public void onError(final Listener<String> listener) {
	addConnectionHandler(new ConnectionHandler() {
	    @Override
	    public void onStateChanged(final ConnectionEvent event) {
		if (event.is(ConnectionEvent.EventType.error)) {
		    listener.onEvent(event.getText());
		}
	    }
	});
    }

    public void onResponse(final Listener<String> listener) {
	addConnectionHandler(new ConnectionHandler() {
	    @Override
	    public void onStateChanged(final ConnectionEvent event) {
		if (event.is(ConnectionEvent.EventType.response)) {
		    listener.onEvent(event.getText());
		}
	    }
	});
    }

    public void onRetry(final Listener2<Integer, Integer> listener) {
	addConnectionHandler(new ConnectionHandler() {
	    @Override
	    public void onStateChanged(final ConnectionEvent event) {
		if (event.is(ConnectionEvent.EventType.response)) {
		    listener.onEvent(event.getCount(), 0);
		}
	    }
	});
    }

    public void onStanzaReceived(final Listener<IPacket> listener) {
	addStanzaReceivedHandler(new StanzaReceivedHandler() {
	    @Override
	    public void onStanzaReceived(final StanzaReceivedEvent event) {
		listener.onEvent(event.getStanza());
	    }
	});
    }

    public void onStanzaSent(final Listener<IPacket> listener) {
	addStanzaSentHandler(new StanzaSentHandler() {
	    @Override
	    public void onStanzaSent(final StanzaSentEvent event) {
		listener.onEvent(event.getStanza());
	    }
	});
    }
}
