package com.github.cowwoc.pouch.dropwizard.scope;

import org.glassfish.hk2.api.ServiceLocator;

/**
 * The ServerScope used by the application.
 */
public final class MainServerScope extends AbstractServerScope
{
	/**
	 * @param database the database configuration
	 * @throws NullPointerException if any of the arguments are null
	 */
	public MainServerScope(DatabaseScope database)
	{
		super(database);
	}

	@Override
	public RequestScope createRequest(ServiceLocator serviceLocator)
	{
		ensureOpen();
		return new MainRequestScope(this, serviceLocator);
	}

	@Override
	public void close()
	{
		if (!closed.compareAndSet(false, true))
			return;
		super.close();
	}
}