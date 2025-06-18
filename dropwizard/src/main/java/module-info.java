/**
 * Dropwizard support.
 */
module io.github.cowwoc.pouch.dropwizard
{
	requires java.sql;
	requires java.naming;
	requires org.slf4j;
	requires jakarta.inject;
	requires jakarta.ws.rs;
	requires io.github.cowwoc.pouch.core;
	requires org.glassfish.hk2.api;
	requires com.google.common;
	requires com.h2database;
	requires jersey.common;
	requires io.dropwizard.core;
	requires io.dropwizard.jersey;

	exports io.github.cowwoc.pouch.dropwizard.application;
	exports io.github.cowwoc.pouch.dropwizard.resource;
}