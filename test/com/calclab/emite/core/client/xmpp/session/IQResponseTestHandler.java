package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.xmpp.stanzas.IQ;

public class IQResponseTestHandler implements IQResponseHandler {

    private IQ iq;

    public IQResponseTestHandler() {
	iq = null;
    }

    public IQ getIq() {
	return iq;
    }

    public boolean hasIq() {
	return iq != null;
    }

    @Override
    public void onIQ(final IQ iq) {
	this.iq = iq;
    }

}
