package com.github.cowwoc.pouch.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * A runtime exception dedicated to wrapping checked exceptions.
 */
public final class WrappedCheckedException extends RuntimeException
{
	private static final long serialVersionUID = 0L;

	/**
	 * Wraps an exception.
	 *
	 * @param message the detail message
	 * @param cause   the exception to wrap
	 * @throws NullPointerException if any of the arguments are null
	 */
	private WrappedCheckedException(String message, Throwable cause)
	{
		super(message, cause);
		if (message == null)
			throw new NullPointerException("message may not be null");
		if (cause == null)
			throw new NullPointerException("cause may not be null");
	}

	/**
	 * Wraps an exception.
	 *
	 * @param cause the exception to wrap
	 * @throws NullPointerException if {@code cause} is null
	 */
	private WrappedCheckedException(Throwable cause)
	{
		super(cause);
		if (cause == null)
			throw new NullPointerException("cause may not be null");
	}

	/**
	 * Wraps any checked exceptions thrown by a callable.
	 *
	 * @param callable the task to execute
	 * @param <V>      the type of value returned by {@code callable}
	 * @return the value returned by {@code callable}
	 * @throws NullPointerException if {@code callable} is null
	 */
	public static <V> V wrap(Callable<V> callable)
	{
		try
		{
			return callable.call();
		}
		catch (Exception e)
		{
			throw WrappedCheckedException.wrap(e);
		}
	}

	/**
	 * Wraps any checked exceptions thrown by a task.
	 *
	 * @param task the task to execute
	 * @throws NullPointerException if {@code task} is null
	 */
	public static void wrap(Task task)
	{
		try
		{
			task.run();
		}
		catch (Exception e)
		{
			throw WrappedCheckedException.wrap(e);
		}
	}

	/**
	 * Wraps an exception, unless it is a {@code RuntimeException}.
	 *
	 * @param t the exception to wrap
	 * @return the updated exception
	 * @throws NullPointerException if {@code t} is null
	 */
	public static RuntimeException wrap(Throwable t)
	{
		if (t instanceof RuntimeException)
			return (RuntimeException) t;
		if (t instanceof ExecutionException)
			return wrap(t.getCause());
		return new WrappedCheckedException(t);
	}

	/**
	 * Wraps an exception, unless it is a {@code RuntimeException}.
	 *
	 * @param message the detail message of the WrappedCheckedException
	 * @param t       the exception to wrap
	 * @return the updated exception
	 * @throws NullPointerException if {@code t} is null
	 */
	public static RuntimeException wrap(String message, Throwable t)
	{
		if (t instanceof RuntimeException)
			return (RuntimeException) t;
		if (t instanceof ExecutionException)
			return wrap(message, t.getCause());
		return new WrappedCheckedException(message, t);
	}

	/**
	 * A {@link Callable} without a return value.
	 */
	@FunctionalInterface
	public interface Task
	{
		/**
		 * Runs the task.
		 *
		 * @throws Exception if unable to compute a result
		 */
		void run() throws Exception;
	}
}