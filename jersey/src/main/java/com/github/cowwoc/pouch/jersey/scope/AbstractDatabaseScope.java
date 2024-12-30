package com.github.cowwoc.pouch.jersey.scope;

import com.github.cowwoc.pouch.core.AbstractScope;
import com.github.cowwoc.pouch.core.Scopes;
import com.github.cowwoc.pouch.core.WrappedCheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractDatabaseScope extends AbstractScope
	implements DatabaseScope
{
	/**
	 * The parent configuration.
	 */
	protected final JvmScope parent;
	/**
	 * {@code true} if the scope was closed.
	 */
	protected final AtomicBoolean closed = new AtomicBoolean();

	public AbstractDatabaseScope(JvmScope parent)
	{
		if (parent == null)
			throw new NullPointerException("parent may not be null");
		this.parent = parent;
		parent.getScheduler().scheduleWithFixedDelay(new CheckDatabase(), 5, 60, TimeUnit.SECONDS);
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
	public Connection getConnection()
	{
		ensureOpen();
		try
		{
			return getDataSource().getConnection();
		}
		catch (SQLException e)
		{
			throw WrappedCheckedException.wrap(e);
		}
	}

	@Override
	public TransactionScope createTransactionScope()
	{
		if (isClosed())
			throw new IllegalStateException("Scope is closed");
		return new DefaultTransactionScope(this);
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

	/**
	 * Example task that monitors database uptime.
	 */
	private class CheckDatabase implements Runnable
	{
		private final Logger log = LoggerFactory.getLogger(CheckDatabase.class);

		@Override
		public void run()
		{
			try (TransactionScope transaction = createTransactionScope())
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
}