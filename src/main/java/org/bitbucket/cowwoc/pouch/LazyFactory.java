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
public abstract class LazyFactory<T> extends AbstractLazyFactory<T>
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
		return create(supplier, (closeable) ->
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
}
