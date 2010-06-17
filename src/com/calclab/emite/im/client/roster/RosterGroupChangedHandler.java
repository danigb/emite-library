package com.calclab.emite.im.client.roster;

import com.google.gwt.event.shared.EventHandler;

public interface RosterGroupChangedHandler extends EventHandler {

    void onRosterGroupChanged(RosterGroupChangedEvent event);

}
