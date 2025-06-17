/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.dropwizard.scope;

import io.github.cowwoc.pouch.core.Scope;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Holds values and variables that are specific to the lifetime of the current JVM.
 * <p>
 * Note: It is possible for a single application to run multiple JVM instances (e.g., during unit tests). This
 * class encapsulates data relevant for the entire duration of each JVM's execution.
 * <p>
 * Additionally, this class ensures that global variables, such as SLF4J, are initialized in a thread-safe
 * manner.
 * <p>
 * Implementations must be thread-safe.
 */
public interface JvmScope extends Scope
{
	/**
	 * Returns the runtime mode.
	 *
	 * @return the runtime mode
	 * @throws IllegalStateException if the scope is closed
	 */
	RunMode getMode();

	/**
	 * Returns the scheduler to use for background tasks.
	 *
	 * @return the scheduler to use for background tasks
	 * @throws IllegalStateException if the scope is closed
	 */
	ScheduledExecutorService getScheduler();

	/**
	 * @return the amount of time to wait for scopes to close
	 * @throws IllegalStateException if the scope is closed
	 */
	Duration getScopeCloseTimeout();
}