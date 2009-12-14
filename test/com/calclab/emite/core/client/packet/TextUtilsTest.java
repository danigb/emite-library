package com.calclab.emite.core.client.packet;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TextUtilsTest {

    @Test
    public void matchDemoEmail() {
        String email = "test100@emitedemo.ourproject.org";
        assertTrue(email.matches(TextUtils.EMAIL_REGEXP));
    }

    @Test
    public void matchLocalhostEmail() {
        String email = "me@localhost";
        assertTrue(email.matches(TextUtils.EMAIL_REGEXP));
    }

    @Test
    public void matchSimpleEmail() {
        String email = "me@example.com";
        assertTrue(email.matches(TextUtils.EMAIL_REGEXP));
    }
}
