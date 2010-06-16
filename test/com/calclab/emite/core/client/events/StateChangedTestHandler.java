package com.calclab.emite.core.client.events;

public class StateChangedTestHandler implements StateChangedHandler {
    private StateChangedEvent event;

    public StateChangedTestHandler() {
	event = null;
    }

    public StateChangedEvent getEvent() {
	return event;
    }

    public String getEventState() {
	return event.getState();
    }

    @Override
    public void onStateChanged(final StateChangedEvent event) {
	this.event = event;
    }

}
