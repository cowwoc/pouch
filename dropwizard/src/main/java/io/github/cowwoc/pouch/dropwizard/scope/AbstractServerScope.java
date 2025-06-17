package io.github.cowwoc.pouch.dropwizard.scope;

import io.github.cowwoc.pouch.core.AbstractScope;
import io.github.cowwoc.pouch.core.Scopes;
import org.glassfish.hk2.api.ServiceLocator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The default implementation of {@code ServerScope}.
 */
public abstract class AbstractServerScope extends AbstractScope
	implements ServerScope
{
	/**
	 * The database configuration.
	 */
	protected final DatabaseScope databaseScope;
	/**
	 * {@code true} if the scope was closed.
	 */
	protected final AtomicBoolean closed = new AtomicBoolean();

	/**
	 * Creates a new server scope.
	 *
	 * @param databaseScope the database configuration
	 * @throws NullPointerException if {@code databaseScope} is null
	 */
	protected AbstractServerScope(DatabaseScope databaseScope)
	{
		if (databaseScope == null)
			throw new NullPointerException("databaseScope may not be null");
		this.databaseScope = databaseScope;
	}

	@Override
	public Duration getScopeCloseTimeout()
	{
		return databaseScope.getScopeCloseTimeout();
	}

	@Override
	public RunMode getMode()
	{
		return databaseScope.getMode();
	}

	@Override
	public ScheduledExecutorService getScheduler()
	{
		return databaseScope.getScheduler();
	}

	@Override
	public DataSource getDataSource()
	{
		return databaseScope.getDataSource();
	}

	@Override
	public Connection getConnection()
	{
		return databaseScope.getConnection();
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
		return databaseScope.createTransactionScope();
	}

	@Override
	public boolean isClosed()
	{
		return closed.get();
	}

	@Override
	public void close()
	{
		Scopes.runAll(() -> children.shutdown(databaseScope.getScopeCloseTimeout()), () ->
			databaseScope.removeChild(this));
	}
}