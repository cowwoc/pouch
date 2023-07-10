package com.github.cowwoc.pouch.dropwizard.scope;

import org.glassfish.hk2.api.ServiceLocator;

/**
 * The ServerScope used by tests.
 */
public final class TestServerScope extends AbstractServerScope
{
	/**
	 * @param database the database configuration
	 * @throws NullPointerException if any of the arguments are null
	 */
	public TestServerScope(DatabaseScope database)
	{
		super(database);
	}

	@Override
	public RequestScope createRequest(ServiceLocator serviceLocator)
	{
		ensureOpen();
		return new TestRequestScope(this, serviceLocator);
	}

	@Override
	public void close()
	{
		if (!closed.compareAndSet(false, true))
			return;
		super.close();
	}
}