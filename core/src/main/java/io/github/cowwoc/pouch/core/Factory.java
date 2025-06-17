/*
 * Copyright (c) 2014 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.core;

import java.io.Closeable;

/**
 * Creates and destroys a value.
 *
 * @param <T> the type of the value
 */
public interface Factory<T> extends Reference<T>, Closeable
{
	/**
	 * Disposes the Factory and the value. Subsequent invocations of this method have no effect.
	 * Invoking any other method after this one results in {@code IllegalStateException} being thrown.
	 */
	@Override
	void close();
}