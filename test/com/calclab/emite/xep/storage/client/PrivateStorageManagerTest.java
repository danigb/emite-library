package com.calclab.emite.xep.storage.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.calclab.emite.core.client.xmpp.session.IQResponseTestHandler;
import com.calclab.emite.xtesting.SessionTester;
import com.calclab.emite.xtesting.services.TigaseXMLService;

public class PrivateStorageManagerTest {
    private SessionTester session;
    private PrivateStorageManager manager;

    String storeData = "<iq type=\"set\" ><query xmlns=\"jabber:iq:private\">"
	    + "<exodus xmlns=\"exodus:prefs\"><defaultnick>Hamlet</defaultnick></exodus></query></iq>";
    String data = "<exodus xmlns=\"exodus:prefs\"><defaultnick>Hamlet</defaultnick></exodus>";

    String dataToRetrieve = "<exodus xmlns=\"exodus:prefs\"/>";
    String retriveData = "<iq type=\"get\"><query xmlns=\"jabber:iq:private\"><exodus xmlns=\"exodus:prefs\"/></query></iq>";
    String retrieveResponse = "<iq type=\"result\" from=\"hamlet@shakespeare.lit/denmark\" to=\"hamlet@shakespeare.lit/denmark\"> <query xmlns=\"jabber:iq:private\"><exodus xmlns=\"exodus:prefs\"><defaultnick>Hamlet</defaultnick></exodus></query></iq>";

    @Before
    public void setup() {
	session = new SessionTester("test@domain");
	manager = new PrivateStorageManager(session);
    }

    @Test
    public void shouldStore() {
	manager.store(new SimpleStorageData(TigaseXMLService.toPacket(data)), null);
	session.verifyIQSent(storeData);
    }

    @Test
    public void shoulGet() {
	final IQResponseTestHandler handler = new IQResponseTestHandler();
	manager.retrieve(new SimpleStorageData("exodus", "exodus:prefs"), handler);
	session.verifyIQSent(retriveData);
	session.answer(retrieveResponse);
	assertTrue(handler.hasIq());
	assertEquals("Hamlet", handler.getIq().getFirstChild("query").getFirstChild("exodus").getFirstChild(
		"defaultnick").getText());
    }
}
