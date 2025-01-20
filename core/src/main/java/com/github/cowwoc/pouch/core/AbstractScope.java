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
		// This method does not throw an exception if the scope is already closed, enabling the closure of
		// child scopes. When a parent scope is closed with some child scopes still open, closing the child
		// scopes will call parent.removeChild(this). In such cases, even though the parent scope is closed, the
		// child scopes must still be able to remove themselves.
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