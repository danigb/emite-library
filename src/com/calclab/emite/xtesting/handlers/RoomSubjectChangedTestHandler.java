package com.calclab.emite.xtesting.handlers;

import com.calclab.emite.xep.muc.client.Occupant;
import com.calclab.emite.xep.muc.client.RoomSubjectChangedEvent;
import com.calclab.emite.xep.muc.client.RoomSubjectChangedHandler;

public class RoomSubjectChangedTestHandler extends TestHandler<RoomSubjectChangedEvent> implements
	RoomSubjectChangedHandler {

    public Occupant getOccupant() {
	return hasEvent() ? event.getOccupant() : null;
    }

    public String getSubject() {
	return hasEvent() ? event.getSubject() : null;
    }

    @Override
    public void onRoomSubjectChanged(final RoomSubjectChangedEvent event) {
	setEvent(event);
    }

}
