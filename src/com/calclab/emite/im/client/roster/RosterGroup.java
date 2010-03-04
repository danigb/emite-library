package com.calclab.emite.im.client.roster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.suco.client.events.Event;
import com.calclab.suco.client.events.Listener;

/**
 * Represents a group in a roster. All the roster itself is a group (with name
 * null)
 * 
 * @see Roster
 */
public class RosterGroup implements Iterable<RosterItem> {
    private final String name;
    private final Event<RosterItem> onItemAdded;
    private final Event<RosterItem> onItemChanged;
    private final Event<RosterItem> onItemRemoved;
    private final HashMap<XmppURI, RosterItem> itemsByJID;

    /**
     * Creates a new roster group. If name is null, its supposed to be the
     * entire roster
     * 
     * @param groupName
     *            the roster name, can be null
     * @param roster
     *            the roster object
     */
    public RosterGroup(final String groupName) {
	name = groupName;
	itemsByJID = new HashMap<XmppURI, RosterItem>();

	onItemAdded = new Event<RosterItem>("rosterGroup.onItemAdded");
	onItemChanged = new Event<RosterItem>("rosterGroup.onItemChanged");
	onItemRemoved = new Event<RosterItem>("rosterGroup.onItemRemoved");
    }

    /**
     * Add a RosterItem to this group. A ItemAdded event is fired.
     * 
     * @param item
     *            The item to be added. If there's a previously item with the
     *            same jid, it's replaced
     */
    public void add(final RosterItem item) {
	itemsByJID.put(item.getJID(), item);
	onItemAdded.fire(item);
    }

    /**
     * Returns the RosterItem of the given JID or null if theres no RosterItem
     * for that jabber id.
     * 
     * @param uri
     *            the jabber id (resource is ignored)
     * @return the RosterItem or null if no item found
     */
    public RosterItem getItem(final XmppURI jid) {
	return itemsByJID.get(jid.getJID());
    }

    /**
     * Return a modificable list of the roster items sorted by the given
     * comparator
     * 
     * @param comparator
     *            The comparator using to sort the items. Can be null (and then
     *            no sort is performed)
     * 
     * @return a modificable roster item list
     * 
     * @see RosterItemsOrder
     * 
     */
    public ArrayList<RosterItem> getItemList(final Comparator<RosterItem> comparator) {
	final ArrayList<RosterItem> list = new ArrayList<RosterItem>(getItems());
	if (comparator != null) {
	    Collections.sort(list, comparator);
	}
	return list;
    }

    /**
     * Return the collection of roster items in this group. This collection
     * should be not modified directly (since is the backend of the group).
     * 
     * @return a view-only collection of roster items of this group with no
     *         specific order
     */
    public Collection<RosterItem> getItems() {
	return itemsByJID.values();
    }

    public String getName() {
	return name;
    }

    public int getSize() {
	return itemsByJID.size();
    }

    public boolean hasItem(final XmppURI uri) {
	return getItem(uri) != null;
    }

    public boolean isAllContacts() {
	return name == null;
    }

    @Override
    public Iterator<RosterItem> iterator() {
	return itemsByJID.values().iterator();
    }

    public void onItemAdded(final Listener<RosterItem> listener) {
	onItemAdded.add(listener);
    }

    public void onItemChanged(final Listener<RosterItem> listener) {
	onItemChanged.add(listener);
    }

    public void onItemRemoved(final Listener<RosterItem> listener) {
	onItemRemoved.add(listener);
    }

    public RosterItem remove(final XmppURI jid) {
	final RosterItem removed = itemsByJID.remove(jid);
	if (removed != null) {
	    onItemRemoved.fire(removed);
	}
	return removed;
    }

    protected void fireItemChange(final RosterItem item) {
	onItemChanged.fire(item);
    }

    void clear() {
	itemsByJID.clear();
    }

}
