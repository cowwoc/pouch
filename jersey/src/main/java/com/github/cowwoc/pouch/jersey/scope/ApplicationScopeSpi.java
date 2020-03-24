/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.scope;

import org.glassfish.hk2.api.ServiceLocator;

/**
 * Methods that all {@code ApplicationScope}s must implement, but are hidden from end-users.
 *
 * @author Gili Tzabari
 */
interface ApplicationScopeSpi extends ApplicationScope
{
	/**
	 * @param serviceLocator the Jersey dependency-injection mechanism
	 * @return a new HTTP scope
	 * @throws NullPointerException  if {@code serviceLocator} is null
	 * @throws IllegalStateException if {@link #isClosed()}
	 */
	HttpScope createHttpScope(ServiceLocator serviceLocator)
		throws NullPointerException, IllegalStateException;

	/**
	 * Notifies the parent that a child scope has closed.
	 *
	 * @param scope the scope that was closed
	 * @throws NullPointerException  if {@code scope} is null
	 * @throws IllegalStateException if the parent scope is closed
	 */
	void onClosed(AutoCloseable scope)
		throws NullPointerException, IllegalStateException;
}
