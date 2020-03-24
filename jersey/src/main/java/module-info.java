module com.github.cowwoc.pouch.jersey
{
	requires com.github.cowwoc.pouch.core;
	requires org.slf4j;
	requires jul.to.slf4j;
	requires com.h2database;
	requires java.ws.rs;
	requires java.naming;
	requires jakarta.inject;
	requires java.sql;
	requires hk2.api;
	requires jersey.common;
	requires jersey.server;
	requires jersey.container.servlet.core;
	requires org.eclipse.jetty.server;
	requires org.eclipse.jetty.servlet;
	requires com.google.common;

	opens com.github.cowwoc.pouch.jersey.scope;
	exports com.github.cowwoc.pouch.jersey.resource to jersey.server;
}