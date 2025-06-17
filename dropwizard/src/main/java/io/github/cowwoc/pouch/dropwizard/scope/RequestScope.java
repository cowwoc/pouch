/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.dropwizard.scope;

import java.net.URI;

/**
 * Holds values and variables that are specific to the lifetime of the current HTTP request.
 * <p>
 * Implementations are not thread-safe.
 */
public interface RequestScope extends TransactionScope
{
	/**
	 * Returns the requested URI.
	 *
	 * @return the requested URI
	 */
	URI getRequestedUri();
}