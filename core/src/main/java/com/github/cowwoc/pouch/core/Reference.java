/*
 * Copyright (c) 2014 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.core;

/**
 * A reference to a value.
 *
 * @param <T> the type of value returned by the object
 */
public interface Reference<T>
{
	/**
	 * Returns the value. Subsequent invocations of this method return the same value.
	 *
	 * @return an object of type {@code <T>}
	 */
	T getValue();

	/**
	 * Indicates if the value was initialized.
	 * <p>
	 * A value may get initialized immediately after this method returns {@code false} but once it
	 * returns {@code true} it will continue to do so indefinitely.
	 *
	 * @return true if the value was initialized
	 */
	boolean isInitialized();
}
