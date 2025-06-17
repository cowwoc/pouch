package io.github.cowwoc.pouch.jersey.database;

import io.github.cowwoc.pouch.jersey.scope.DatabaseScope;
import io.github.cowwoc.pouch.jersey.scope.DefaultJvmScope;
import io.github.cowwoc.pouch.jersey.scope.JvmScope;
import io.github.cowwoc.pouch.jersey.scope.RunMode;
import io.github.cowwoc.pouch.jersey.scope.TestDatabaseScope;
import io.github.cowwoc.pouch.jersey.scope.TransactionScope;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public final class TestDatabase
{
	@Test
	public void test1() throws SQLException
	{
		try (JvmScope jvmScope = new DefaultJvmScope(RunMode.DEBUG);
		     DatabaseScope databaseScope = new TestDatabaseScope(jvmScope);
		     TransactionScope transaction = databaseScope.createTransactionScope())
		{
			Connection connection = transaction.getConnection();
			System.out.println("test1() running against " + connection.getMetaData().getURL());
		}
	}

	@Test
	public void test2() throws SQLException
	{
		try (JvmScope jvmScope = new DefaultJvmScope(RunMode.DEBUG);
		     DatabaseScope databaseScope = new TestDatabaseScope(jvmScope);
		     TransactionScope transaction = databaseScope.createTransactionScope())
		{
			Connection connection = transaction.getConnection();
			System.out.println("test2() running against " + connection.getMetaData().getURL());
		}
	}
}