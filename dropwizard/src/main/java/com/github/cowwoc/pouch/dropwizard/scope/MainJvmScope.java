/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import com.github.cowwoc.pouch.core.LazyReference;
import com.github.cowwoc.pouch.core.Reference;
import org.glassfish.hk2.api.ServiceLocator;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * JvmScope for the main codebase.
 *
 * @author Gili Tzabari
 */
public final class MainJvmScope extends AbstractJvmScope
{
	/**
	 * Example task that monitors database uptime.
	 */
	private static class CheckDatabase implements Runnable
	{
		private final JvmScope jvmScope;
		private final Logger log = LoggerFactory.getLogger(CheckDatabase.class);

		/**
		 * @param jvmScope values specific to an application run
		 * @throws NullPointerException if {@code jvmScope} is null
		 */
		CheckDatabase(JvmScope jvmScope)
		{
			if (jvmScope == null)
				throw new NullPointerException("jvmScope may not be null");
			this.jvmScope = jvmScope;
		}

		@Override
		public void run()
		{
			try (TransactionScope transaction = jvmScope.createTransactionScope())
			{
				Connection connection = transaction.getConnection();
				log.info("Database is up at {}", connection.getMetaData().getURL());
			}
			catch (SQLException e)
			{
				log.error("", e);
				throw new RuntimeException(e);
			}
			catch (RuntimeException | Error e)
			{
				log.error("", e);
				throw e;
			}
		}
	}

	private final Reference<DataSource> dataSource = LazyReference.create(() ->
	{
		JdbcDataSource result = new JdbcDataSource();
		result.setUrl("jdbc:h2:mem:main");
		result.setUser("sa");
		return result;
	});

	/**
	 * Creates a new MainJvmScope.
	 */
	public MainJvmScope()
	{
		getScheduler().scheduleWithFixedDelay(new CheckDatabase(this), 5, 5, TimeUnit.SECONDS);
	}

	@Override
	public DataSource getDataSource()
	{
		return dataSource.getValue();
	}

	@Override
	public String getMode()
	{
		return "main";
	}

	@Override
	public TransactionScope createTransactionScope()
	{
		if (isClosed())
			throw new IllegalStateException("Scope is closed");
		return children.createChildScope(() -> new MainTransactionScope(this));
	}

	@Override
	public HttpScope createHttpScope(ServiceLocator serviceLocator)
	{
		if (isClosed())
			throw new IllegalStateException("Scope is closed");
		return children.createChildScope(() -> new MainHttpScope(this, serviceLocator));
	}

	@Override
	protected void beforeClose()
	{
	}
}