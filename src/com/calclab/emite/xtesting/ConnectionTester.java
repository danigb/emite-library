package com.calclab.emite.xtesting;

import java.util.ArrayList;

import com.calclab.emite.core.client.bosh.StreamSettings;
import com.calclab.emite.core.client.conn.AbstractConnection;
import com.calclab.emite.core.client.conn.Connection;
import com.calclab.emite.core.client.events.DefaultEmiteEventBus;
import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.xtesting.matchers.IsPacketLike;
import com.calclab.emite.xtesting.services.TigaseXMLService;

/**
 * Object of this class are used to test against connection (for example, to
 * test the Session)
 * 
 */
public class ConnectionTester extends AbstractConnection implements Connection {

    private boolean isConnected;
    private boolean paused;
    private final TigaseXMLService xmler;
    private final ArrayList<IPacket> sent;
    private boolean streamRestarted;
    private final ArrayList<IPacket> received;

    public ConnectionTester() {
	this(new DefaultEmiteEventBus());
    }

    public ConnectionTester(final EmiteEventBus eventBus) {
	super(eventBus);
	xmler = new TigaseXMLService();
	sent = new ArrayList<IPacket>();
	received = new ArrayList<IPacket>();
    }

    @Override
    public void connect() {
	isConnected = true;
    }

    @Override
    public void disconnect() {
	isConnected = false;
    }

    public int getSentSize() {
	return sent.size();
    }

    public boolean hasSent(final IPacket packet) {
	final IsPacketLike matcher = new IsPacketLike(packet);
	for (final IPacket stanza : sent) {
	    if (matcher.matches(stanza, System.out)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean isConnected() {
	return isConnected;
    }

    public boolean isStreamRestarted() {
	return streamRestarted;
    }

    @Override
    public StreamSettings pause() {
	paused = true;
	return null;
    }

    public void receives(final IPacket stanza) {
	received.add(stanza);
	fireStanzaReceived(stanza);
    }

    public void receives(final String stanza) {
	fireStanzaReceived(xmler.toXML(stanza));
    }

    @Override
    public void restartStream() {
	streamRestarted = true;
    }

    @Override
    public boolean resume(final StreamSettings settings) {
	if (paused) {
	    paused = false;
	    return true;
	}
	return false;
    }

    @Override
    public void send(final IPacket packet) {
	sent.add(packet);
	fireStanzaSent(packet);
    }

    public void send(final String stanza) {
	send(xmler.toXML(stanza));
    }

}
