/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.dropwizard.application;

import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.github.cowwoc.pouch.dropwizard.resource.HelloWorldResource;
import io.github.cowwoc.pouch.dropwizard.scope.TestPouchBinder;

/**
 * Jersey application for the "test" codebase.
 */
public final class TestApplication extends Application<Configuration>
{
	@Override
	public void run(Configuration configuration, Environment environment)
	{
		environment.jersey().register(TestPouchBinder.class);
		environment.jersey().register(HelloWorldResource.class);
	}
}