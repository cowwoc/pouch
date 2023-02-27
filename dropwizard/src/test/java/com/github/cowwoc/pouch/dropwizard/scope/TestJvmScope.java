/*
 * Copyright 2016 Gili Tzabari. Licensed under the Apache License, Version 2.0:
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import com.github.cowwoc.pouch.core.LazyReference;
import com.github.cowwoc.pouch.core.Reference;
import org.glassfish.hk2.api.ServiceLocator;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JvmScope for the test codebase.
 *
 * @author Gili Tzabari
 */
public final class TestJvmScope extends AbstractJvmScope
{
	private static final AtomicInteger NEXT_DATABASE_ID = new AtomicInteger(1);
	private final Reference<DataSource> dataSource = LazyReference.create(() ->
	{
		JdbcDataSource result = new JdbcDataSource();
		result.setUrl("jdbc:h2:mem:test" + NEXT_DATABASE_ID.getAndIncrement());
		result.setUser("sa");
		return result;
	});

	@Override
	public DataSource getDataSource()
	{
		return dataSource.getValue();
	}

	@Override
	public String getMode()
	{
		return "test";
	}

	@Override
	public TransactionScope createTransactionScope()
	{
		if (isClosed())
			throw new IllegalStateException("Scope is closed");
		return children.createChildScope(() -> new TestTransactionScope(this));
	}

	@Override
	public HttpScope createHttpScope(ServiceLocator serviceLocator)
	{
		if (isClosed())
			throw new IllegalStateException("Scope is closed");
		return children.createChildScope(() -> new TestHttpScope(this, serviceLocator));
	}

	@Override
	protected void beforeClose()
	{
	}
}
