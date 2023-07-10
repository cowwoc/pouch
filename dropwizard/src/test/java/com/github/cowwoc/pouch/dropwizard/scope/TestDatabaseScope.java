package com.github.cowwoc.pouch.dropwizard.scope;

import com.github.cowwoc.pouch.core.LazyReference;
import com.github.cowwoc.pouch.core.Reference;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The database scope used by tests.
 */
public final class TestDatabaseScope extends AbstractDatabaseScope
{
	private static final AtomicInteger NEXT_DATABASE_ID = new AtomicInteger();
	private final Reference<DataSource> dataSource = LazyReference.create(() ->
	{
		JdbcDataSource result = new JdbcDataSource();
		result.setUrl("jdbc:h2:mem:test" + NEXT_DATABASE_ID.getAndIncrement());
		result.setUser("sa");
		return result;
	});

	/**
	 * Creates a new TestDatabaseScope.
	 *
	 * @param parent the JVM configuration
	 * @throws NullPointerException if any of the arguments are null
	 */
	public TestDatabaseScope(JvmScope parent)
	{
		super(parent);
	}

	@Override
	public DataSource getDataSource()
	{
		ensureOpen();
		return dataSource.getValue();
	}

	@Override
	public void close()
	{
		if (!closed.compareAndSet(false, true))
			return;
		super.close();
	}
}