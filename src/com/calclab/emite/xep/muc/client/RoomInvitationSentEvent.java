package com.calclab.emite.xep.muc.client;

public class RoomInvitationSentEvent extends RoomInvitationEvent {

    private static final Type<RoomInvitationHandler> TYPE = new Type<RoomInvitationHandler>();

    public static Type<RoomInvitationHandler> getType() {
	return TYPE;
    }

    public RoomInvitationSentEvent(final RoomInvitation invitation) {
	super(TYPE, invitation);
    }

}
