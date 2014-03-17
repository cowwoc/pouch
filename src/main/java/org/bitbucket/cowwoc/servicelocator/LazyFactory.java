/*
 * Copyright 2014 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.servicelocator;

/**
 * A factory that initializes the value lazily.
 * <p>
 * The implementation is not thread-safe.
 * <p>
 * @param <T> the type of the value
 * @author Gili Tzabari
 */
public abstract class LazyFactory<T> extends LazyReference<T>
	implements Factory<T>
{
	/**
	 * True if the factory was closed.
	 */
	private boolean closed;

	/**
	 * Disposes the value.
	 * <p>
	 * This method is invoked the first time {@link #close()} is invoked, and only if the value was
	 * already initialized. This method may not invoke any other method as the factory is already
	 * marked as closed.
	 * <p>
	 * @param value the value to dispose
	 */
	protected abstract void disposeValue(T value);

	/**
	 * {@inheritDoc}
	 * <p>
	 * @return an object of type {@code <T>}
	 * @throws IllegalStateException if the factory is closed
	 */
	@Override
	public final T getValue()
		throws IllegalStateException
	{
		if (closed)
			throw new IllegalStateException("Factory is closed");
		return super.getValue();
	}

	@Override
	public final void close()
	{
		if (closed)
			return;
		closed = true;
		if (!isInitialized())
			return;
		T value = super.getValue();
		disposeValue(value);
	}
}
