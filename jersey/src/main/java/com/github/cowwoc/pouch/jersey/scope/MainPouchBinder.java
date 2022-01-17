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
			return new MainJvmScope();
		}

		@Override
		public void dispose(JvmScope instance)
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
		private final JvmScope jvmScope;

		/**
		 * Creates a new HttpScopeFactory.
		 *
		 * @param jvmScope the application scope
		 * @throws NullPointerException if {@code jvmScope} is null
		 */
		@Inject
		HttpScopeFactory(JvmScope jvmScope, ServiceLocator serviceLocator)
		{
			if (jvmScope == null)
				throw new NullPointerException("jvmScope may not be null");
			if (serviceLocator == null)
				throw new NullPointerException("serviceLocator may not be null");
			this.jvmScope = jvmScope;
			this.serviceLocator = serviceLocator;
		}

		@Override
		public HttpScope provide()
		{
			AbstractJvmScope jvmScope = (AbstractJvmScope) this.jvmScope;
			return jvmScope.createHttpScope(serviceLocator);
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
		bindFactory(JvmScopeFactory.class).to(JvmScope.class).in(Singleton.class);
		bindFactory(HttpScopeFactory.class).to(HttpScope.class).in(RequestScoped.class);
	}
}