/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.application;

import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.net.URI;

/**
 * The main entry point.
 */
public class Main
{
	public static void main(String[] args) throws Exception
	{
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
		Server server = JettyHttpContainerFactory.createServer(baseUri, new MainApplication());

		try
		{
			server.start();
			ServerConnector connector = (ServerConnector) server.getConnectors()[0];
			int port = connector.getLocalPort();
			System.out.println("Server up at http://localhost:" + port + "/helloworld");
			server.join();
		}
		finally
		{
			server.stop();
		}
	}
}
