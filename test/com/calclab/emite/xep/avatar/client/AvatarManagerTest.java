package com.calclab.emite.xep.avatar.client;

import static com.calclab.emite.core.client.xmpp.stanzas.XmppURI.uri;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.xtesting.SessionTester;
import com.calclab.emite.xtesting.handlers.PresenceTestHandler;
import com.calclab.suco.testing.events.MockedListener;

public class AvatarManagerTest {
    private AvatarManager avatarManager;
    private SessionTester session;
    private EmiteEventBus eventBus;

    @Before
    public void aaaCreateManager() {
	session = new SessionTester();
	eventBus = session.getEventBus();
	avatarManager = new AvatarManager(eventBus, session);
    }

    @Test
    public void managerShouldListenPresenceWithPhoto() {

	final PresenceTestHandler handler = new PresenceTestHandler();
	avatarManager.addIncomingHashPresenceHandler(handler);

	final Presence presence = new Presence(XmppURI.uri("juliet@capulet.com/balcony"));
	presence.addChild("x", "vcard-temp:x:update").addChild("photo", null).setText("sha1-hash-of-image");
	session.receives(presence);
	assertTrue(handler.isCalledOnce());
    }

    @Test
    public void managerShouldPublishAvatar() {
	session.setLoggedIn(uri("romeo@montague.net/orchard"));
	final String photo = "some base64 encoded photo";
	avatarManager.setVCardAvatar(photo);
	session.verifyIQSent("<iq type='set'><vCard prodid='-//HandGen//NONSGML vGen v1.0//EN' "
		+ "version='2.0' xmlns='vcard-temp' xdbns='vcard-temp'>"
		+ "<PHOTO><BINVAL>some base64 encoded photo</BINVAL></PHOTO></vCard></iq>");
	session.answerSuccess();
	// User's Server Acknowledges Publish:
	// <iq to='juliet@capulet.com' type='result' id='vc1'/>
    }

    @Test
    public void verifySendVcardRequest() {
	final MockedListener<AvatarVCard> listener = new MockedListener<AvatarVCard>();
	avatarManager.onVCardReceived(listener);

	session.setLoggedIn(uri("romeo@montague.net/orchard"));
	avatarManager.requestVCard(XmppURI.uri("juliet@capulet.com"));
	session.verifyIQSent("<iq to='juliet@capulet.com' type='get'><vCard xmlns='vcard-temp'/></iq>");
	session.answer("<iq from='juliet@capulet.com' to='romeo@montague.net/orchard' type='result'>"
		+ "<vCard xmlns='vcard-temp'><PHOTO><TYPE>image/jpeg</TYPE>"
		+ "<BINVAL>Base64-encoded-avatar-file-here!</BINVAL></PHOTO></vCard></iq>");
	assertTrue(listener.isCalledOnce());
    }
}
