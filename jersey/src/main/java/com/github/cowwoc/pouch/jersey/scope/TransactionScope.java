/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.scope;

import java.sql.Connection;

/**
 * Values specific to a database transaction.
 * <p>
 * Implementations are not thread-safe.
 *
 * @author Gili Tzabari
 */
public interface TransactionScope extends ApplicationScope
{
	/**
	 * @return the database connection associated with the transaction
	 */
	Connection getConnection();
}
