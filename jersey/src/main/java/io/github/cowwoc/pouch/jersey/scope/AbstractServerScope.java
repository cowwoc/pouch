package io.github.cowwoc.pouch.jersey.scope;

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
	private final DatabaseScope parent;
	/**
	 * {@code true} if the scope was closed.
	 */
	protected final AtomicBoolean closed = new AtomicBoolean();

	/**
	 * Creates a new instance.
	 *
	 * @param databaseScope The database configuration
	 * @throws NullPointerException if {@code databaseScope} is null
	 */
	protected AbstractServerScope(DatabaseScope databaseScope)
	{
		if (databaseScope == null)
			throw new NullPointerException("databaseScope may not be null");
		this.parent = databaseScope;

		databaseScope.addChild(this);
	}

	@Override
	public Duration getScopeCloseTimeout()
	{
		return parent.getScopeCloseTimeout();
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
		Scopes.runAll(() -> children.shutdown(getScopeCloseTimeout()), () -> parent.removeChild(this));
	}
}