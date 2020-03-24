/*
 * Copyright (c) 2014 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.core;

/**
 * Implements {@link LazyReference} without the static factory methods.
 *
 * @param <T> the type of object being referenced
 */
abstract class AbstractLazyReference<T> implements Reference<T>
{
	/**
	 * True if the value was created.
	 */
	private boolean initialized;
	/**
	 * The value.
	 */
	private T value;

	/**
	 * Creates the value. This method is invoked the first time {@link #getValue()} is invoked.
	 *
	 * @return the value
	 */
	protected abstract T createValue();

	@Override
	public boolean isInitialized()
	{
		return initialized;
	}

	@Override
	public T getValue()
	{
		if (!initialized)
		{
			this.value = createValue();
			initialized = true;
		}
		return this.value;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder("LazyReference\n" +
			"{\n" +
			"  initialized: " + initialized);
		if (initialized)
		{
			result.append(",\n").
				append("  value: ").append(value);
		}
		result.append("\n").
			append("}");
		return result.toString();
	}
}
