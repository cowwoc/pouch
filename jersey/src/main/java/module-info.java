/**
 * Jersey support.
 */
module io.github.cowwoc.pouch10.jersey
{
	requires com.h2database;
	requires io.github.cowwoc.pouch10.core;
	requires jakarta.inject;
	requires jakarta.ws.rs;
	requires java.sql;
	requires jul.to.slf4j;
	requires org.eclipse.jetty.server;
	requires org.glassfish.hk2.api;
	requires org.slf4j;
	requires org.glassfish.jersey.container.jetty.http;
	requires org.glassfish.jersey.core.server;
	requires org.glassfish.jersey.core.common;

	exports io.github.cowwoc.pouch10.jersey.application;
	exports io.github.cowwoc.pouch10.jersey.resource;
	exports io.github.cowwoc.pouch10.jersey.scope to io.github.cowwoc.pouch10.jersey.test;
}