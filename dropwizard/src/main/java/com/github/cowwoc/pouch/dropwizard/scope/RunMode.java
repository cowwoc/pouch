package com.github.cowwoc.pouch.dropwizard.scope;

/**
 * Runtime modes.
 */
public enum RunMode
{
	/**
	 * Optimized for debugging problems (extra runtime checks, logging of the program state, disabled web client
	 * cache).
	 */
	DEBUG,
	/**
	 * Optimized for maximum performance.
	 */
	RELEASE
}