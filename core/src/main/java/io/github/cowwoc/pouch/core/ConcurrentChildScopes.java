/*
 * Copyright (c) 2016 Gili Tzabari
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.cowwoc.pouch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
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
	 * A map from each child scope to its metadata.
	 */
	private final Map<Scope, Metadata> scopeToMetadata = new ConcurrentHashMap<>();
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
	 * @throws NullPointerException     if {@code child} is null
	 * @throws IllegalArgumentException if {@code child} was already added to this scope
	 * @throws IllegalStateException    if shutdown has been requested
	 */
	public void add(Scope child)
	{
		if (child == null)
			throw new NullPointerException("child may not be null");
		if (shutdownRequested.get())
			throw new IllegalStateException("Shutdown has been requested");
		openScopes.register();
		try
		{
			Thread thread = Thread.currentThread();
			Metadata existingValue = scopeToMetadata.putIfAbsent(child, new Metadata(thread));
			if (existingValue != null)
			{
				StringJoiner stackTrace = new StringJoiner("\n\tat ");
				for (StackTraceElement element : existingValue.stackTrace)
					stackTrace.add(element.toString());

				throw new IllegalStateException("child was already added to this scope by " +
					existingValue.thread.getName() + " at " + stackTrace);
			}
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
	 * @throws NullPointerException if {@code child} is null
	 */
	public boolean remove(Scope child)
	{
		// Avoid checking shutdownRequested because children must be allowed to remove themselves while
		// shutdown is in progress
		if (child == null)
			throw new NullPointerException("child may not be null");
		boolean result = scopeToMetadata.remove(child) != null;
		if (result)
			openScopes.arriveAndDeregister();
		return result;
	}

	/**
	 * Initiates a graceful shutdown of child scopes.
	 *
	 * @param timeout the amount of time to wait for the children to shut down on their own before invoking
	 *                {@code close()} on them
	 * @return {@code true} if all the children shut down gracefully, {@code false} if a shutdown is already in
	 * progress or a timeout occurred
	 * @throws WrappedCheckedException if the thread is interrupted or a child scope threw an exception while
	 *                                 shutting down
	 */
	public boolean shutdown(Duration timeout)
	{
		if (!shutdownRequested.compareAndSet(false, true))
			return false;
		List<Exception> exceptions = new ArrayList<>();
		boolean result;
		try
		{
			openScopes.awaitAdvanceInterruptibly(openScopes.arriveAndDeregister(),
				timeout.toMillis(), TimeUnit.MILLISECONDS);
			result = true;
		}
		catch (InterruptedException e)
		{
			// Interrupted while waiting for child scopes to shut down
			exceptions.add(e);
			result = false;
		}
		catch (TimeoutException unused)
		{
			result = false;
		}
		for (Entry<Scope, Metadata> scopeToMetadata : scopeToMetadata.entrySet())
		{
			try
			{
				Scope scope = scopeToMetadata.getKey();
				Metadata metadata = scopeToMetadata.getValue();
				StringJoiner stackTrace = new StringJoiner("\n\tat ");
				for (StackTraceElement element : metadata.stackTrace)
					stackTrace.add(element.toString());

				log.warn("Thread {} leaked child scope {} created at {}", metadata.thread.getName(), scope,
					stackTrace);
				scope.close();
			}
			catch (Exception e)
			{
				exceptions.add(e);
			}
		}
		if (!exceptions.isEmpty())
		{
			Exception mainException = exceptions.get(0);
			for (int i = 1, size = exceptions.size(); i < size; ++i)
				mainException.addSuppressed(exceptions.get(i));
			throw WrappedCheckedException.wrap(mainException);
		}
		return result;
	}

	/**
	 * Information about who created a scope.
	 */
	private static class Metadata
	{
		public final Thread thread;
		public final StackTraceElement[] stackTrace;

		/**
		 * Creates a new instance.
		 *
		 * @param thread the thread that created the scope
		 * @throws NullPointerException if any of the arguments are null
		 */
		public Metadata(Thread thread)
		{
			if (thread == null)
				throw new NullPointerException("thread may not be null");
			this.thread = thread;
			this.stackTrace = thread.getStackTrace();
		}
	}
}