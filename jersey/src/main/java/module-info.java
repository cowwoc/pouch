/**
 * Jersey support.
 */
module io.github.cowwoc.pouch.jersey
{
	requires com.h2database;
	requires io.github.cowwoc.pouch.core;
	requires jakarta.inject;
	requires jakarta.ws.rs;
	requires java.sql;
	requires jersey.common;
	requires jersey.container.jetty.http;
	requires jersey.server;
	requires jul.to.slf4j;
	requires org.eclipse.jetty.server;
	requires org.glassfish.hk2.api;
	requires org.slf4j;

	exports io.github.cowwoc.pouch.jersey.application;
	exports io.github.cowwoc.pouch.jersey.resource;
}