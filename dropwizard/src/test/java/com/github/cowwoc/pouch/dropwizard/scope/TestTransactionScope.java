/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.scope;

/**
 * TransactionScope for the test codebase.
 *
 * @author Gili Tzabari
 */
public final class TestTransactionScope extends AbstractTransactionScope
{
	/**
	 * Creates a new TestTransactionScope.
	 *
	 * @param parent the parent scope
	 * @throws NullPointerException if {@code parent} is null
	 */
	TestTransactionScope(JvmScope parent)
	{
		super(parent);
	}

	@Override
	protected void beforeClose()
	{
	}
}
