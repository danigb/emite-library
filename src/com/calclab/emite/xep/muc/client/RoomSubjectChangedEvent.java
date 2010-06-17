package com.calclab.emite.xep.muc.client;

import com.google.gwt.event.shared.GwtEvent;

public class RoomSubjectChangedEvent extends GwtEvent<RoomSubjectChangedHandler> {

    private static final Type<RoomSubjectChangedHandler> TYPE = new Type<RoomSubjectChangedHandler>();

    public static Type<RoomSubjectChangedHandler> getType() {
	return TYPE;
    }
    private final Occupant occupant;

    private final String subject;

    public RoomSubjectChangedEvent(final Occupant occupant, final String subject) {
	this.occupant = occupant;
	this.subject = subject;
    }

    @Override
    public Type<RoomSubjectChangedHandler> getAssociatedType() {
	return TYPE;
    }

    public Occupant getOccupant() {
	return occupant;
    }

    public String getSubject() {
	return subject;
    }

    @Override
    protected void dispatch(final RoomSubjectChangedHandler handler) {
	handler.onRoomSubjectChanged(this);
    }

}
