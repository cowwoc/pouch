/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.scope;

import com.github.cowwoc.pouch.core.Scope;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Holds values and variables that are specific to the lifetime of the current JVM.
 * <p>
 * Implementations must be thread-safe.
 */
public interface JvmScope extends Scope
{
	/**
	 * Returns the runtime mode
	 *
	 * @return the runtime mode
	 * @throws IllegalStateException if the scope is closed
	 */
	RunMode getMode();

	/**
	 * @return the amount of time to wait for scopes to close
	 * @throws IllegalStateException if the scope is closed
	 */
	Duration getScopeCloseTimeout();

	/**
	 * Returns the scheduler to use for background tasks.
	 *
	 * @return the scheduler to use for background tasks
	 * @throws IllegalStateException if the scope is closed
	 */
	ScheduledExecutorService getScheduler();
}