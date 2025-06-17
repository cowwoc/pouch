package io.github.cowwoc.pouch.dropwizard.scope;

import io.github.cowwoc.pouch.core.LazyReference;
import io.github.cowwoc.pouch.core.Reference;
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

	/**
	 * Creates a new instance.
	 *
	 * @param jvmScope the JVM configuration
	 * @throws NullPointerException if {@code jvmScope} is null
	 */
	public MainDatabaseScope(JvmScope jvmScope)
	{
		super(jvmScope);
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