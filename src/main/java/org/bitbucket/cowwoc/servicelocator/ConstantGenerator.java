/*
 * Copyright 2014 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.servicelocator;

import org.bitbucket.cowwoc.preconditions.Preconditions;

/**
 * A {@code ValueGenerator} that returns a pre-existing value.
 * <p>
 * The implementation is thread-safe.
 * <p>
 * @author Gili Tzabari
 * @param <T> the type of value returned by the generator
 */
public final class ConstantGenerator<T> implements ValueGenerator<T>
{
	private final T value;

	/**
	 * Creates a new ConstantGenerator.
	 * <p>
	 * @param value the value
	 * @throws NullPointerException if value is null
	 */
	public ConstantGenerator(T value)
	{
		Preconditions.requireThat(value, "value").isNotNull();
		this.value = value;
	}

	@Override
	public T getValue() throws IllegalStateException
	{
		return value;
	}
}
