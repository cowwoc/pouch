/*
 * Copyright (c) 2018 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.core;

import com.github.cowwoc.pouch.core.WrappedCheckedException.CheckedRunnable;

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
	 * Runs one or more tasks, throwing any exceptions they throw after they all finish executing.
	 *
	 * @param tasks a list of tasks
	 * @throws WrappedCheckedException if any of the tasks threw checked exceptions
	 */
	public static void runAll(CheckedRunnable... tasks)
	{
		List<Exception> exceptions = new ArrayList<>();
		for (CheckedRunnable task : tasks)
		{
			try
			{
				task.run();
			}
			catch (Exception e)
			{
				exceptions.add(e);
			}
		}
		if (!exceptions.isEmpty())
		{
			Exception mainException = exceptions.get(0);
			for (int i = 1, size = exceptions.size(); i < size; ++i)
				mainException.addSuppressed(exceptions.get(i));
			throw WrappedCheckedException.wrap(mainException);
		}
	}
}