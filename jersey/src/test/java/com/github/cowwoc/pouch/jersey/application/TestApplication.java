/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.application;

import com.github.cowwoc.pouch.jersey.scope.TestPouchBinder;
import com.github.cowwoc.pouch.jersey.resource.HelloWorldResource;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jersey application for the "test" codebase.
 *
 * @author Gili Tzabari
 */
public final class TestApplication extends ResourceConfig
{
	public TestApplication()
	{
		register(new TestPouchBinder());
		register(HelloWorldResource.class);
	}
}
