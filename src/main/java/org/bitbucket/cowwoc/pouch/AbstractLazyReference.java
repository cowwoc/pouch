/*
 * Copyright 2014 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.pouch;

/**
 * Implements {@link LazyReference} without the static factory methods.
 * <p>
 * @author Gili Tzabari
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
	 * <p>
	 * @return the value
	 */
	protected abstract T createValue();

	/**
	 * @return true if the value was initialized
	 */
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

}
