/*
 * Copyright (c) 2018 Gili Tzabari
 */
package org.bitbucket.cowwoc.pouch;

import java.util.ArrayList;
import java.util.List;

/**
 * Scope helper functions.
 */
public final class Scopes
{
	/**
	 * Prevent construction.
	 */
	private Scopes()
	{
	}

	/**
	 * Runs one or more {@code Runnable}s, throwing any exceptions they throw after they all finish executing.
	 *
	 * @param runnables the {@code Runnable}s to execute
	 */
	public static void runAll(Runnable... runnables)
	{
		List<RuntimeException> exceptions = new ArrayList<>();
		for (Runnable runnable : runnables)
		{
			try
			{
				runnable.run();
			}
			catch (RuntimeException e)
			{
				exceptions.add(e);
			}
		}
		if (!exceptions.isEmpty())
		{
			RuntimeException mainException = exceptions.get(0);
			for (int i = 1, size = exceptions.size(); i < size; ++i)
				mainException.addSuppressed(exceptions.get(i));
			throw mainException;
		}
	}
}
