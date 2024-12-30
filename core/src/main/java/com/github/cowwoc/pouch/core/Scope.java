package com.github.cowwoc.pouch.core;

/**
 * The lifespan of one or more variables.
 * <p>
 * Child scopes must call {@link #addChild(Scope)} at the end of their constructor and
 * {@link #removeChild(Scope)} at the end of their {@link #close()} method.
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