package com.calclab.emite.core.client.conn;

import com.calclab.emite.core.client.bosh.StreamSettings;
import com.calclab.emite.core.client.bus.EmiteEventBus;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.packet.Packet;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractXmppConnection implements XmppConnection {

    protected final EmiteEventBus eventBus;
    private int errors;
    private boolean active;
    private StreamSettings stream;
    private Packet currentBody;
    private ConnectionSettings userSettings;

    public AbstractXmppConnection(final EmiteEventBus eventBus) {
	this.eventBus = eventBus;
    }

    @Override
    public HandlerRegistration addConnectionHandler(final ConnectionHandler handler) {
	return eventBus.addHandler(ConnectionEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addStanzaReceivedHandler(final StanzaReceivedHandler handler) {
	return eventBus.addHandler(StanzaReceivedEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addStanzaSentHandler(final StanzaSentHandler handler) {
	return eventBus.addHandler(StanzaSentEvent.getType(), handler);
    }

    public void clearErrors() {
	errors = 0;
    }

    @Override
    public boolean hasErrors() {
	return errors != 0;
    }

    public int incrementErrors() {
	errors++;
	return errors;
    }

    public void setSettings(final ConnectionSettings settings) {
	userSettings = settings;
    }

    protected void fireConnected() {
	eventBus.fireEvent(new ConnectionEvent(ConnectionEvent.EventType.connected));
    }

    protected void fireDisconnected(final String message) {
	eventBus.fireEvent(new ConnectionEvent(ConnectionEvent.EventType.connected, message));
    }

    protected void fireError(final String error) {
	eventBus.fireEvent(new ConnectionEvent(ConnectionEvent.EventType.error, error));
    }

    protected void fireResponse(final String response) {
	eventBus.fireEvent(new ConnectionEvent(ConnectionEvent.EventType.response, response));
    }

    protected void fireRetry(final Integer attempt, final Integer scedTime) {
	eventBus.fireEvent(new ConnectionEvent(ConnectionEvent.EventType.beforeRetry, null, attempt));
    }

    protected void fireStanzaReceived(final IPacket stanza) {
	eventBus.fireEvent(new StanzaReceivedEvent(stanza));
    }

    protected void fireStanzaSent(final IPacket packet) {
	eventBus.fireEvent(new StanzaSentEvent(packet));
    }

    /**
     * @return the currentBody
     */
    protected Packet getCurrentBody() {
	return currentBody;
    }

    /**
     * @return the stream
     */
    protected StreamSettings getStream() {
	return stream;
    }

    protected ConnectionSettings getUserSettings() {
	return userSettings;
    }

    /**
     * @return if the connection is active
     */
    protected boolean isActive() {
	return active;
    }

    /**
     * Set the conntection active
     * 
     * @param active
     *            true if active
     * 
     */
    protected void setActive(final boolean active) {
	this.active = active;
    }

    /**
     * @param currentBody
     *            the currentBody to set
     */
    protected void setCurrentBody(final Packet currentBody) {
	this.currentBody = currentBody;
    }

    /**
     * @param stream
     *            the stream to set
     */
    protected void setStream(final StreamSettings stream) {
	this.stream = stream;
    }
}
