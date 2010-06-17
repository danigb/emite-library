package com.calclab.emite.im.client.roster;

import com.google.gwt.event.shared.GwtEvent;

public class RosterGroupChangedEvent extends GwtEvent<RosterGroupChangedHandler> {

    private static final Type<RosterGroupChangedHandler> TYPE = new Type<RosterGroupChangedHandler>();
    public static final String GROUP_ADDED = "group_added";
    public static final String GROUP_REMOVED = "group_removed";

    public static Type<RosterGroupChangedHandler> getType() {
	return TYPE;
    }
    private final String changeType;

    private final RosterGroup group;

    public RosterGroupChangedEvent(final String changeType, final RosterGroup group) {
	assert changeType != null : "ChangeType can't be null in Roster group changed events";
	assert group != null : "RosterGroup can't be null in Roster group changed events";
	this.changeType = changeType;
	this.group = group;
    }

    @Override
    public Type<RosterGroupChangedHandler> getAssociatedType() {
	return TYPE;
    }

    public String getChangeType() {
	return changeType;
    }

    public RosterGroup getGroup() {
	return group;
    }

    public boolean is(final String changeType) {
	return this.changeType.equals(changeType);
    }

    @Override
    protected void dispatch(final RosterGroupChangedHandler handler) {
	handler.onRosterGroupChanged(this);
    }

}
