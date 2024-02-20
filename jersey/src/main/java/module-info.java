module com.github.cowwoc.pouch.jersey
{
	requires com.github.cowwoc.pouch.core;
	requires org.slf4j;
	requires jul.to.slf4j;
	requires com.h2database;
	requires jakarta.ws.rs;
	requires java.naming;
	requires jakarta.inject;
	requires java.sql;
	requires org.glassfish.hk2.api;
	requires jersey.common;
	requires jersey.server;
	requires jersey.container.jetty.http;
	requires org.eclipse.jetty.server;
	requires com.google.common;
	requires jakarta.xml.bind;

	opens com.github.cowwoc.pouch.jersey.scope;
	exports com.github.cowwoc.pouch.jersey.resource;
	exports com.github.cowwoc.pouch.jersey.scope;
}