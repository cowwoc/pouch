/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import org.glassfish.hk2.api.ServiceLocator;

/**
 * HttpScope for the test codebase.
 */
public final class TestHttpScope extends AbstractHttpScope
{
	/**
	 * Creates a new TestHttpScope.
	 *
	 * @param parent         the parent scope
	 * @param serviceLocator the Jersey dependency-injection mechanism
	 * @throws NullPointerException if any of the arguments are null
	 */
	TestHttpScope(JvmScope parent, ServiceLocator serviceLocator)
	{
		super(parent, serviceLocator);
	}

	@Override
	protected void beforeClose()
	{
	}
}
