/*
 *
 * suco: Mini IoC framework a-la-guice style for GWT
 *
 * (c) 2009 The suco development team (see CREDITS for details)
 * This file is part of emite.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.calclab.suco.client.ioc.decorator;

import java.util.ArrayList;

import com.calclab.suco.client.ioc.Decorator;
import com.calclab.suco.client.ioc.Provider;

/**
 * GroupedSingleton helps grouping some providers and allowing to create all the
 * instances of the grouped providers. All the providers are converted to
 * singleton providers
 * 
 * This class has problems when removing providers from the container. Please
 * use ProviderCollection
 * 
 * @deprecated
 * @see ProviderCollection
 */
@Deprecated
public abstract class GroupedSingleton implements Decorator {
    private final ArrayList<Provider<?>> providers;

    public GroupedSingleton() {
	this.providers = new ArrayList<Provider<?>>();
    }

    public void createAll() {
	for (final Provider<?> provider : providers) {
	    provider.get();
	}
    }

    public <T> Provider<T> decorate(final Class<T> type, final Provider<T> undecorated) {
	final Provider<T> decorated = Singleton.instance.decorate(type, undecorated);
	providers.add(decorated);
	return decorated;
    }

    public ArrayList<Provider<?>> getProviders() {
	return providers;
    }

}
