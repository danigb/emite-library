package com.calclab.emite.xep.muc.client;

import com.calclab.emite.core.client.events.ChangedEvent;

public class RoomOccupantsChangedEvent extends ChangedEvent<RoomOccupantsChangedHandler> {
    private static final Type<RoomOccupantsChangedHandler> TYPE = new Type<RoomOccupantsChangedHandler>();

    public static Type<RoomOccupantsChangedHandler> getType() {
	return TYPE;
    }

    private final Occupant occupant;

    public RoomOccupantsChangedEvent(final String changeType, final Occupant occupant) {
	super(TYPE, changeType);
	assert occupant != null : "Occupant can't be null in RoomOccupantsChangedEvent";
	this.occupant = occupant;
    }

    @Override
    public Type<RoomOccupantsChangedHandler> getAssociatedType() {
	return TYPE;
    }

    public Occupant getOccupant() {
	return occupant;
    }

    @Override
    public String toDebugString() {
	return super.toDebugString() + occupant;
    }

    @Override
    protected void dispatch(final RoomOccupantsChangedHandler handler) {
	handler.onRoomOccupantsChanged(this);
    }

}
