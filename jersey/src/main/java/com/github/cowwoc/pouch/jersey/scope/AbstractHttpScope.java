/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.scope;

import org.glassfish.hk2.api.ServiceLocator;

import javax.sql.DataSource;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.sql.Connection;
import java.util.concurrent.ScheduledExecutorService;

/**
 * HttpScope common to main and test codebases.
 *
 * @author Gili Tzabari
 */
abstract class AbstractHttpScope
	implements HttpScope
{
	private final ApplicationScopeSpi parent;
	private final TransactionScope transaction;
	private final ServiceLocator serviceLocator;
	private boolean closed;

	/**
	 * Creates a new HTTP scope.
	 *
	 * @param parent         the parent scope
	 * @param serviceLocator the Jersey dependency-injection mechanism
	 * @throws NullPointerException if any of the arguments are null
	 */
	AbstractHttpScope(ApplicationScopeSpi parent, ServiceLocator serviceLocator)
		throws NullPointerException
	{
		if (parent == null)
			throw new NullPointerException("parent may not be null");
		if (serviceLocator == null)
			throw new NullPointerException("serviceLocator may not be null");
		this.parent = parent;
		this.serviceLocator = serviceLocator;
		this.transaction = parent.createTransactionScope();
	}

	/**
	 * @return the Jersey dependency-injection mechanism
	 */
	protected ServiceLocator getServiceLocator()
	{
		return serviceLocator;
	}

	@Override
	public Connection getConnection()
	{
		return transaction.getConnection();
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
	public URI getRequestedUri()
	{
		UriInfo uriInfo = serviceLocator.getService(UriInfo.class);
		return uriInfo.getRequestUri();
	}

	@Override
	public boolean isClosed()
	{
		return closed;
	}

	@Override
	public void close() throws RuntimeException
	{
		if (closed)
			return;
		closed = true;
		try
		{
			transaction.close();
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
