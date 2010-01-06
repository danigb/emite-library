package com.calclab.emite.xfunctional.client.ui;

public interface TestRunnerView {

    enum Level {
	info, debug, fail, success, stanzas
    }

    String getUserJID();

    String getUserPassword();

    void print(Level level, String message);

}
