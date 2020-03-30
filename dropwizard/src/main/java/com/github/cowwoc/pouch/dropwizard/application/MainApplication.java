/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.application;

import com.github.cowwoc.pouch.dropwizard.resource.HelloWorldResource;
import com.github.cowwoc.pouch.dropwizard.scope.MainPouchBinder;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

/**
 * Jersey application for the "main" codebase.
 *
 * @author Gili Tzabari
 */
public final class MainApplication extends Application<Configuration>
{
	public static void main(String[] args) throws Exception
	{
		new MainApplication().run(args);
	}

	@Override
	public void run(Configuration configuration, Environment environment)
	{
		environment.jersey().register(new MainPouchBinder());
		environment.jersey().register(HelloWorldResource.class);
	}
}
