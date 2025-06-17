package io.github.cowwoc.pouch.core;

import io.github.cowwoc.pouch.core.annotation.CheckReturnValue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static java.util.Objects.requireNonNull;

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
		super(requireNonNull(message, "message may not be null"),
			requireNonNull(cause, "cause may not be null"));
	}

	/**
	 * Wraps an exception.
	 *
	 * @param cause the exception to wrap
	 * @throws NullPointerException if {@code cause} is null
	 */
	private WrappedCheckedException(Throwable cause)
	{
		super(requireNonNull(cause, "cause may not be null"));
	}

	/**
	 * Wraps any checked exceptions thrown by a {@code Callable}.
	 *
	 * @param task the task to execute
	 * @param <V>  the type of value returned by {@code task}
	 * @return a {@code Callable} that does not throw any checked exceptions
	 * @throws NullPointerException if {@code task} is null
	 */
	@CheckReturnValue
	public static <V> UncheckedCallable<V> wrap(Callable<V> task)
	{
		return () ->
		{
			try
			{
				return task.call();
			}
			catch (Exception e)
			{
				throw WrappedCheckedException.wrap(e);
			}
		};
	}

	/**
	 * Wraps any checked exceptions thrown by a {@code ThrowingRunnable}.
	 *
	 * @param task the task to execute
	 * @return a {@code Runnable}
	 * @throws NullPointerException if {@code task} is null
	 */
	@CheckReturnValue
	public static Runnable wrap(CheckedRunnable task)
	{
		return () ->
		{
			try
			{
				task.run();
			}
			catch (Exception e)
			{
				throw WrappedCheckedException.wrap(e);
			}
		};
	}

	/**
	 * Wraps an exception, unless it is a {@code RuntimeException}.
	 *
	 * @param t the exception to wrap
	 * @return the updated exception
	 * @throws NullPointerException if {@code t} is null
	 */
	@CheckReturnValue
	public static RuntimeException wrap(Throwable t)
	{
		if (t instanceof RuntimeException)
			return (RuntimeException) t;
		if (t instanceof ExecutionException)
			return wrap(t.getCause());
		return new WrappedCheckedException(t);
	}

	/**
	 * Wraps an exception, unless it is a {@code RuntimeException} with the specified {@code message}.
	 *
	 * @param message the detail message of the WrappedCheckedException
	 * @param t       the exception to wrap
	 * @return the updated exception
	 * @throws NullPointerException if {@code t} is null
	 */
	@CheckReturnValue
	public static RuntimeException wrap(String message, Throwable t)
	{
		if (t instanceof RuntimeException && t.getMessage().equals(message))
			return (RuntimeException) t;
		if (t instanceof ExecutionException)
			return wrap(message, t.getCause());
		return new WrappedCheckedException(message, t);
	}

	/**
	 * A {@link Runnable} that throws checked exceptions.
	 */
	@FunctionalInterface
	public interface CheckedRunnable
	{
		/**
		 * Runs the task.
		 *
		 * @throws Exception if unable to compute a result
		 */
		void run() throws Exception;
	}

	/**
	 * A {@link Callable} that does not throw any checked exceptions.
	 *
	 * @param <V> the return type of the {@code call} method
	 */
	@FunctionalInterface
	public interface UncheckedCallable<V>
	{
		/**
		 * Runs the task.
		 *
		 * @return computed result
		 * @throws WrappedCheckedException if unable to compute a result
		 */
		V call();
	}
}