/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import com.github.cowwoc.pouch.core.AbstractScope;
import com.github.cowwoc.pouch.core.Factory;
import com.github.cowwoc.pouch.core.LazyFactory;
import com.github.cowwoc.pouch.core.Scopes;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

/**
 * TransactionScope common to main and test codebases.
 */
public final class DefaultTransactionScope extends AbstractScope
	implements TransactionScope
{
	private final DatabaseScope parent;
	private final Factory<Connection> connection = LazyFactory.create(() ->
	{
		DataSource ds = getDataSource();
		try
		{
			return ds.getConnection();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}, theConnection ->
	{
		try
		{
			// Any uncommitted changes should be rolled back before returning a connection to the pool.
			// @see http://stackoverflow.com/a/9644783/14731
			try (theConnection)
			{
				if (!theConnection.getAutoCommit() && !theConnection.isClosed())
					theConnection.rollback();
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	});
	private boolean closed;

	/**
	 * Creates a new transaction scope.
	 *
	 * @param parent the parent scope
	 * @throws NullPointerException if {@code parent} is null
	 */
	public DefaultTransactionScope(DatabaseScope parent)
	{
		if (parent == null)
			throw new NullPointerException("parent may not be null");
		this.parent = parent;
		parent.addChild(this);
	}

	@Override
	public DataSource getDataSource()
	{
		return parent.getDataSource();
	}

	@Override
	public RunMode getMode()
	{
		return parent.getMode();
	}

	@Override
	public ScheduledExecutorService getScheduler()
	{
		return parent.getScheduler();
	}

	@Override
	public TransactionScope createTransactionScope()
	{
		return parent.createTransactionScope();
	}

	@Override
	public Connection getConnection()
	{
		return connection.getValue();
	}

	@Override
	public Duration getScopeCloseTimeout()
	{
		return parent.getScopeCloseTimeout();
	}

	@Override
	public boolean isClosed()
	{
		return closed;
	}

	@Override
	public void close()
	{
		if (closed)
			return;
		closed = true;
		Scopes.runAll(connection::close, () -> parent.removeChild(this));
	}
}