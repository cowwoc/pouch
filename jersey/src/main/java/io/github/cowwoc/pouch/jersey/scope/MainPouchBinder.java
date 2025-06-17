/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.jersey.scope;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

/**
 * Integrates Pouch scopes with Jersey's dependency injection {@code ServiceLocator} for the "main"
 * codebase.
 */
public final class MainPouchBinder extends AbstractBinder
{
	/**
	 * Binds an JvmScope.
	 */
	private static class JvmScopeFactory implements Factory<JvmScope>
	{
		@Override
		public JvmScope provide()
		{
			return new DefaultJvmScope(RunMode.RELEASE);
		}

		@Override
		public void dispose(JvmScope instance)
		{
			instance.close();
		}
	}

	/**
	 * Creates a new database scope.
	 */
	private static class DatabaseScopeFactory implements Factory<DatabaseScope>
	{
		private final JvmScope jvmScope;

		/**
		 * Creates a new database scope.
		 *
		 * @param jvmScope the application scope
		 * @throws NullPointerException if {@code jvmScope} is null
		 */
		@Inject
		DatabaseScopeFactory(JvmScope jvmScope)
		{
			if (jvmScope == null)
				throw new NullPointerException("jvmScope may not be null");
			this.jvmScope = jvmScope;
		}

		@Override
		public DatabaseScope provide()
		{
			return new MainDatabaseScope(jvmScope);
		}

		@Override
		public void dispose(DatabaseScope instance)
		{
			instance.close();
		}
	}

	/**
	 * Creates a new request scope.
	 */
	private static class RequestScopeFactory implements Factory<RequestScope>
	{
		private final ServiceLocator serviceLocator;
		private final ServerScope serverScope;

		/**
		 * Creates a new request scope.
		 *
		 * @param databaseScope the database scope
		 * @throws NullPointerException if any of the arguments are null
		 */
		@Inject
		RequestScopeFactory(DatabaseScope databaseScope, ServiceLocator serviceLocator)
		{
			if (databaseScope == null)
				throw new NullPointerException("databaseScope may not be null");
			if (serviceLocator == null)
				throw new NullPointerException("serviceLocator may not be null");
			this.serverScope = new MainServerScope(databaseScope);
			this.serviceLocator = serviceLocator;
		}

		@Override
		public RequestScope provide()
		{
			return new MainRequestScope(serverScope, serviceLocator);
		}

		@Override
		public void dispose(RequestScope instance)
		{
			instance.close();
		}
	}

	/**
	 * Creates a new instance.
	 */
	public MainPouchBinder()
	{
	}

	@Override
	protected void configure()
	{
		bindFactory(JvmScopeFactory.class).to(JvmScope.class).in(Singleton.class);
		bindFactory(DatabaseScopeFactory.class).to(DatabaseScope.class).in(Singleton.class);
		bindFactory(RequestScopeFactory.class).to(RequestScope.class).in(RequestScoped.class);
	}
}