package com.calclab.emite.im.client.roster;

import com.google.gwt.event.shared.GwtEvent;

public class RosterItemChangedEvent extends GwtEvent<RosterItemChangedHandler> {

    private static final Type<RosterItemChangedHandler> TYPE = new Type<RosterItemChangedHandler>();
    public static final String ITEM_ADDED = "item_added";
    public static final String ITEM_UPDATED = "item_updated";
    public static final String ITEM_REMOVED = "item_removed";

    public static Type<RosterItemChangedHandler> getType() {
	return TYPE;
    }

    private final String changeType;
    private final RosterItem item;

    public RosterItemChangedEvent(final String changeType, final RosterItem item) {
	assert changeType != null : "Change type can't be null in roster item changed events";
	assert item != null : "RosterItem can't be null in roster item changed events";
	this.changeType = changeType;
	this.item = item;
    }

    @Override
    public Type<RosterItemChangedHandler> getAssociatedType() {
	return TYPE;
    }

    public String getChangeType() {
	return changeType;
    }

    public RosterItem getItem() {
	return item;
    }

    public boolean is(final String changeType) {
	return this.changeType.equals(changeType);
    }

    @Override
    protected void dispatch(final RosterItemChangedHandler handler) {
	handler.onRosterItemChanged(this);
    }

}
