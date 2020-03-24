/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.resource;

import com.github.cowwoc.pouch.jersey.scope.HttpScope;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author Gili Tzabari
 */
@Path("helloworld")
public final class HelloWorldResource
{
	private final HttpScope scope;

	/**
	 * Creates a new resource.
	 *
	 * @param scope the HTTP request scope
	 * @throws NullPointerException if {@code scope} is null
	 */
	@Inject
	public HelloWorldResource(HttpScope scope)
	{
		// Injecting either ApplicationScope or HttpScope will work
		if (scope == null)
			throw new NullPointerException("scope may not be null");
		this.scope = scope;
	}

	@GET
	@Produces("text/plain")
	public String getHello()
	{
		return "Hello world!\n" +
			"HTTP-scoped value       : " + scope.getRequestedUri() + "\n" +
			"Application-scoped value: " + scope.getMode();
	}
}
