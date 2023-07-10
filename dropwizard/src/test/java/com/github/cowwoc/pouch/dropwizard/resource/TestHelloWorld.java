/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.dropwizard.resource;

import com.github.cowwoc.pouch.dropwizard.application.TestApplication;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.core.Configuration;
import io.dropwizard.testing.DropwizardTestSupport;
import jakarta.ws.rs.client.Client;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class TestHelloWorld
{
	private static final DropwizardTestSupport<Configuration> DROPWIZARD = new DropwizardTestSupport<>(
		TestApplication.class, "target/classes/main.yml");

	@BeforeAll
	public static void beforeClass() throws Exception
	{
		DROPWIZARD.before();
	}

	@AfterAll
	public static void afterClass()
	{
		DROPWIZARD.after();
	}

	@Test
	public void getMessage()
	{
		Client client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("test client");
		String message = client.target(String.format("http://localhost:%d/helloworld",
			DROPWIZARD.getLocalPort())).request().get(String.class);
		System.out.println("response: " + message);
	}
}