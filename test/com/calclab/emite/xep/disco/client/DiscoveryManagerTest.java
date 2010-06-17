package com.calclab.emite.xep.disco.client;

import org.junit.Before;
import org.junit.Test;

import com.calclab.emite.core.client.events.EmiteEventBus;
import com.calclab.emite.xtesting.SessionTester;

public class DiscoveryManagerTest {

    private SessionTester session;

    @Before
    public void beforeTests() {
	session = new SessionTester();
	final EmiteEventBus eventBus = session.getEventBus();
	new DiscoveryManager(eventBus, session);
    }

    @Test
    public void shouldInformListeners() {
    }
}
