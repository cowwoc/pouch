package com.github.cowwoc.pouch.dropwizard.scope;

import com.github.cowwoc.pouch.core.AbstractScope;
import com.github.cowwoc.pouch.core.Scopes;
import org.glassfish.hk2.api.ServiceLocator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The default implementation of ServerScope.
 */
public abstract class AbstractServerScope extends AbstractScope
	implements ServerScope
{
	/**
	 * The database configuration.
	 */
	protected final DatabaseScope parent;
	/**
	 * {@code true} if the scope was closed.
	 */
	protected final AtomicBoolean closed = new AtomicBoolean();

	/**
	 * @param parent The database configuration
	 * @throws NullPointerException if any of the arguments are null
	 */
	protected AbstractServerScope(DatabaseScope parent)
	{
		if (parent == null)
			throw new NullPointerException("parent may not be null");
		this.parent = parent;
	}

	@Override
	public Duration getScopeCloseTimeout()
	{
		return parent.getScopeCloseTimeout();
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
	public RequestScope createRequest(ServiceLocator serviceLocator)
	{
		if (isClosed())
			throw new IllegalStateException("Scope is closed");
		return new MainRequestScope(this, serviceLocator);
	}

	@Override
	public TransactionScope createTransactionScope()
	{
		return parent.createTransactionScope();
	}

	@Override
	public boolean isClosed()
	{
		return closed.get();
	}

	@Override
	public void close()
	{
		Scopes.runAll(() -> children.shutdown(parent.getScopeCloseTimeout()), () ->
			parent.removeChild(this));
	}
}