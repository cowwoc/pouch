package com.github.cowwoc.pouch.dropwizard.scope;

/**
 * The database scope used by the application.
 */
public final class MainJvmScope extends AbstractJvmScope
{
	@Override
	public String getMode()
	{
		return "main";
	}
}