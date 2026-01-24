/**
 * Jersey support.
 */
module io.github.cowwoc.pouch.jersey.test
{
	requires io.github.cowwoc.pouch.jersey;
	requires io.github.cowwoc.pouch.core;
	requires java.sql;
	requires jakarta.ws.rs;
	requires org.glassfish.hk2.locator;
	requires org.glassfish.jersey.core.common;
	requires org.glassfish.jersey.core.server;
	requires jakarta.inject;
	requires jul.to.slf4j;
	requires org.junit.jupiter.api;
	requires org.glassfish.jersey.tests.framework.core;

	opens io.github.cowwoc.pouch.jersey.test.scope to org.glassfish.hk2.locator;
	opens io.github.cowwoc.pouch.jersey.test.database to org.junit.platform.commons;
	opens io.github.cowwoc.pouch.jersey.test.resource to org.junit.platform.commons;
}