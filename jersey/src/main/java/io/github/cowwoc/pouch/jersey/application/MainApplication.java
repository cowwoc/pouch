/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.jersey.application;

import io.github.cowwoc.pouch.jersey.resource.HelloWorldResource;
import io.github.cowwoc.pouch.jersey.scope.MainPouchBinder;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jersey application for the "main" codebase.
 */
public final class MainApplication extends ResourceConfig
{
	public MainApplication()
	{
		register(MainPouchBinder.class);
		register(HelloWorldResource.class);
	}
}