/*
 * Copyright (c) 2019 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */

package com.github.cowwoc.pouch.core;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Closeable helper functions.
 */
final class Closeables
{
	/**
	 * Prevent construction.
	 */
	private Closeables()
	{
	}

	/**
	 * Closes an {@code AutoCloseable}, translating exceptions into a {@code RuntimeException} on failure.
	 *
	 * @param closeable the object to close
	 */
	public static void closeWithRuntimeException(AutoCloseable closeable)
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
	}
}
