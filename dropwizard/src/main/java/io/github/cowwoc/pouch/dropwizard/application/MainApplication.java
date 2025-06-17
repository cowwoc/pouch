/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.dropwizard.application;

import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.github.cowwoc.pouch.dropwizard.resource.HelloWorldResource;
import io.github.cowwoc.pouch.dropwizard.scope.MainPouchBinder;

/**
 * Jersey application for the "main" codebase.
 */
public final class MainApplication extends Application<Configuration>
{
	/**
	 * The main entry-point.
	 *
	 * @param args the command-line arguments
	 * @throws Exception if an error occurs
	 */
	public static void main(String[] args) throws Exception
	{
		new MainApplication().run(args);
	}

	@Override
	public void run(Configuration configuration, Environment environment)
	{
		environment.jersey().register(MainPouchBinder.class);
		environment.jersey().register(HelloWorldResource.class);
	}
}