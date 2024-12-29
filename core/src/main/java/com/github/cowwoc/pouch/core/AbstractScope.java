package com.github.cowwoc.pouch.core;

/**
 * The default implementation of {@link Scope}.
 */
public abstract class AbstractScope implements Scope
{
	/**
	 * The child scopes.
	 */
	protected final ConcurrentChildScopes children = new ConcurrentChildScopes();

	/**
	 * Creates new scope.
	 */
	protected AbstractScope()
	{
	}

	@Override
	public void addChild(Scope child)
	{
		ensureOpen();
		children.add(child);
	}

	@Override
	public void removeChild(Scope child)
	{
		ensureOpen();
		children.remove(child);
	}

	/**
	 * Ensures that the scope is open.
	 *
	 * @throws IllegalStateException if the scope is closed
	 */
	protected void ensureOpen()
	{
		if (isClosed())
			throw new IllegalStateException("Scope is closed");
	}
}