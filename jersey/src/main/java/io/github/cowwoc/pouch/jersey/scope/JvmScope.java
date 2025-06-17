/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.jersey.scope;

import io.github.cowwoc.pouch.core.Scope;

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
	 * Returns the amount of time to wait for scopes to close.
	 *
	 * @return the amount of time
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