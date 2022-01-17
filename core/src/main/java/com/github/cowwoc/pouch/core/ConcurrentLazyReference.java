/*
 * Copyright (c) 2014 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.core;

import java.util.function.Supplier;

/**
 * A thread-safe reference that initializes a value on demand.
 * <p>
 * The implementation is thread-safe.
 *
 * @param <T> the type of object being referenced
 */
public abstract class ConcurrentLazyReference<T> implements Reference<T>
{
	/**
	 * Creates a new {@code ConcurrentLazyReference}.
	 *
	 * @param <T>      the type of value returned by the reference
	 * @param supplier supplies the reference value
	 * @return a new {@code ConcurrentLazyReference}
	 */
	public static <T> ConcurrentLazyReference<T> create(Supplier<T> supplier)
	{
		return new ConcurrentLazyReference<T>()
		{
			@Override
			protected T createValue()
			{
				return supplier.get();
			}
		};
	}

	/**
	 * True if the value was created.
	 */
	private volatile boolean initialized;
	/**
	 * The value. This variable uses <a href="http://stackoverflow.com/a/6169551/14731">piggybacking
	 * synchronization</a>.
	 */
	private T value;

	/**
	 * Returns true if the value was initialized.
	 *
	 * @return true if the value was initialized
	 */
	public boolean isInitialized()
	{
		return initialized;
	}

	@Override
	public final T getValue()
	{
		if (!initialized)
		{
			synchronized (this)
			{
				if (!initialized)
				{
					this.value = createValue();
					initialized = true;
				}
			}
		}
		return this.value;
	}

	/**
	 * Creates the value. This method is invoked the first time {@link #getValue()} is invoked.
	 *
	 * @return the value
	 */
	protected abstract T createValue();

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder("ConcurrentLazyReference\n" +
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
