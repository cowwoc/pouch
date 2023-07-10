/*
 * Copyright (c) 2016 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages child scopes.
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
	private final Map<AutoCloseable, Thread> childScopeToCreator = new ConcurrentHashMap<>();
	/**
	 * Counts the number of open scopes.
	 */
	private final Phaser openScopes = new Phaser();
	private final AtomicBoolean shutdownRequested = new AtomicBoolean();
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
	 * Adds a child scope.
	 *
	 * @param child the child scope
	 * @return {@code true} on success; {@code false} if the scope is already associated with another thread
	 * @throws NullPointerException  if {@code child} is null
	 * @throws IllegalStateException if shutdown has been requested
	 */
	public boolean add(AutoCloseable child)
	{
		if (child == null)
			throw new NullPointerException("child may not be null");
		if (shutdownRequested.get())
			throw new IllegalStateException("Shutdown has been requested");
		openScopes.register();
		try
		{
			Thread value = childScopeToCreator.putIfAbsent(child, Thread.currentThread());
			return value == child;
		}
		catch (RuntimeException e)
		{
			openScopes.arriveAndDeregister();
			throw e;
		}
	}

	/**
	 * Removes a child scope.
	 *
	 * @param child the child scope
	 * @return {@code true} on success; {@code false} if the scope was not found
	 * @throws NullPointerException  if {@code child} is null
	 */
	public boolean remove(AutoCloseable child)
	{
		// Avoid checking shutdownRequested because children must be allowed to remove themselves while
		// shutdown is in progress
		if (child == null)
			throw new NullPointerException("child may not be null");
		boolean result = childScopeToCreator.remove(child) != null;
		if (result)
			openScopes.arriveAndDeregister();
		return result;
	}

	/**
	 * Initiates a graceful shutdown of child scopes.
	 *
	 * @param timeout the amount of time to wait for the children to close before invoking {@code close}
	 *                on them
	 * @return {@code true} if all children down gracefully, {@code false} if a shutdown is already in
	 * 	progress or a timeout occurred
	 */
	public boolean shutdown(Duration timeout)
	{
		if (!shutdownRequested.compareAndSet(false, true))
			return false;
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
		return result;
	}
}