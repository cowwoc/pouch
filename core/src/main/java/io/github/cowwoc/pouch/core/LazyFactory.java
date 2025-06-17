/*
 * Copyright (c) 2014 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.core;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A factory that initializes a value on demand.
 * <p>
 * Instances of LazyFactory are not safe for use by multiple threads. If such synchronization is
 * required then it is recommended that {@link ConcurrentLazyFactory} be used.
 *
 * @param <T> the type of the value
 */
public abstract class LazyFactory<T> extends AbstractLazyReference<T>
	implements Factory<T>
{
	/**
	 * Creates a new instance.
	 */
	protected LazyFactory()
	{
	}

	/**
	 * Creates a new {@code LazyFactory}.
	 *
	 * @param <T>      the type of value returned by the factory
	 * @param supplier supplies the factory value
	 * @param disposer implements {@link #disposeValue(java.lang.Object) disposeValue(T)}
	 * @return a new {@code LazyFactory}
	 */
	public static <T> LazyFactory<T> create(Supplier<T> supplier, Consumer<T> disposer)
	{
		return new LazyFactory<T>()
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
	 * Creates a new {@code LazyFactory} that disposes its value by invoking {@code close()}. If
	 * {@code close()} throws a checked exception, it is wrapped in a {@code RuntimeException} or an
	 * exception that extends it.
	 *
	 * @param <T>      the type of value returned by the factory
	 * @param supplier supplies the factory value
	 * @return a new {@code LazyFactory}
	 */
	public static <T extends AutoCloseable> LazyFactory<T> create(Supplier<T> supplier)
	{
		return create(supplier, value ->
		{
			try
			{
				value.close();
			}
			catch (Exception e)
			{
				throw WrappedCheckedException.wrap(e);
			}
		});
	}

	/**
	 * {@code true} if the factory was closed.
	 */
	private boolean closed;

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
	 * {@inheritDoc}
	 *
	 * @return an object of type {@code <T>}
	 * @throws IllegalStateException if the factory is closed
	 */
	@Override
	public final T getValue()
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

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder("LazyFactory\n" +
			"{\n" +
			"  initialized: " + isInitialized());
		if (isInitialized())
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