package com.github.cowwoc.pouch.jersey.scope;

import com.github.cowwoc.pouch.core.ConcurrentChildScopes;
import com.github.cowwoc.pouch.core.Scopes;
import org.glassfish.hk2.api.ServiceLocator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractServerScope implements ServerScope
{
	private final DatabaseScope parent;
	/**
	 * The children of this scope.
	 */
	protected final ConcurrentChildScopes children = new ConcurrentChildScopes();
	/**
	 * {@code true} if the scope was closed.
	 */
	protected final AtomicBoolean closed = new AtomicBoolean();

	/**
	 * @param parent The database configuration
	 * @throws NullPointerException if any of the arguments are null
	 */
	public AbstractServerScope(DatabaseScope parent)
	{
		if (parent == null)
			throw new NullPointerException("parent may not be null");
		this.parent = parent;

		parent.addChild(this);
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
	public void addChild(AutoCloseable child)
	{
		children.add(child);
	}

	@Override
	public void removeChild(AutoCloseable child)
	{
		children.remove(child);
	}

	/**
	 * @throws IllegalStateException if the scope is closed
	 */
	protected void ensureOpen()
	{
		if (closed.get())
			throw new IllegalStateException("Scope is closed");
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