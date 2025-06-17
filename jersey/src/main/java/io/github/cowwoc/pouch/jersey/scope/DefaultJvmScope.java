/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.jersey.scope;

import io.github.cowwoc.pouch.core.AbstractScope;
import io.github.cowwoc.pouch.core.ConcurrentLazyFactory;
import io.github.cowwoc.pouch.core.Factory;
import io.github.cowwoc.pouch.core.Scopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

/**
 * The default implementation of JvmScope.
 * <p>
 * This class ensures that globals, such as slf4j, are initialized in a thread-safe manner.
 */
public final class DefaultJvmScope extends AbstractScope
	implements JvmScope
{
	/**
	 * The maximum amount of time to wait for child scopes to close.
	 */
	private static final Duration CLOSE_TIMEOUT = Duration.ofSeconds(10);
	private final RunMode mode;
	private final Factory<ScheduledExecutorService> schedulerFactory = new ConcurrentLazyFactory<>()
	{
		@Override
		protected ScheduledExecutorService createValue()
		{
			ScheduledThreadPoolExecutor result = new ScheduledThreadPoolExecutor(1,
				new ThreadFactory()
				{
					private final LongAdder counter = new LongAdder();

					@Override
					public Thread newThread(Runnable runnable)
					{
						Thread thread = new Thread(runnable, "scheduler-" + counter);
						thread.setDaemon(true);
						return thread;
					}
				});
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
					log.warn("Scheduler did not shut down cleanly: {}", scheduler);
			}
			catch (InterruptedException e)
			{
				log.warn("", e);
			}
		}
	};
	/**
	 * {@code true} if the scope has been closed.
	 */
	private final AtomicBoolean closed = new AtomicBoolean();
	private final Logger log = LoggerFactory.getLogger(DefaultJvmScope.class);

	/**
	 * Creates a new scope.
	 *
	 * @param mode the runtime mode of the JVM
	 * @throws NullPointerException if {@code mode} is null
	 */
	public DefaultJvmScope(RunMode mode)
	{
		if (mode == null)
			throw new NullPointerException("mode may not be null");
		this.mode = mode;
	}

	@Override
	public RunMode getMode()
	{
		return mode;
	}

	@Override
	public ScheduledExecutorService getScheduler()
	{
		return schedulerFactory.getValue();
	}

	@Override
	public Duration getScopeCloseTimeout()
	{
		return CLOSE_TIMEOUT;
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
		Scopes.runAll(() -> children.shutdown(CLOSE_TIMEOUT), schedulerFactory::close);
	}
}