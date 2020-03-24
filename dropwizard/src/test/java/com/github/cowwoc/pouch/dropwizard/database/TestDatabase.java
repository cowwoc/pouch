package com.github.cowwoc.pouch.dropwizard.database;

import com.github.cowwoc.pouch.dropwizard.scope.ApplicationScope;
import com.github.cowwoc.pouch.dropwizard.scope.TestApplicationScope;
import com.github.cowwoc.pouch.dropwizard.scope.TransactionScope;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Gili Tzabari
 */
public final class TestDatabase
{
	@Test
	public void test1() throws SQLException
	{
		try (ApplicationScope applicationScope = new TestApplicationScope();
		     TransactionScope transaction = applicationScope.createTransactionScope())
		{
			Connection connection = transaction.getConnection();
			System.out.println("test1() running against " + connection.getMetaData().getURL());
		}
	}

	@Test
	public void test2() throws SQLException
	{
		try (ApplicationScope applicationScope = new TestApplicationScope();
		     TransactionScope transaction = applicationScope.createTransactionScope())
		{
			Connection connection = transaction.getConnection();
			System.out.println("test2() running against " + connection.getMetaData().getURL());
		}
	}
}
