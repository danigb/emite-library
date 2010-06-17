package com.calclab.emite.xep.muc.client;

import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.google.gwt.event.shared.GwtEvent;

public class RoomInvitationSentEvent extends GwtEvent<RoomInvitationSentHandler> {

    private static final Type<RoomInvitationSentHandler> TYPE = new Type<RoomInvitationSentHandler>();

    public static Type<RoomInvitationSentHandler> getType() {
	return TYPE;
    }
    private final XmppURI invitedJid;
    private final String reasonText;

    public RoomInvitationSentEvent(final XmppURI invitedJid, final String reasonText) {
	this.invitedJid = invitedJid;
	this.reasonText = reasonText;
    }

    @Override
    public Type<RoomInvitationSentHandler> getAssociatedType() {
	return TYPE;
    }

    public XmppURI getInvitedJid() {
	return invitedJid;
    }

    public String getReasonText() {
	return reasonText;
    }

    @Override
    protected void dispatch(final RoomInvitationSentHandler handler) {
	handler.onRoomInvitationSent(this);

    }

}
