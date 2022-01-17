/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.scope;

/**
 * TransactionScope for the main codebase.
 */
public final class MainTransactionScope extends AbstractTransactionScope
{
	/**
	 * Creates a new MainTransactionScope.
	 *
	 * @param parent the parent scope
	 * @throws NullPointerException if {@code parent} is null
	 */
	MainTransactionScope(JvmScope parent)
	{
		super(parent);
	}

	@Override
	protected void beforeClose()
	{
	}
}
