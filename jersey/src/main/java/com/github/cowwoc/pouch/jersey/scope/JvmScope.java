/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.scope;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Values specific to the current JVM.
 * <p>
 * Implementations must be thread-safe.
 */
public interface JvmScope extends AutoCloseable
{
	/**
	 * Returns the execution mode (e.g. "main", "test").
	 *
	 * @return the execution mode (e.g. "main", "test")
	 */
	String getMode();

	/**
	 * @return the amount of time to wait for scopes to close
	 * @throws IllegalStateException if the scope is closed
	 */
	Duration getScopeCloseTimeout();

	/**
	 * Returns the scheduler to use for background tasks.
	 *
	 * @return the scheduler to use for background tasks
	 */
	ScheduledExecutorService getScheduler();

	/**
	 * Adds a child scope.
	 *
	 * @param child the child scope
	 * @throws NullPointerException  if {@code child} is null
	 * @throws IllegalStateException if the scope is closed
	 */
	void addChild(AutoCloseable child);

	/**
	 * Removes a child scope.
	 *
	 * @param child the child scope
	 * @throws NullPointerException  if {@code child} is null
	 * @throws IllegalStateException if the scope is closed
	 */
	void removeChild(AutoCloseable child);

	/**
	 * Returns {@code true} if the scope is closed.
	 *
	 * @return {@code true} if the scope is closed
	 */
	boolean isClosed();

	@Override
	void close();
}