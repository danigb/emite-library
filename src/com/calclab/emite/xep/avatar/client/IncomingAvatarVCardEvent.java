package com.calclab.emite.xep.avatar.client;

import com.google.gwt.event.shared.GwtEvent;

public class IncomingAvatarVCardEvent extends GwtEvent<IncomingAvatarVCardHandler> {

    private static final Type<IncomingAvatarVCardHandler> TYPE = new Type<IncomingAvatarVCardHandler>();

    public static Type<IncomingAvatarVCardHandler> getType() {
	return TYPE;
    }

    private final AvatarVCard avatar;

    public IncomingAvatarVCardEvent(final AvatarVCard avatar) {
	this.avatar = avatar;
    }

    @Override
    public Type<IncomingAvatarVCardHandler> getAssociatedType() {
	return TYPE;
    }

    public AvatarVCard getAvatarVCard() {
	return avatar;
    }

    @Override
    protected void dispatch(final IncomingAvatarVCardHandler handler) {
	handler.onIncomingAvatarVCard(this);
    }

}
