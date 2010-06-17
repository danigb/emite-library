package com.calclab.emite.xep.vcard.client;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.xmpp.session.IQResponseHandler;
import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

public class VCardManager {

    private static final String ID_PREFIX = "vcard";
    private final XmppSession session;
    private final EmiteEventBus eventBus;

    public VCardManager(final EmiteEventBus eventBus, final XmppSession session) {
	this.eventBus = eventBus;
	this.session = session;
    }

    @Deprecated
    public void addOnVCardReceived(final Listener<VCardResponse> listener) {
	addVCardResponseHandler(new VCardResponseHandler() {
	    @Override
	    public void onVCardResponse(final VCardResponseEvent event) {
		listener.onEvent(event.getResponse());
	    }
	});
    }

    /**
     * Add a handler to know when a VCard response is received
     * 
     * @param handler
     * @return
     */
    public HandlerRegistration addVCardResponseHandler(final VCardResponseHandler handler) {
	return eventBus.addHandler(VCardResponseEvent.getType(), handler);
    }

    public void getUserVCard(final XmppURI userJid, final Listener<VCardResponse> listener) {
	final IQ iq = new IQ(IQ.Type.get);
	iq.addChild(VCard.VCARD, VCard.DATA_XMLS);
	iq.setFrom(session.getCurrentUser());
	iq.setTo(userJid);
	session.sendIQ(ID_PREFIX, iq, new IQResponseHandler() {
	    @Override
	    public void onIQ(final IQ parameter) {
		handleVCard(parameter, listener);
	    }
	});
    }

    public void requestOwnVCard(final Listener<VCardResponse> listener) {
	final IQ iq = new IQ(IQ.Type.get);
	iq.addChild(VCard.VCARD, VCard.DATA_XMLS);
	iq.setFrom(session.getCurrentUser());
	session.sendIQ(ID_PREFIX, iq, new IQResponseHandler() {
	    @Override
	    public void onIQ(final IQ parameter) {
		handleVCard(parameter, listener);
	    }
	});
    }

    public void updateOwnVCard(final VCard vcard, final Listener<VCardResponse> listener) {
	final IQ iq = new IQ(IQ.Type.set);
	iq.addChild(vcard);
	session.sendIQ(ID_PREFIX, iq, new IQResponseHandler() {
	    @Override
	    public void onIQ(final IQ parameter) {
		handleVCard(parameter, listener);
	    }
	});

    }

    protected void handleVCard(final IQ result, final Listener<VCardResponse> listener) {
	final VCardResponse response = new VCardResponse(result);
	if (listener != null) {
	    listener.onEvent(response);
	}
	eventBus.fireEvent(new VCardResponseEvent(response));
    }
}
