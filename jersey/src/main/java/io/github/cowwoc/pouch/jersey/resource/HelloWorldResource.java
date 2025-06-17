/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.jersey.resource;

import io.github.cowwoc.pouch.jersey.scope.RequestScope;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("helloworld")
public final class HelloWorldResource
{
	private final RequestScope scope;

	/**
	 * Creates a new resource.
	 *
	 * @param scope values and variables that are specific to the lifetime of the current HTTP request
	 * @throws NullPointerException if {@code scope} is null
	 */
	@Inject
	public HelloWorldResource(RequestScope scope)
	{
		if (scope == null)
			throw new NullPointerException("scope may not be null");
		this.scope = scope;
	}

	@GET
	@Produces("text/plain")
	public String getHello()
	{
		return "Hello world!\n" +
			"HTTP-scoped value: " + scope.getRequestedUri() + "\n" +
			"JVM-scoped value : " + scope.getMode();
	}
}