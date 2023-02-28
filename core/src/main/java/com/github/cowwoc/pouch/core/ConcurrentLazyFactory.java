/*
 * Copyright (c) 2014 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.core;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A thread-safe factory that initializes a value on demand.
 * <p>
 * The implementation is thread-safe.
 *
 * @param <T> the type of the value
 */
public abstract class ConcurrentLazyFactory<T> implements Factory<T>
{
	/**
	 * Creates a new {@code ConcurrentLazyFactory}.
	 *
	 * @param <T>      the type of value returned by the factory
	 * @param supplier supplies the factory value
	 * @param disposer implements {@link #disposeValue(java.lang.Object) disposeValue(T)}
	 * @return a new {@code ConcurrentLazyFactory}
	 */
	public static <T> ConcurrentLazyFactory<T> create(Supplier<T> supplier, Consumer<T> disposer)
	{
		return new ConcurrentLazyFactory<T>()
		{
			@Override
			protected T createValue()
			{
				return supplier.get();
			}

			@Override
			protected void disposeValue(T value)
			{
				disposer.accept(value);
			}
		};
	}

	/**
	 * Creates a new {@code ConcurrentLazyFactory} that disposes its value by invoking
	 * {@code close()}. If {@code close()} throws a checked exception, it is wrapped in a
	 * {@code RuntimeException} or an exception that extends it.
	 *
	 * @param <T>      the type of value returned by the factory
	 * @param supplier supplies the factory value
	 * @return a new {@code ConcurrentLazyFactory}
	 */
	public static <T extends AutoCloseable> ConcurrentLazyFactory<T> create(Supplier<T> supplier)
	{
		return create(supplier, value -> WrappedCheckedException.wrap(value::close));
	}

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReadLock readLock = lock.readLock();
	private final WriteLock writeLock = lock.writeLock();
	/**
	 * {@code true} if the value was created.
	 */
	private boolean initialized;
	/**
	 * {@code true} if the factory was closed.
	 */
	private boolean closed;
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

	/**
	 * Disposes the value.
	 * <p>
	 * This method is invoked the first time {@link #close()} is invoked, and only if the value was
	 * already initialized. This method may not invoke any other method as the factory is already
	 * marked as closed.
	 *
	 * @param value the value to dispose
	 */
	protected abstract void disposeValue(T value);

	/**
	 * Returns the value. Subsequent invocations of this method return the same value.
	 *
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
	public boolean isInitialized()
	{
		return initialized;
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

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder("ConcurrentLazyFactory\n" +
			"{\n" +
			"  initialized: " + initialized);
		if (initialized)
		{
			result.append(",\n").
				append("  value: ").append(getValue());
		}
		result.append("\n").
			append("  closed: ").append(closed).append("\n").
			append("}");
		return result.toString();
	}
}