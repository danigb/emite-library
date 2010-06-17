package com.calclab.emite.xtesting.handlers;

import com.calclab.emite.core.client.events.ChangedEvent;

public abstract class ChangedTestHandler<T extends ChangedEvent<?>> extends TestHandler<T> {
    public String getChangeType() {
	return hasEvent() ? event.getChangeType() : null;
    }
}
