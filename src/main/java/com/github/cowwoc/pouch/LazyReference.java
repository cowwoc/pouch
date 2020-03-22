/*
 * Copyright (c) 2014 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch;

import java.util.function.Supplier;

/**
 * A reference that initializes a value on demand.
 * <p>
 * Instances of LazyFactory are not safe for use by multiple threads. If such synchronization is
 * required then it is recommended that {@link ConcurrentLazyReference} be used.
 *
 * @param <T> the type of object being referenced
 */
public abstract class LazyReference<T> extends AbstractLazyReference<T>
{
	/**
	 * Creates a new {@code LazyReference}.
	 *
	 * @param <T>      the type of value returned by the reference
	 * @param supplier supplies the reference value
	 * @return a new {@code LazyReference}
	 */
	public static <T> LazyReference<T> create(Supplier<T> supplier)
	{
		return new LazyReference<>()
		{
			@Override
			protected T createValue()
			{
				return supplier.get();
			}
		};
	}
}
