/*
 * Copyright 2014 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.pouch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A factory that initializes a value on demand.
 * <p>
 * Instances of LazyFactory are not safe for use by multiple threads. If such synchronization is
 * required then it is recommended that {@link ConcurrentLazyFactory} be used.
 * <p>
 * @param <T> the type of the value
 * @author Gili Tzabari
 */
public abstract class LazyFactory<T> extends AbstractLazyReference<T>
	implements Factory<T>
{
	/**
	 * Creates a new LazyFactory.
	 * <p>
	 * @param <T>      the type of value returned by the factory
	 * @param supplier supplies the factory value
	 * @param disposer implements {@link #disposeValue(java.lang.Object) disposeValue(T)}
	 * @return a new LazyFactory
	 */
	public static <T> LazyFactory<T> create(final Supplier<T> supplier, final Consumer<T> disposer)
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
	 * <p>
	 * @param <T>      the type of value returned by the factory
	 * @param supplier supplies the factory value
	 * @return a new LazyFactory
	 */
	public static <T extends AutoCloseable> LazyFactory<T> create(final Supplier<T> supplier)
	{
		return create(supplier, closeable ->
		{
			try
			{
				closeable.close();
			}
			catch (IOException e)
			{
				throw new UncheckedIOException(e);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		});
	}
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
