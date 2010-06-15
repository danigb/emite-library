package com.calclab.emite.core.client.bosh;

import java.util.List;

import com.calclab.emite.core.client.bus.EmiteEventBus;
import com.calclab.emite.core.client.conn.AbstractXmppConnection;
import com.calclab.emite.core.client.conn.ConnectionSettings;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.packet.Packet;
import com.calclab.emite.core.client.services.ConnectorCallback;
import com.calclab.emite.core.client.services.ConnectorException;
import com.calclab.emite.core.client.services.ScheduledAction;
import com.calclab.emite.core.client.services.Services;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

public class XmppBoshConnection extends AbstractXmppConnection {
    private int activeConnections;
    private final Services services;
    private final ConnectorCallback listener;
    private boolean shouldCollectResponses;
    private final RetryControl retryControl = new RetryControl();

    @Inject
    public XmppBoshConnection(final EmiteEventBus eventBus, final Services services) {
	super(eventBus);
	this.services = services;

	listener = new ConnectorCallback() {

	    @Override
	    public void onError(final String request, final Throwable throwable) {
		if (isActive()) {
		    final int e = incrementErrors();
		    GWT.log("Connection error n°" + e, throwable);
		    if (e > retryControl.maxRetries) {
			fireError("Connection error: " + throwable.toString());
			disconnect();
		    } else {
			final int scedTime = retryControl.retry(e);
			fireRetry(e, scedTime);
			services.schedule(scedTime, new ScheduledAction() {
			    public void run() {
				GWT.log("Error retry: " + e);
				send(request);
			    }
			});
		    }
		}
	    }

	    @Override
	    public void onResponseReceived(final int statusCode, final String content, final String originalRequest) {
		clearErrors();
		activeConnections--;
		if (isActive()) {
		    // TODO: check if is the same code in other than FF and make
		    // tests
		    if (statusCode == 404) {
			fireError("404 Connection Error (session removed ?!) : " + content);
			disconnect();
		    } else if (statusCode != 200 && statusCode != 0) {
			// setActive(false);
			// fireError("Bad status: " + statusCode);
			onError(originalRequest, new Exception("Bad status: " + statusCode + " " + content));
		    } else {
			final IPacket response = services.toXML(content);
			if (response != null && "body".equals(response.getName())) {
			    clearErrors();
			    fireResponse(content);
			    handleResponse(response);
			} else {
			    onError(originalRequest, new Exception("Bad response: " + statusCode + " " + content));
			    // fireError("Bad response: " + content);
			}
		    }
		}
	    }
	};
    }

    public void connect() {
	assert getUserSettings() != null;
	clearErrors();

	if (!isActive()) {
	    setActive(true);
	    setStream(new StreamSettings());
	    activeConnections = 0;
	    createInitialBody(getUserSettings());
	    sendBody();
	}
    }

    public void disconnect() {
	GWT.log("BoshConnection - Disconnected called - Clearing current body and send a priority 'terminate' stanza.");
	// Clearing all queued stanzas
	setCurrentBody(null);
	// Create a new terminate stanza and force the send
	createBody();
	getCurrentBody().setAttribute("type", "terminate");
	sendBody(true);
	setActive(false);
	getStream().sid = null;
	fireDisconnected("logged out");
    }

    public boolean isConnected() {
	return getStream() != null;
    }

    public StreamSettings pause() {
	if (getStream() != null && getStream().sid != null) {
	    createBody();
	    getCurrentBody().setAttribute("pause", getStream().maxPause);
	    sendBody(true);
	    return getStream();
	}
	return null;
    }

    public void restartStream() {
	createBody();
	getCurrentBody().setAttribute("xmlns:xmpp", "urn:xmpp:xbosh");
	getCurrentBody().setAttribute("xmpp:restart", "true");
	getCurrentBody().setAttribute("to", getUserSettings().hostName);
	getCurrentBody().setAttribute("xml:lang", "en");
    }

    public boolean resume(final StreamSettings settings) {
	setActive(true);
	setStream(settings);
	continueConnection(null);
	return isActive();
    }

    public void send(final IPacket packet) {
	createBody();
	getCurrentBody().addChild(packet);
	sendBody();
	fireStanzaSent(packet);
    }

    @Override
    public String toString() {
	return "Bosh in " + (isActive() ? "active" : "inactive") + " stream=" + getStream();
    }

    private void continueConnection(final String ack) {
	if (isConnected() && activeConnections == 0) {
	    if (getCurrentBody() != null) {
		sendBody();
	    } else {
		final long currentRID = getStream().rid;
		// FIXME: hardcoded
		final int msecs = 200;
		services.schedule(msecs, new ScheduledAction() {
		    public void run() {
			if (getCurrentBody() == null && getStream().rid == currentRID) {
			    // Whitespace keep-alive
			    createBody();
			    getCurrentBody().setText(" ");
			    sendBody();
			}
		    }
		});
	    }
	}
    }

    private void createBody() {
	if (getCurrentBody() == null) {
	    final Packet body = new Packet("body");
	    body.With("xmlns", "http://jabber.org/protocol/httpbind");
	    body.With("rid", getStream().getNextRid());
	    if (getStream() != null) {
		body.With("sid", getStream().sid);
	    }
	    setCurrentBody(body);
	}
    }

    private void createInitialBody(final ConnectionSettings userSettings) {
	final Packet body = new Packet("body");
	body.setAttribute("content", "text/xml; charset=utf-8");
	body.setAttribute("xmlns", "http://jabber.org/protocol/httpbind");
	body.setAttribute("xmlns:xmpp", "urn:xmpp:xbosh");
	body.setAttribute("ver", userSettings.version);
	body.setAttribute("xmpp:version", "1.0");
	body.setAttribute("xml:lang", "en");
	body.setAttribute("ack", "1");
	body.setAttribute("secure", Boolean.toString(userSettings.secure));
	body.setAttribute("rid", getStream().getNextRid());
	body.setAttribute("to", userSettings.hostName);
	if (userSettings.routeHost != null && userSettings.routePort != null) {
	    String routeHost = userSettings.routeHost;
	    if (routeHost == null) {
		routeHost = userSettings.hostName;
	    }
	    Integer routePort = userSettings.routePort;
	    if (routePort == null) {
		routePort = 5222;
	    }
	    body.setAttribute("route", "xmpp:" + routeHost + ":" + routePort);
	}
	body.With("hold", userSettings.hold);
	body.With("wait", userSettings.wait);
	setCurrentBody(body);
    }

    private void handleResponse(final IPacket response) {
	if (isTerminate(response.getAttribute("type"))) {
	    getStream().sid = null;
	    setActive(false);
	    fireDisconnected("disconnected by server");
	} else {
	    if (getStream().sid == null) {
		initStream(response);
		fireConnected();
	    }
	    shouldCollectResponses = true;
	    final List<? extends IPacket> stanzas = response.getChildren();
	    for (final IPacket stanza : stanzas) {
		fireStanzaReceived(stanza);
	    }
	    shouldCollectResponses = false;
	    continueConnection(response.getAttribute("ack"));
	}
    }

    private void initStream(final IPacket response) {
	final StreamSettings stream = getStream();
	stream.sid = response.getAttribute("sid");
	stream.wait = response.getAttribute("wait");
	stream.inactivity = response.getAttribute("inactivity");
	stream.maxPause = response.getAttribute("maxpause");
    }

    private boolean isTerminate(final String type) {
	// Openfire bug: terminal instead of terminate
	return "terminate".equals(type) || "terminal".equals(type);
    }

    /**
     * Sends a new request (and count the activeConnections)
     * 
     * @param request
     */
    private void send(final String request) {
	try {
	    activeConnections++;
	    services.send(getUserSettings().httpBase, request, listener);
	    getStream().lastRequestTime = services.getCurrentTime();
	} catch (final ConnectorException e) {
	    activeConnections--;
	    e.printStackTrace();
	}
    }

    private void sendBody() {
	sendBody(false);
    }

    private void sendBody(final boolean force) {
	// TODO: better semantics
	if (force || !shouldCollectResponses && isActive() && activeConnections < getUserSettings().maxRequests
		&& !hasErrors()) {
	    final String request = services.toString(getCurrentBody());
	    setCurrentBody(null);
	    send(request);
	} else {
	    GWT.log("Send body simply queued", null);
	}
    }

}
