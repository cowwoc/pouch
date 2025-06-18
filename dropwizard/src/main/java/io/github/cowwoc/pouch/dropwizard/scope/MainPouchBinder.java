/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.dropwizard.scope;

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
	 * Binds an JvmScopeScope.
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
	 * Binds a RequestScope.
	 */
	private static class RequestScopeFactory implements Factory<RequestScope>
	{
		private final ServiceLocator serviceLocator;
		private final ServerScope serverScope;

		/**
		 * Creates a new HttpScopeFactory.
		 *
		 * @param jvmScope       the JVM configuration
		 * @param serviceLocator the Jersey dependency-injection mechanism
		 * @throws NullPointerException if any of the arguments are null
		 */
		@Inject
		RequestScopeFactory(JvmScope jvmScope, ServiceLocator serviceLocator)
		{
			if (jvmScope == null)
				throw new NullPointerException("jvmScope may not be null");
			if (serviceLocator == null)
				throw new NullPointerException("serviceLocator may not be null");
			DatabaseScope databaseScope = new MainDatabaseScope(jvmScope);
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
		bindFactory(RequestScopeFactory.class).to(RequestScope.class).in(RequestScoped.class);
	}
}