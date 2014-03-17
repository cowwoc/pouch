/*
 * Copyright 2014 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.servicelocator;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * A factory that initializes the value lazily.
 * <p>
 * The implementation is thread-safe.
 * <p>
 * @param <T> the type of the value
 * @author Gili Tzabari
 */
public abstract class ConcurrentLazyFactory<T> implements Factory<T>
{
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReadLock readLock = lock.readLock();
	private final WriteLock writeLock = lock.writeLock();
	/**
	 * True if the value was created.
	 */
	private boolean initialized;
	/**
	 * True if the factory was closed.
	 */
	private boolean closed;
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
	 * Returns the value. Subsequent invocations of this method return the same value.
	 * <p>
	 * @return an object of type {@code <T>}
	 * @throws IllegalStateException if the factory is closed
	 */
	@Override
	public final T getValue()
	{
		readLock.lock();
		try
		{
			if (closed)
				throw new IllegalStateException("Factory is closed");
			if (!initialized)
			{
				readLock.unlock();
				writeLock.lock();
				try
				{
					if (closed)
						throw new IllegalStateException("Factory is closed");
					if (!initialized)
					{
						this.value = createValue();
						initialized = true;
					}
				}
				finally
				{
					writeLock.unlock();
					readLock.lock();
				}
			}
			return this.value;
		}
		finally
		{
			readLock.unlock();
		}
	}

	@Override
	public final void close()
	{
		readLock.lock();
		try
		{
			if (closed)
				return;
			readLock.unlock();
			writeLock.lock();
			try
			{
				if (closed)
					return;
				closed = true;
				if (!initialized)
					return;
				disposeValue(value);
			}
			finally
			{
				writeLock.unlock();
				readLock.lock();
			}
		}
		finally
		{
			readLock.unlock();
		}
	}
}
