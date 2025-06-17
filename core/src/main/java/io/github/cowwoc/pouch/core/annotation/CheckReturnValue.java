package io.github.cowwoc.pouch.core.annotation;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Indicates that the result of a method call should not be ignored.
 */
@Target(METHOD)
public @interface CheckReturnValue
{
}