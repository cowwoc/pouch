/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.jersey.application;

import io.github.cowwoc.pouch.jersey.resource.HelloWorldResource;
import io.github.cowwoc.pouch.jersey.scope.TestPouchBinder;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jersey application for the "test" codebase.
 */
public final class TestApplication extends ResourceConfig
{
	public TestApplication()
	{
		register(TestPouchBinder.class);
		register(HelloWorldResource.class);
	}
}
