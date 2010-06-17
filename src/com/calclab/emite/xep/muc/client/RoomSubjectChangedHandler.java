package com.calclab.emite.xep.muc.client;

import com.google.gwt.event.shared.EventHandler;

public interface RoomSubjectChangedHandler extends EventHandler {

    void onRoomSubjectChanged(RoomSubjectChangedEvent event);

}
