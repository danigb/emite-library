package com.calclab.emite.xep.muc.client;

import com.google.gwt.event.shared.EventHandler;

public interface RoomOccupantsChangedHandler extends EventHandler {

    void onRoomOccupantsChanged(RoomOccupantsChangedEvent event);
}
