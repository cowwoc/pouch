package com.github.cowwoc.pouch.core;

/**
 * The lifespan of one or more variables.
 * <p>
 * Child scopes must call {@link #addChild(Scope) parent.addChild(this)} at the end of their constructor and
 * {@link #removeChild(Scope) parent.removeChild(this)} at the end of their {@link #close()} method.
 * <p>
 * Example implementation of the {@code close()} method:
 * <pre>
 * {@code
 * public void close()
 * {
 *   if (!closed.compareAndSet(false, true))
 *     return;
 *   parent.removeChild(this);
 *   children.shutdown(CLOSE_TIMEOUT);
 * }
 * }</pre>
 */
public interface Scope extends AutoCloseable
{
	/**
	 * Adds a child scope.
	 *
	 * @param child the child scope
	 * @throws NullPointerException  if {@code child} is null
	 * @throws IllegalStateException if the scope is closed
	 */
	void addChild(Scope child);

	/**
	 * Removes a child scope.
	 *
	 * @param child the child scope
	 * @throws NullPointerException  if {@code child} is null
	 * @throws IllegalStateException if the scope is closed
	 */
	void removeChild(Scope child);

	/**
	 * Determines if the scope is closed.
	 *
	 * @return {@code true} if the scope is closed
	 */
	boolean isClosed();

	@Override
	void close();
}