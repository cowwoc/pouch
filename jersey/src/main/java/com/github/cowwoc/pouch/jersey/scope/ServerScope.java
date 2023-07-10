package com.github.cowwoc.pouch.jersey.scope;

import org.glassfish.hk2.api.ServiceLocator;

/**
 * Configuration bound to the lifetime of the server.
 * <p>
 * Implementations must be thread-safe.
 */
public interface ServerScope extends DatabaseScope
{
	/**
	 * @param serviceLocator the Jersey dependency-injection mechanism
	 * @return a new request scope
	 * @throws NullPointerException  if {@code serviceLocator} is null
	 * @throws IllegalStateException if {@link #isClosed()}
	 */
	RequestScope createRequest(ServiceLocator serviceLocator);
}