/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

import com.github.cowwoc.pouch.core.ConcurrentChildScopes;
import com.github.cowwoc.pouch.core.ConcurrentLazyFactory;
import com.github.cowwoc.pouch.core.Factory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ApplicationScope common to main and test codebases.
 * <p>
 * Child scopes must invoke {@link ApplicationScopeSpi#onClosed(AutoCloseable)} after they are
 * closed.
 *
 * @author Gili Tzabari
 */
abstract class AbstractApplicationScope implements ApplicationScopeSpi
{
	/**
	 * The maximum amount of time to wait for child scopes to close.
	 */
	private static final Duration SHUTDOWN_TIMEOUT = Duration.ofSeconds(10);
	/**
	 * True if the scope is closed.
	 */
	private final AtomicBoolean closed = new AtomicBoolean();
	protected final ConcurrentChildScopes children = new ConcurrentChildScopes();
	private final Factory<ScheduledExecutorService> schedulerFactory = new ConcurrentLazyFactory<>()
	{
		@Override
		protected ScheduledExecutorService createValue()
		{
			ScheduledThreadPoolExecutor result = new ScheduledThreadPoolExecutor(1,
				new ThreadFactoryBuilder().setDaemon(true).setNameFormat("scheduler-%d").build());
			result.setMaximumPoolSize(1);
			return result;
		}

		@Override
		protected void disposeValue(ScheduledExecutorService scheduler)
		{
			scheduler.shutdown();
			try
			{
				if (!scheduler.awaitTermination(10, TimeUnit.SECONDS))
					log.warn("Scheduler did not shut down cleanly: " + scheduler);
			}
			catch (InterruptedException e)
			{
				log.warn("", e);
			}
		}
	};
	private final Logger log = LoggerFactory.getLogger(AbstractApplicationScope.class);

	/**
	 * Creates a new application scope.
	 */
	protected AbstractApplicationScope()
	{
	}

	@Override
	public ScheduledExecutorService getScheduler()
	{
		return schedulerFactory.getValue();
	}

	@Override
	public void onClosed(AutoCloseable scope) throws NullPointerException
	{
		children.onClosed(scope);
	}

	@Override
	public boolean isClosed()
	{
		return closed.get();
	}

	@Override
	public void close()
	{
		if (!closed.compareAndSet(false, true))
			return;
		children.close(SHUTDOWN_TIMEOUT);
		schedulerFactory.close();
		beforeClose();
	}

	/**
	 * A method that is invoked before closing the scope. Subclasses wishing to extend {@code close()}
	 * should override this method.
	 */
	protected abstract void beforeClose();
}
