package com.sample.jdbc4.chapter1.isolation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sample.jdbc4.chapter1.DriverManagerTest;

/**
 * TRANSACTION_READ_COMMITTED = Dirty-reads CANNOT be done. Non-repeatable reads
 * and phantom reads CAN be done.
 *
 */
public class DirtyReadTest {

	private static Connection connection1 = null;
	private static Connection connection2 = null;

	static int PRODUCT_ID = 1;

	public static void main(String[] args) throws SQLException {
		try {
			/**
			 * DIRTY READ
			 */
			System.out.println("~~~~~~~~~~~~~~~~~~ DIRTY READ = TRANSACTION_READ_UNCOMMITTED");
			execute(Connection.TRANSACTION_READ_UNCOMMITTED);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			exit();
		}

		try {
			/**
			 * NON DIRTY READ
			 */
			System.out.println("~~~~~~~~~~~~~~~~~~ NON DIRTY READ = TRANSACTION_READ_COMMITTED");
			execute(Connection.TRANSACTION_READ_COMMITTED);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			exit();
		}
	}

	static void execute(Integer transactionIsolation) throws SQLException {

		// CONNECTIONS
		connection1 = DriverManagerTest.createConnection();
		connection1.setTransactionIsolation(transactionIsolation);
		DriverManagerTest.printConnection(connection1);
		
		connection2 = DriverManagerTest.createConnection();
		connection2.setAutoCommit(false);
		DriverManagerTest.printConnection(connection2);

		PreparedStatement p = null;
		ResultSet rs = null;

		// SELECT VERSION TRX 1
		p = connection1.prepareStatement("SELECT VERSION FROM SANDBOX.JDBC4_PRODUCTS WHERE ID = ?");
		p.setInt(1, PRODUCT_ID);
		rs = p.executeQuery();
		rs.next();
		System.out.println("CONNECTION-1 Product version is : '" + rs.getInt(1) + "'");
		rs.close();

		// INCREMENT VERSION TRX 2
		p = connection2.prepareStatement("UPDATE SANDBOX.JDBC4_PRODUCTS SET VERSION = (VERSION + 1) WHERE ID = ?");
		p.setInt(1, PRODUCT_ID);
		p.executeUpdate();
		p.close();
		System.out.println("CONNECTION-2 Product version has been increased, Not commited yet!");

		// SELECT VERSION TRX1
		p = connection1.prepareStatement("SELECT VERSION FROM SANDBOX.JDBC4_PRODUCTS WHERE ID = ?");
		p.setInt(1, PRODUCT_ID);
		rs = p.executeQuery();
		rs.next();
		if (Connection.TRANSACTION_READ_UNCOMMITTED == transactionIsolation) {
			System.err.println("CONNECTION-1 Product version is: '" + rs.getInt(1) + "' DIRTY READ!");
		} else {
			System.out.println("CONNECTION-1 Product version is: '" + rs.getInt(1) + "'");
		}
		rs.close();

		// ROLLBACK TRX2
		connection2.rollback();
		System.out.println("CONNECTION-2 Rollback");

	}

	static void exit() {
		try {
			if (connection1 != null && !connection1.isClosed()) {
				connection1.close();
//				System.out.println("CONNECTION-1 closed");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		try {
			if (connection2 != null && !connection2.isClosed()) {
				connection2.close();
//				System.out.println("CONNECTION-2 closed");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

}
