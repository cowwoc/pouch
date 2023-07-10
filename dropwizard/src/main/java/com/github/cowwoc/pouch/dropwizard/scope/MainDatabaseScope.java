package com.github.cowwoc.pouch.dropwizard.scope;

import com.github.cowwoc.pouch.core.LazyReference;
import com.github.cowwoc.pouch.core.Reference;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

/**
 * The database scope used by the application.
 */
public final class MainDatabaseScope extends AbstractDatabaseScope
{
	private final Reference<DataSource> dataSource = LazyReference.create(() ->
	{
		JdbcDataSource result = new JdbcDataSource();
		result.setUrl("jdbc:h2:mem:main");
		result.setUser("sa");
		return result;
	});

	public MainDatabaseScope(JvmScope parent)
	{
		super(parent);
	}

	@Override
	public String getMode()
	{
		return parent.getMode();
	}

	@Override
	public DataSource getDataSource()
	{
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