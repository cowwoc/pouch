/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.scope;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

/**
 * Integrates Pouch scopes with Jersey's dependency injection {@code ServiceLocator} for the "test"
 * codebase.
 */
public final class TestPouchBinder extends AbstractBinder
{
	/**
	 * Binds a JvmScope.
	 */
	private static class JvmScopeFactory implements Factory<JvmScope>
	{
		@Override
		public JvmScope provide()
		{
			return new DefaultJvmScope(RunMode.DEBUG);
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
		 * Creates a new RequestScopeFactory.
		 *
		 * @param jvmScope the application scope
		 * @throws NullPointerException if any of the arguments are null
		 */
		@Inject
		RequestScopeFactory(JvmScope jvmScope, ServiceLocator serviceLocator)
		{
			if (jvmScope == null)
				throw new NullPointerException("serverScope may not be null");
			if (serviceLocator == null)
				throw new NullPointerException("serviceLocator may not be null");
			DatabaseScope databaseScope = new TestDatabaseScope(jvmScope);
			this.serverScope = new TestServerScope(databaseScope);
			this.serviceLocator = serviceLocator;
		}

		@Override
		public RequestScope provide()
		{
			return serverScope.createRequest(serviceLocator);
		}

		@Override
		public void dispose(RequestScope instance)
		{
			instance.close();
		}
	}

	@Override
	protected void configure()
	{
		bindFactory(JvmScopeFactory.class).to(JvmScope.class).in(Singleton.class);
		bindFactory(RequestScopeFactory.class).to(RequestScope.class).in(RequestScoped.class);
	}
}