/*
 * Copyright 2013 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.servicelocator;

import org.bitbucket.cowwoc.preconditions.Preconditions;

/**
 * A factory that returns a pre-existing value.
 * <p>
 * The implementation is thread-safe.
 * <p>
 * @author Gili Tzabari
 * @param <T> the type of value returned by the factor
 */
public final class ConstantFactory<T> implements Factory<T>
{
	private final T value;

	/**
	 * Creates a new ConstantFactory.
	 * <p>
	 * @param value the value
	 * @throws NullPointerException if value is null
	 */
	public ConstantFactory(T value)
	{
		Preconditions.requireThat(value, "value").isNotNull();
		this.value = value;
	}

	@Override
	public T getValue() throws IllegalStateException
	{
		return value;
	}

	@Override
	public void close()
	{
	}
}
