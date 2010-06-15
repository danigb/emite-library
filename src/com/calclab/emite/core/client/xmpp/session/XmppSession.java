package com.calclab.emite.core.client.xmpp.session;

import com.calclab.emite.core.client.bosh.StreamSettings;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.xmpp.stanzas.IQ;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * The most important object in Xmpp emite module. You can login, send and
 * receive stanzas. It also allows you to pause and resume the session.
 */
public interface XmppSession {

    /**
     * Different session states. The different states paths are:
     * <ul>
     * <li>Successfull login: (disconnected) - connecting - authorized -
     * loggedIn - ready</li>
     * <li>Unsuccessfull login: (disconnected) - connecting - notAuthorized -
     * disconected</li>
     * <li>Loging out: (ready) - loggingOut - disconnected</li>
     * </ul>
     */
    public static enum SessionState {
	/**
	 * The authorization was successfull. You can NOT send stanzas using the
	 * session (stanzas will be queued). If you need to send stanzas, use
	 * the connection object directly
	 */
	authorized,
	/**
	 * You are logged in. This is the first state when you can send stanzas.
	 */
	loggedIn,
	/**
	 * Start login process. You can NOT send stanzas using session (you
	 * should use the connection directly)
	 */
	connecting,
	/**
	 * We are disconnected. You can NOT send stanzas.
	 */
	disconnected, error, notAuthorized,
	/**
	 * The session is ready to use. All the queued stanzas are sent just
	 * before this state.
	 */
	ready,
	/**
	 * We are loggin out. Last oportunity to send stanzas (i.e: last
	 * presence). session.getCurrentUser() returns the current user;
	 */
	loggingOut,
	/**
	 * We are resuming a session. When resuming a session you only receive
	 * "resuming" and "ready" (not loggedIn)
	 */
	resume
    }

    /**
     * Returns the current user xmpp uri
     * 
     * @return the current user xmpp uri
     */
    public XmppURI getCurrentUser();

    /**
     * Returns the current state
     * 
     * @return The current state as enum
     */
    public SessionState getSessionState();

    /**
     * Answer if is logged in or not
     * 
     * @return true if a user is logged in
     */
    public boolean isLoggedIn();

    /**
     * <p>
     * Start a login process with the current credentials. Use onLoggedIn method
     * to know when you are really logged in. If the uri doesn't provide a
     * resource, the session will generate one.
     * <p>
     * You can use LoginCredentials.ANONYMOUS and to perform an anonumous login.
     * </p>
     * 
     * @param credentials
     */
    public abstract void login(Credentials credentials);

    /**
     * Start a logout process in the current session. Use obnLoggedOut to know
     * when you are really logged out.
     */
    public abstract void logout();

    /**
     * Call this method to pause the session. You can use the given object
     * object (or other with the same data) to resume the session later.
     * 
     * @see http://www.xmpp.org/extensions/xep-0124.html#inactive
     * @see Session.resume
     * @return The StreamSettings object if the session was ready, null
     *         otherwise
     */
    public abstract StreamSettings pause();

    /**
     * Call this method to resume a session.
     * 
     * @see http://www.xmpp.org/extensions/xep-0124.html#inactive
     * @see Session.pause
     * @param userURI
     *            the previous session user's uri
     * @param settings
     *            the stream settings given by the pause method
     */
    public abstract void resume(XmppURI userURI, StreamSettings settings);

    /**
     * Send a stanza to the server. This method overrides the "from" uri
     * attribute.
     * 
     * <b>All the stanzas sent using this method BEFORE the LoggedIn state are
     * queued and sent AFTER Ready state.</b>
     * 
     * @see sendIQ
     * @param stanza
     *            the stanza to be sent
     */
    public abstract void send(final IPacket stanza);

    /**
     * A helper method that allows to send a IQ stanza and attach a listener to
     * the response. This method overrides (if present) the given IQ id using
     * the category provided and a internal sequential number. This method also
     * overrides (if present) the given 'from' attribute
     * 
     * <b>All the stanzas sent using this method BEFORE the LoggedIn state are
     * queued and sent AFTER Ready state.</b>
     * 
     * @param category
     *            a uniqe-per-component string that allows the session to
     *            generate a sequential and uniqe id for the IQ
     * @param iq
     *            the IQ stanza to be sent
     * @param listener
     *            the listener called when a IQ of type "result" arrives to the
     *            server. After the invocation, the listener is discarded
     * 
     */
    public abstract void sendIQ(final String category, final IQ iq, final Listener<IPacket> listener);

    /**
     * Presence managers should call this method when initial presence is sent
     */
    public void setReady();

    HandlerRegistration addIQHandler(IQHandler handler);

    HandlerRegistration addMessageHandler(MessageHandler handler);

    HandlerRegistration addPresenceHandler(PresenceHandler handler);

    HandlerRegistration addSessionStateChangedHandler(StateChangedHandler handler);
}
