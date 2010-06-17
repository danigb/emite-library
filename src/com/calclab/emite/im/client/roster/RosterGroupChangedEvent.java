package com.calclab.emite.im.client.roster;

import com.calclab.emite.core.client.events.ChangedEvent;

public class RosterGroupChangedEvent extends ChangedEvent<RosterGroupChangedHandler> {
    private static final Type<RosterGroupChangedHandler> TYPE = new Type<RosterGroupChangedHandler>();

    public static Type<RosterGroupChangedHandler> getType() {
	return TYPE;
    }

    private final RosterGroup group;

    public RosterGroupChangedEvent(final String changeType, final RosterGroup group) {
	super(TYPE, changeType);
	assert group != null : "RosterGroup can't be null in Roster group changed events";
	this.group = group;
    }

    public RosterGroup getGroup() {
	return group;
    }

    @Override
    public String toDebugString() {
	return super.toDebugString() + group;
    }

    @Override
    protected void dispatch(final RosterGroupChangedHandler handler) {
	handler.onRosterGroupChanged(this);
    }

}
