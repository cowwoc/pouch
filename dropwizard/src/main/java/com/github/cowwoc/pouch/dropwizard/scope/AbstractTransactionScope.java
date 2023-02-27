/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import com.github.cowwoc.pouch.core.Factory;
import com.github.cowwoc.pouch.core.LazyFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * TransactionScope common to main and test codebases.
 *
 * @author Gili Tzabari
 */
abstract class AbstractTransactionScope implements TransactionScope
{
	protected final JvmScope parent;
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
	AbstractTransactionScope(JvmScope parent)
	{
		if (parent == null)
			throw new NullPointerException("parent may not be null");
		this.parent = parent;
	}

	@Override
	public DataSource getDataSource()
	{
		return parent.getDataSource();
	}

	@Override
	public String getMode()
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
		AbstractJvmScope parent = (AbstractJvmScope) this.parent;
		try
		{
			connection.close();
			beforeClose();
		}
		finally
		{
			parent.onClosed(this);
		}
	}

	/**
	 * A method that is invoked before closing the scope. Subclasses wishing to extend {@code close()}
	 * should override this method.
	 */
	protected abstract void beforeClose();
}
