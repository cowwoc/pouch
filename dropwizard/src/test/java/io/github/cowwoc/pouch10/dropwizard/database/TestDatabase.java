package io.github.cowwoc.pouch10.dropwizard.database;

import io.github.cowwoc.pouch10.dropwizard.scope.DatabaseScope;
import io.github.cowwoc.pouch10.dropwizard.scope.DefaultJvmScope;
import io.github.cowwoc.pouch10.dropwizard.scope.JvmScope;
import io.github.cowwoc.pouch10.dropwizard.scope.RunMode;
import io.github.cowwoc.pouch10.dropwizard.scope.TestDatabaseScope;
import io.github.cowwoc.pouch10.dropwizard.scope.TransactionScope;
import org.junit.jupiter.api.Test;

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