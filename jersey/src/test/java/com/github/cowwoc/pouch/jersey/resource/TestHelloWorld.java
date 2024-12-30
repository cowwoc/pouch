/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.resource;

import com.github.cowwoc.pouch.jersey.application.TestApplication;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

final class TestHelloWorld extends JerseyTest
{
	@BeforeAll
	public static void beforeClass()
	{
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	@Override
	protected Application configure()
	{
		return new TestApplication();
	}

	@Test
	public void getMessage()
	{
		String message = target("helloworld").request().get(String.class);
		System.out.println("response: " + message);
	}
}
