package com.calclab.emite.xtesting.handlers;

import com.calclab.emite.xep.muc.client.Occupant;
import com.calclab.emite.xep.muc.client.RoomOccupantsChangedEvent;
import com.calclab.emite.xep.muc.client.RoomOccupantsChangedHandler;

public class RoomOccupantsChangedTestHandler extends ChangedTestHandler<RoomOccupantsChangedEvent> implements
	RoomOccupantsChangedHandler {

    public Occupant getOccupant() {
	return hasEvent() ? event.getOccupant() : null;
    }

    @Override
    public void onRoomOccupantsChanged(final RoomOccupantsChangedEvent event) {
	setEvent(event);
    }

}
