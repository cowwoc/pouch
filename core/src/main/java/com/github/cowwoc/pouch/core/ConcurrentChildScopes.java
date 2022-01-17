/*
 * Copyright (c) 2016 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Graceful shutdown of child scopes from a parent scope that requires thread-safety.
 * <p>
 * This class is thread-safe.
 * <p>
 * <b>Preconditions that apply to all methods</b>:
 * <ul>
 * <li>Parent scope is not closed.</li>
 * <li>Child scope implementations of {@code close()} are
 * <a href="http://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html#close--">idempotent</a>.</li>
 * </ul>
 */
public final class ConcurrentChildScopes
{
	/**
	 * A map from a child scope to the thread that created it.
	 */
	@SuppressWarnings("CollectionWithoutInitialCapacity")
	private final ConcurrentMap<AutoCloseable, Thread> childScopeToCreator = new ConcurrentHashMap<>();
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
	public <T extends AutoCloseable> T createChildScope(Supplier<T> supplier)
	{
		if (supplier == null)
			throw new NullPointerException("supplier may not be null");
		openScopes.register();
		try
		{
			T result = supplier.get();
			childScopeToCreator.put(result, Thread.currentThread());
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
	 *
	 * @param scope the scope that was closed
	 * @return true on success; false if the scope was not found or was already closed
	 * @throws NullPointerException if {@code scope} is null
	 */
	public boolean onClosed(AutoCloseable scope)
	{
		boolean result = childScopeToCreator.remove(scope) != null;
		if (result)
			openScopes.arriveAndDeregister();
		return result;
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
			log.warn("Child scopes leaked. Here is a mapping from each leaked scope to the " +
				"thread that created it: {}", childScopeToCreator, e);
			result = false;
		}
		for (AutoCloseable scope : childScopeToCreator.keySet())
		{
			try
			{
				scope.close();
			}
			catch (Exception e)
			{
				log.warn("Failed to close scope: {}", scope, e);
			}
		}
		childScopeToCreator.clear();
		return result;
	}
}