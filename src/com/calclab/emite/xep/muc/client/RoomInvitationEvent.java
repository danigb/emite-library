package com.calclab.emite.xep.muc.client;

import com.google.gwt.event.shared.GwtEvent;

public class RoomInvitationEvent extends GwtEvent<RoomInvitationHandler> {

    private final RoomInvitation roomInvitation;
    private final Type<RoomInvitationHandler> associatedType;

    public RoomInvitationEvent(final Type<RoomInvitationHandler> associatedType, final RoomInvitation roomInvitation) {
	assert associatedType != null : "AssociatedType can't be null in RoomInvitationEvents";
	assert roomInvitation != null : "RoomInvitation can't be null in RoomInvitationEvents";
	this.associatedType = associatedType;
	this.roomInvitation = roomInvitation;
    }

    @Override
    public Type<RoomInvitationHandler> getAssociatedType() {
	return associatedType;
    }

    public RoomInvitation getRoomInvitation() {
	return roomInvitation;
    }

    @Override
    protected void dispatch(final RoomInvitationHandler handler) {
	handler.onRoomInvitation(this);
    }

}
