package com.github.cowwoc.pouch.dropwizard.scope;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Database configuration.
 * <p>
 * Implementations must be thread-safe.
 */
public interface DatabaseScope extends JvmScope
{
	/**
	 * Returns a database connection factory.
	 *
	 * @return a database connection factory
	 */
	DataSource getDataSource();

	/**
	 * Returns a database connection.
	 *
	 * @return a database connection
	 */
	Connection getConnection();

	/**
	 * Returns a new transaction scope.
	 *
	 * @return a new transaction scope
	 * @throws IllegalStateException if {@link #isClosed()}
	 */
	TransactionScope createTransactionScope();
}