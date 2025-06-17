/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.dropwizard.scope;

import org.glassfish.hk2.api.ServiceLocator;

/**
 * HttpScope for the test codebase.
 */
public final class TestRequestScope extends AbstractRequestScope
{
	/**
	 * Creates a new TestHttpScope.
	 *
	 * @param parent         the parent scope
	 * @param serviceLocator the Jersey dependency-injection mechanism
	 * @throws NullPointerException if any of the arguments are null
	 */
	TestRequestScope(ServerScope parent, ServiceLocator serviceLocator)
	{
		super(parent, serviceLocator);
	}
}