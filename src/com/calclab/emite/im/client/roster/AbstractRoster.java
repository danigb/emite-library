package com.calclab.emite.im.client.roster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.events.StateChangedEvent;
import com.calclab.emite.core.client.events.StateChangedHandler;
import com.calclab.emite.core.client.events.ChangedEvent.ChangeAction;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Implements all roster method not related directly with xmpp: boileplate code
 * and group handling code
 * 
 */
public abstract class AbstractRoster implements Roster {

    private final HashMap<String, RosterGroup> groups;

    private final RosterGroup all;

    private final EmiteEventBus eventBus;

    private String state;

    public AbstractRoster(final EmiteEventBus eventBus) {
	this.eventBus = eventBus;
	state = RosterState.notReady;
	groups = new HashMap<String, RosterGroup>();

	all = new RosterGroup(null);
    }

    @Deprecated
    public final void addItem(final XmppURI jid, final String name, final String... groups) {
	requestAddItem(jid, name, groups);
    }

    @Override
    public HandlerRegistration addRosterGroupChangedHandler(final RosterGroupChangedHandler handler) {
	return eventBus.addHandler(RosterGroupChangedEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addRosterItemChangedHandler(final RosterItemChangedHandler handler) {
	return eventBus.addHandler(RosterItemChangedEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addRosterStateChangedHandler(final StateChangedHandler handler) {
	return eventBus.addHandler(RosterStateChangedEvent.getType(), handler);
    }

    public Set<String> getGroupNames() {
	return groups.keySet();
    }

    @Deprecated
    public Set<String> getGroups() {
	return getGroupNames();
    }

    public RosterItem getItemByJID(final XmppURI jid) {
	return all.getItem(jid.getJID());
    }

    public Collection<RosterItem> getItems() {
	return new ArrayList<RosterItem>(all.getItems());
    }

    public Collection<RosterItem> getItemsByGroup(final String groupName) {
	final RosterGroup group = getRosterGroup(groupName);
	return group != null ? group.getItems() : null;
    }

    @Override
    public RosterGroup getRosterGroup(final String name) {
	return groups.get(name);
    }

    @Override
    public Collection<RosterGroup> getRosterGroups() {
	return groups.values();
    }

    @Override
    public boolean isRosterReady() {
	return state.equals(RosterState.ready);
    }

    @Override
    public void onGroupAdded(final Listener<RosterGroup> listener) {
	addRosterGroupChangedHandler(new RosterGroupChangedHandler() {
	    @Override
	    public void onRosterGroupChanged(final RosterGroupChangedEvent event) {
		if (event.isAdded()) {
		    listener.onEvent(event.getGroup());
		}
	    }
	});
    }

    @Override
    public void onGroupRemoved(final Listener<RosterGroup> listener) {
	addRosterGroupChangedHandler(new RosterGroupChangedHandler() {
	    @Override
	    public void onRosterGroupChanged(final RosterGroupChangedEvent event) {
		if (event.isRemoved()) {
		    listener.onEvent(event.getGroup());
		}
	    }
	});
    }

    public void onItemAdded(final Listener<RosterItem> listener) {
	addRosterItemChangedHandler(new RosterItemChangedHandler() {
	    @Override
	    public void onRosterItemChanged(final RosterItemChangedEvent event) {
		if (event.isAdded()) {
		    listener.onEvent(event.getItem());
		}
	    }
	});
    }

    public void onItemChanged(final Listener<RosterItem> listener) {
	addRosterItemChangedHandler(new RosterItemChangedHandler() {
	    @Override
	    public void onRosterItemChanged(final RosterItemChangedEvent event) {
		if (event.isModified()) {
		    listener.onEvent(event.getItem());
		}
	    }
	});
    }

    public void onItemRemoved(final Listener<RosterItem> listener) {
	addRosterItemChangedHandler(new RosterItemChangedHandler() {
	    @Override
	    public void onRosterItemChanged(final RosterItemChangedEvent event) {
		if (event.isRemoved()) {
		    listener.onEvent(event.getItem());
		}
	    }
	});
    }

    @Deprecated
    public void onItemUpdated(final Listener<RosterItem> listener) {
	onItemChanged(listener);
    }

    public void onRosterRetrieved(final Listener<Collection<RosterItem>> listener) {
	addRosterStateChangedHandler(new StateChangedHandler() {
	    @Override
	    public void onStateChanged(final StateChangedEvent event) {
		if (RosterState.ready == event.getState()) {
		    listener.onEvent(getItems());
		}
	    }
	});
    }

    private void addToGroup(final RosterItem item, final String groupName) {
	RosterGroup group = groups.get(groupName);
	if (group == null) {
	    group = addGroup(groupName);
	}
	group.add(item);
    }

    protected RosterGroup addGroup(final String groupName) {
	RosterGroup group;
	group = groupName != null ? new RosterGroup(groupName) : all;
	groups.put(groupName, group);
	fireGroupAdded(group);
	return group;
    }

    protected void clearGroupAll() {
	all.clear();
    }

    protected void fireGroupAdded(final RosterGroup group) {
	eventBus.fireEvent(new RosterGroupChangedEvent(ChangeAction.ADDED, group));
    }

    protected void fireGroupRemoved(final RosterGroup group) {
	eventBus.fireEvent(new RosterGroupChangedEvent(ChangeAction.REMOVED, group));
    }

    protected void fireItemAdded(final RosterItem item) {
	eventBus.fireEvent(new RosterItemChangedEvent(ChangeAction.ADDED, item));
    }

    protected void fireItemChanged(final RosterItem item) {
	eventBus.fireEvent(new RosterItemChangedEvent(ChangeAction.MODIFIED, item));
	all.fireItemChange(item);
	for (final String name : item.getGroups()) {
	    getRosterGroup(name).fireItemChange(item);
	}
    }

    protected void fireItemRemoved(final RosterItem item) {
	eventBus.fireEvent(new RosterItemChangedEvent(ChangeAction.REMOVED, item));
    }

    protected void fireRosterReady(final Collection<RosterItem> collection) {
	state = RosterState.ready;
	eventBus.fireEvent(new RosterStateChangedEvent(state));
    }

    protected void removeGroup(final String groupName) {
	final RosterGroup group = groups.remove(groupName);
	if (groupName != null && group != null) {
	    fireGroupRemoved(group);
	}
    }

    protected void removeItem(final RosterItem item) {
	final ArrayList<String> groupsToRemove = new ArrayList<String>();
	for (final String groupName : getGroupNames()) {
	    final RosterGroup group = getRosterGroup(groupName);
	    group.remove(item.getJID());
	    if (group.getName() != null && group.getSize() == 0) {
		groupsToRemove.add(groupName);
	    }
	}
	for (final String groupName : groupsToRemove) {
	    removeGroup(groupName);
	}
    }

    protected void storeItem(final RosterItem item) {
	addToGroup(item, null);
	for (final String groupName : item.getGroups()) {
	    addToGroup(item, groupName);
	}

    }
}
