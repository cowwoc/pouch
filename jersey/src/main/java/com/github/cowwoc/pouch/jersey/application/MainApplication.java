/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.application;

import com.github.cowwoc.pouch.jersey.scope.MainPouchBinder;
import com.github.cowwoc.pouch.jersey.resource.HelloWorldResource;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jersey application for the "main" codebase.
 *
 * @author Gili Tzabari
 */
public final class MainApplication extends ResourceConfig
{
	public MainApplication()
	{
		register(new MainPouchBinder());
		register(HelloWorldResource.class);
	}
}
