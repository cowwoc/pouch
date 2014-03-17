/*
 * Copyright 2014 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.pouch;

/**
 * A reference to a value.
 * <p>
 * @author Gili Tzabari
 * @param <T> the type of value returned by the object
 */
public interface Reference<T>
{
	/**
	 * Returns the value. Subsequent invocations of this method return the same value.
	 * <p>
	 * @return an object of type {@code <T>}
	 */
	T getValue();
}
