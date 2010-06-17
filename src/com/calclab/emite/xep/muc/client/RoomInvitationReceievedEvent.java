package com.calclab.emite.xep.muc.client;

public class RoomInvitationReceievedEvent extends RoomInvitationEvent {
    private static final Type<RoomInvitationHandler> TYPE = new Type<RoomInvitationHandler>();

    public static Type<RoomInvitationHandler> getType() {
	return TYPE;
    }

    public RoomInvitationReceievedEvent(final RoomInvitation roomInvitation) {
	super(TYPE, roomInvitation);
    }

}
