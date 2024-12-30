/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import com.github.cowwoc.pouch.core.Scope;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Values specific to the current JVM.
 * <p>
 * Implementations must be thread-safe.
 */
public interface JvmScope extends Scope
{
	/**
	 * Returns the execution mode (e.g. "main", "test").
	 *
	 * @return the execution mode (e.g. "main", "test")
	 */
	String getMode();

	/**
	 * Returns the scheduler to use for background tasks.
	 *
	 * @return the scheduler to use for background tasks
	 */
	ScheduledExecutorService getScheduler();

	/**
	 * @return the amount of time to wait for scopes to close
	 * @throws IllegalStateException if the scope is closed
	 */
	Duration getScopeCloseTimeout();
}