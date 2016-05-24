/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.bitbucket.cowwoc.pouch;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.bitbucket.cowwoc.preconditions.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Graceful shutdown of child scopes from a parent scope that requires thread-safety.
 * <p>
 * This class is thread-safe.
 * <p>
 * <b>Preconditions that apply to all methods</b>:
 * <ul>
 * <li>Parent scope is not closed.</li>
 * <li>Child scope implementations of {@code close()} are
 * <a href="http://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html#close--">idempotent<a>.</li>
 * <ul>
 *
 * @author Gili Tzabari
 */
public final class ConcurrentChildScopes
{
	/**
	 * @param expected the {@code Thread} expected to invoke
	 *                 {@link ConcurrentChildScopes#onClosed(AutoCloseable)}
	 * @return true if the method was invoked by an acceptable thread
	 */
	private static boolean validThread(Thread expected)
	{
		if (Thread.currentThread().equals(expected))
			return true;
		// Forcibly closed after timeout
		return Arrays.toString(new Exception().getStackTrace()).contains(ConcurrentChildScopes.class.
			getSimpleName() + ".close");
	}
	/**
	 * A map from a child scope to the thread that created it.
	 */
	private final Cache<AutoCloseable, Thread> childScopes = CacheBuilder.newBuilder().weakKeys().
		build();
	/**
	 * Counts the number of open scopes.
	 */
	private final Phaser openScopes = new Phaser();
	private final Logger log = LoggerFactory.getLogger(ConcurrentChildScopes.class);

	/**
	 * Creates a new ConcurrentChildScopes.
	 */
	public ConcurrentChildScopes()
	{
		// Ensures that we are allowed to invoke arriveAndDeregister() even if there are no child scopes
		openScopes.register();
	}

	/**
	 * Creates a new child scope.
	 *
	 * @param <T>      the type of the child scope
	 * @param supplier supplies a child scope
	 * @return a new child scope
	 * @throws NullPointerException if {@code supplier} is null
	 */
	@SuppressWarnings(
		{
			"BroadCatchBlock", "TooBroadCatch"
		})
	public <T extends AutoCloseable> T createChildScope(Supplier<T> supplier)
		throws NullPointerException
	{
		Preconditions.requireThat(supplier, "supplier").isNotNull();
		openScopes.register();
		try
		{
			T result = supplier.get();
			childScopes.put(result, Thread.currentThread());
			return result;
		}
		catch (RuntimeException e)
		{
			openScopes.arriveAndDeregister();
			throw e;
		}
	}

	/**
	 * Notifies the parent scope that a child has closed.
	 * <p>
	 * @param scope the scope that was closed
	 * @throws NullPointerException if {@code scope} is null
	 */
	public void onClosed(AutoCloseable scope) throws NullPointerException
	{
		Thread expected = childScopes.getIfPresent(scope);
		assert (validThread(expected)): "Expecting " + expected + " but got " + Thread.currentThread();
		childScopes.invalidate(scope);
		openScopes.arriveAndDeregister();
	}

	/**
	 * Closes all child scopes.
	 *
	 * @param timeout the maximum amount of time to wait for child scopes to shut down before invoking
	 *                their {@code close()} method
	 * @return {@code true} if child scopes shut down gracefully, {@code false} if a timeout occurred
	 */
	public boolean close(Duration timeout)
	{
		boolean result;
		try
		{
			openScopes.awaitAdvanceInterruptibly(openScopes.arriveAndDeregister(),
				timeout.toMillis(), TimeUnit.MILLISECONDS);
			result = true;
		}
		catch (InterruptedException e)
		{
			log.warn("Interrupted while waiting for child scopes to shut down", e);
			result = false;
		}
		catch (TimeoutException e)
		{
			log.warn("Child scope leaked by " + childScopes.asMap().values(), e);
			result = false;
		}
		for (AutoCloseable scope: childScopes.asMap().keySet())
		{
			try
			{
				scope.close();
			}
			catch (Exception e)
			{
				log.warn("", e);
			}
		}
		childScopes.invalidateAll();
		return result;
	}
}
