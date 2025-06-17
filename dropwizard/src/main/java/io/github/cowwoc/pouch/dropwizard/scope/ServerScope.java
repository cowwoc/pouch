package io.github.cowwoc.pouch.dropwizard.scope;

import org.glassfish.hk2.api.ServiceLocator;

/**
 * Configuration bound to the lifetime of the server.
 * <p>
 * Implementations must be thread-safe.
 */
public interface ServerScope extends DatabaseScope
{
	/**
	 * Creates a new request scope.
	 *
	 * @param serviceLocator the Dropwizard dependency-injection mechanism
	 * @return the scope
	 * @throws NullPointerException  if {@code serviceLocator} is null
	 * @throws IllegalStateException if {@link #isClosed()}
	 */
	RequestScope createRequest(ServiceLocator serviceLocator);
}