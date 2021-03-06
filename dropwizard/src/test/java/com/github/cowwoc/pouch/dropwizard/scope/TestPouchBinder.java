/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

/**
 * Integrates Pouch scopes with Jersey's dependency injection {@code ServiceLocator} for the "test"
 * codebase.
 *
 * @author Gili Tzabari
 */
public final class TestPouchBinder extends AbstractBinder
{
	/**
	 * Binds an ApplicationScope.
	 */
	private static class ApplicationScopeFactory implements Factory<ApplicationScope>
	{
		@Override
		public ApplicationScope provide()
		{
			return new TestApplicationScope();
		}

		@Override
		public void dispose(ApplicationScope instance)
		{
			instance.close();
		}
	}

	/**
	 * Binds an HttpScope.
	 */
	private static class HttpScopeFactory implements Factory<HttpScope>
	{
		private final ServiceLocator serviceLocator;
		private final ApplicationScope applicationScope;

		/**
		 * Creates a new HttpScopeFactory.
		 * <p>
		 * @param applicationScope the application scope
		 * @throws NullPointerException if any of the arguments are null
		 */
		@Inject
		HttpScopeFactory(ApplicationScope applicationScope, ServiceLocator serviceLocator)
		{
			if (applicationScope == null)
				throw new NullPointerException("applicationScope may not be null");
			if (serviceLocator == null)
				throw new NullPointerException("serviceLocator may not be null");
			this.applicationScope = applicationScope;
			this.serviceLocator = serviceLocator;
		}

		@Override
		public HttpScope provide()
		{
			ApplicationScopeSpi spi = (ApplicationScopeSpi) applicationScope;
			return spi.createHttpScope(serviceLocator);
		}

		@Override
		public void dispose(HttpScope instance)
		{
			instance.close();
		}
	}

	@Override
	protected void configure()
	{
		bindFactory(ApplicationScopeFactory.class).to(ApplicationScope.class).in(Singleton.class);
		bindFactory(HttpScopeFactory.class).to(HttpScope.class).in(RequestScoped.class);
	}
}
