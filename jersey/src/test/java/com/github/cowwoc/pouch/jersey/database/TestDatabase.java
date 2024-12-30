package com.github.cowwoc.pouch.jersey.database;

import com.github.cowwoc.pouch.jersey.scope.DatabaseScope;
import com.github.cowwoc.pouch.jersey.scope.DefaultJvmScope;
import com.github.cowwoc.pouch.jersey.scope.JvmScope;
import com.github.cowwoc.pouch.jersey.scope.RunMode;
import com.github.cowwoc.pouch.jersey.scope.TestDatabaseScope;
import com.github.cowwoc.pouch.jersey.scope.TransactionScope;
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