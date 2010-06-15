package com.calclab.emite.browser.client;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules( { BrowserModule.class })
public interface EmiteBrowserGinjector extends Ginjector {
    AutoConfig getAutoConfig();
}
