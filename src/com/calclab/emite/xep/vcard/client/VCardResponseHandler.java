package com.calclab.emite.xep.vcard.client;

import com.google.gwt.event.shared.EventHandler;

public interface VCardResponseHandler extends EventHandler {

    void onVCardResponse(VCardResponseEvent event);

}
