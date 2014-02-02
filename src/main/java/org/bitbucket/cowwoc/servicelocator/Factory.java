/*
 * Copyright 2013 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.servicelocator;

import java.io.Closeable;

/**
 * Creates and destroys a single value.
 * <p>
 * Implementations must be thread-safe.
 * <p>
 * @param <T> the type of the value
 * @author Gili Tzabari
 */
public interface Factory<T> extends ValueGenerator<T>, Closeable
{
	/**
	 * Disposes the Factory and the value. Subsequent invocations of this method have no effect.
	 * Invoking any other method after this one results in an undefined behavior.
	 */
	@Override
	void close();
}
