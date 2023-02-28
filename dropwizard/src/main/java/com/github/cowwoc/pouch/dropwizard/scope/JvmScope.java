/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import javax.sql.DataSource;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Values specific to the current JVM.
 * <p>
 * Implementations must be thread-safe.
 */
public interface JvmScope extends AutoCloseable
{
	/**
	 * Returns a database connection factory.
	 *
	 * @return a database connection factory
	 */
	DataSource getDataSource();

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
	 * Returns a new transaction scope.
	 *
	 * @return a new transaction scope
	 * @throws IllegalStateException if {@link #isClosed()}
	 */
	TransactionScope createTransactionScope();

	/**
	 * Returns {@code true} if the scope is closed.
	 *
	 * @return {@code true} if the scope is closed
	 */
	boolean isClosed();

	@Override
	void close();
}
