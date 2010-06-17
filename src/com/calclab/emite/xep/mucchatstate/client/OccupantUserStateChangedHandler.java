package com.calclab.emite.xep.mucchatstate.client;

import com.google.gwt.event.shared.EventHandler;

public interface OccupantUserStateChangedHandler extends EventHandler {

    void onRoomUserStateChanged(OccupantUserStateChangedEvent event);

}
