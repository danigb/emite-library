package com.calclab.emite.core.client.conn;

import com.calclab.emite.core.client.packet.IPacket;
import com.google.gwt.event.shared.HandlerRegistration;

public interface XmppConnection {
    public abstract void connect();

    public abstract void disconnect();

    public boolean hasErrors();

    public abstract boolean isConnected();

    public abstract void restartStream();

    public abstract void send(final IPacket packet);

    HandlerRegistration addConnectionHandler(ConnectionHandler handler);

    HandlerRegistration addStanzaReceivedHandler(StanzaReceivedHandler handler);

    HandlerRegistration addStanzaSentHandler(StanzaSentHandler handler);
}
