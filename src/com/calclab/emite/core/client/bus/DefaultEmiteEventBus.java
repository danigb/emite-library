package com.calclab.emite.core.client.bus;

import com.google.gwt.event.shared.HandlerManager;

public class DefaultEmiteEventBus extends HandlerManager implements EmiteEventBus {

    public DefaultEmiteEventBus() {
	super(null);
    }

}
