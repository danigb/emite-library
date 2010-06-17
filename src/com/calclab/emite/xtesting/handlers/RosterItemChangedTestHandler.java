package com.calclab.emite.xtesting.handlers;

import com.calclab.emite.im.client.roster.RosterItem;
import com.calclab.emite.im.client.roster.RosterItemChangedEvent;
import com.calclab.emite.im.client.roster.RosterItemChangedHandler;

public class RosterItemChangedTestHandler extends ChangedTestHandler<RosterItemChangedEvent> implements
	RosterItemChangedHandler {

    public RosterItem getItem() {
	return hasEvent() ? event.getItem() : null;
    }

    @Override
    public void onRosterItemChanged(final RosterItemChangedEvent event) {
	setEvent(event);
    }

}
