/*
 * Copyright 2014 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.pouch;

import java.util.function.Supplier;

/**
 * A reference that initializes a value on demand.
 * <p>
 * Instances of LazyFactory are not safe for use by multiple threads. If such synchronization is
 * required then it is recommended that {@link ConcurrentLazyReference} be used.
 * <p>
 * @author Gili Tzabari
 * @param <T> the type of object being referenced
 */
public abstract class LazyReference<T> extends AbstractLazyReference<T>
{
	/**
	 * @param <T>      the type of value returned by the reference
	 * @param supplier supplies the reference value
	 * @return a new LazyReference
	 */
	public static <T> LazyReference<T> create(final Supplier<T> supplier)
	{
		return new LazyReference<T>()
		{
			@Override
			protected T createValue()
			{
				return supplier.get();
			}
		};
	}
}
