/*
 * Copyright 2016 Gili Tzabari. Licensed under the Apache License, Version 2.0:
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.scope;

/**
 * JvmScope for the test codebase.
 */
public final class TestJvmScope extends AbstractJvmScope
{
	public TestJvmScope()
	{
	}

	@Override
	public String getMode()
	{
		return "test";
	}
}