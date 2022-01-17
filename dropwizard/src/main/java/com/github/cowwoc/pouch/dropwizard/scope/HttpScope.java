/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import java.net.URI;

/**
 * Values specific to an HTTP request.
 * <p>
 * Implementations are not thread-safe.
 */
public interface HttpScope extends TransactionScope
{
	/**
	 * @return the requested URI
	 */
	URI getRequestedUri();
}
