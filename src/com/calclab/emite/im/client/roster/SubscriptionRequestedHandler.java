package com.calclab.emite.im.client.roster;

import com.google.gwt.event.shared.EventHandler;

public interface SubscriptionRequestedHandler extends EventHandler {

    void onSubscriptionRequested(SubscriptionRequestedEvent event);

}
