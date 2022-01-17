/*
 * Copyright 2016 Gili Tzabari.
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.cowwoc.pouch.jersey.application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static org.glassfish.jersey.servlet.ServletProperties.JAXRS_APPLICATION_CLASS;

/**
 * The main entry point.
 */
public class Main
{
	public static void main(String[] args) throws Exception
	{
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		Server server = new Server(8080);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		ServletContainer servlet = new ServletContainer();
		ServletHolder holder = new ServletHolder(servlet);
		holder.setInitParameter(JAXRS_APPLICATION_CLASS, MainApplication.class.getName());
		context.addServlet(holder, "/*");

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
