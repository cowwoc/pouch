/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.jersey.scope;

import io.github.cowwoc.pouch.core.AbstractScope;
import jakarta.ws.rs.core.UriInfo;
import org.glassfish.hk2.api.ServiceLocator;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

/**
 * HttpScope common to main and test codebases.
 */
abstract class AbstractRequestScope extends AbstractScope
	implements RequestScope
{
	private final ServerScope parent;
	private final ServiceLocator serviceLocator;
	private boolean closed;

	/**
	 * Creates a new HTTP scope.
	 *
	 * @param parent         the parent scope
	 * @param serviceLocator the Jersey dependency-injection mechanism
	 * @throws NullPointerException if any of the arguments are null
	 */
	AbstractRequestScope(ServerScope parent, ServiceLocator serviceLocator)
	{
		if (parent == null)
			throw new NullPointerException("parent may not be null");
		if (serviceLocator == null)
			throw new NullPointerException("serviceLocator may not be null");
		this.parent = parent;
		this.serviceLocator = serviceLocator;
	}

	/**
	 * Returns the Jersey dependency-injection mechanism.
	 *
	 * @return the dependency-injection mechanism
	 */
	protected ServiceLocator getServiceLocator()
	{
		return serviceLocator;
	}

	@Override
	public Duration getScopeCloseTimeout()
	{
		return parent.getScopeCloseTimeout();
	}

	@Override
	public DataSource getDataSource()
	{
		return parent.getDataSource();
	}

	@Override
	public Connection getConnection()
	{
		return parent.getConnection();
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
	public void close()
	{
		if (closed)
			return;
		closed = true;
		parent.removeChild(this);
	}
}