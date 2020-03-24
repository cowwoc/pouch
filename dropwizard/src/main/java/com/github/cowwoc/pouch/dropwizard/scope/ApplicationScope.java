/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import java.util.concurrent.ScheduledExecutorService;
import javax.sql.DataSource;

/**
 * Values specific to an application run.
 * <p>
 * Implementations must be thread-safe.
 *
 * @author Gili Tzabari
 */
public interface ApplicationScope extends AutoCloseable
{
	/**
	 * @return a database connection factory
	 */
	DataSource getDataSource();

	/**
	 * @return the execution mode (e.g. "main", "test")
	 */
	String getMode();

	/**
	 * @return the scheduler to use for background tasks
	 */
	ScheduledExecutorService getScheduler();

	/**
	 * @return a new transaction scope
	 * @throws IllegalStateException if {@link #isClosed()}
	 */
	TransactionScope createTransactionScope() throws IllegalStateException;

	/**
	 * @return true if the scope is closed
	 */
	boolean isClosed();

	@Override
	void close() throws RuntimeException;
}
