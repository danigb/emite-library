package com.calclab.emite.im.client.roster;

import com.calclab.emite.core.client.events.ChangedEvent;

public class RosterItemChangedEvent extends ChangedEvent<RosterItemChangedHandler> {

    private static final Type<RosterItemChangedHandler> TYPE = new Type<RosterItemChangedHandler>();

    public static Type<RosterItemChangedHandler> getType() {
	return TYPE;
    }

    private final RosterItem item;

    public RosterItemChangedEvent(final String changeType, final RosterItem item) {
	super(TYPE, changeType);
	assert item != null : "RosterItem can't be null in roster item changed events";
	this.item = item;
    }

    public RosterItem getItem() {
	return item;
    }

    @Override
    public String toDebugString() {
	return super.toDebugString() + item;
    }

    @Override
    protected void dispatch(final RosterItemChangedHandler handler) {
	handler.onRosterItemChanged(this);
    }

}
