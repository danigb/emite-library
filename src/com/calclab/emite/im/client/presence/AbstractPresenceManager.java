package com.calclab.emite.im.client.presence;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.events.PresenceEvent;
import com.calclab.emite.core.client.events.PresenceHandler;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractPresenceManager implements PresenceManager {
    private Presence ownPresence;
    protected final EmiteEventBus eventBus;

    public AbstractPresenceManager(final EmiteEventBus eventBus) {
	this.eventBus = eventBus;
    }

    @Override
    public HandlerRegistration addOwnPresenceChangedHandler(final PresenceHandler handler) {
	return eventBus.addHandler(OwnPresenceChangedEvent.getType(), handler);
    }

    /**
     * Return the current logged in user presence or a Presence with type
     * unavailable if logged out
     * 
     * @return
     */
    public Presence getOwnPresence() {
	return ownPresence;
    }

    @Deprecated
    public void onOwnPresenceChanged(final Listener<Presence> listener) {
	addOwnPresenceChangedHandler(new PresenceHandler() {
	    @Override
	    public void onPresence(final PresenceEvent event) {
		listener.onEvent(event.getPresence());
	    }
	});
    }

    public void setOwnPresence(final Presence presence) {
	ownPresence = presence;
	eventBus.fireEvent(new OwnPresenceChangedEvent(presence));
    }

}
