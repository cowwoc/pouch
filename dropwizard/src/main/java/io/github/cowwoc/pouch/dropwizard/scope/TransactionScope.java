/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.dropwizard.scope;

import java.sql.Connection;

/**
 * Holds values and variables that are specific to the lifetime of the current database transaction.
 * <p>
 * Implementations are not thread-safe.
 */
public interface TransactionScope extends DatabaseScope
{
	/**
	 * @return the database connection associated with the transaction
	 */
	Connection getConnection();
}