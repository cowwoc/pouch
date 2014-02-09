/*
 * Copyright 2014 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.servicelocator;

/**
 * A factory that initializes the value lazily.
 * <p>
 * The implementation is thread-safe. All subclasses must be too.
 * <p>
 * @param <T> the type of the value
 * @author Gili Tzabari
 */
public abstract class LazyFactory<T> implements Factory<T>
{
	/**
	 * True if the value was created.
	 */
	private volatile boolean initialized;
	/**
	 * True if the factory was closed.
	 */
	private boolean closed;
	/**
	 * The value. This variable uses <a href="http://stackoverflow.com/a/6169551/14731">piggybacking
	 * synchronization</a>.
	 */
	private T value;

	/**
	 * Returns the value. Subsequent invocations of this method return the same value.
	 * <p>
	 * @return an object of type {@code <T>}
	 * @throws IllegalStateException if the factory is closed
	 */
	@Override
	public final T getValue()
	{
		if (!initialized)
		{
			synchronized (this)
			{
				if (closed)
					throw new IllegalStateException("Factory is closed");
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
	 * <p>
	 * @return the value
	 */
	protected abstract T createValue();

	/**
	 * Disposes the value. This method is invoked the first time {@link #close()} is invoked, and only
	 * if the value was already initialized.
	 */
	protected abstract void disposeValue();

	@Override
	public final void close()
	{
		synchronized (this)
		{
			if (closed)
				return;
			closed = true;
			if (!initialized)
				return;
		}
		disposeValue();
	}
}
