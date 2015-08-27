package com.sample.jdbc4.chapter1.isolation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.sample.jdbc4.chapter1.DriverManagerTest;

/**
 * TRANSACTION_SERIALIZABLE = In the example, 1er: select with IN, 2nd: update
 * record(this affect the IN), 3rd: select with IN, in different threads. The
 * results are: the 2nd transaction waited until the serialized transactions
 * finish.
 */
public class PhantomReadTest {

	protected static Connection connection1 = null;
	protected static Connection connection2 = null;

	static int PRODUCT_ID = 1;

	protected static ArrayList<String> eventos = new ArrayList<String>();

	public static void main(String[] args) throws SQLException {
		try {
			System.out.println("~~~~~~~~~~~~~~~~~~ READ_COMMITTED");
			execute(Connection.TRANSACTION_READ_COMMITTED);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			exit();
		}

		eventos.clear();

		try {
			System.out.println("~~~~~~~~~~~~~~~~~~ SERIALIZABLE");
			execute(Connection.TRANSACTION_SERIALIZABLE);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			exit();
		}
	}

	static void execute(Integer transactionIsolation) throws SQLException, InterruptedException {

		// CONNECTIONS
		connection1 = DriverManagerTest.createConnection();
		connection1.setAutoCommit(false);
		connection1.setTransactionIsolation(transactionIsolation);
		DriverManagerTest.printConnection(connection1);

		connection2 = DriverManagerTest.createConnection();
		DriverManagerTest.printConnection(connection2);

		// Prepare test set
		connection2.prepareStatement("UPDATE SANDBOX.JDBC4_PRODUCTS SET VERSION = 0").execute();// Autocommit:true

		new Select().start();
		Thread.sleep(1000);
		new Update().start();
		Thread.sleep(1000);
		new Select().start();

		Thread.sleep(3000);
		connection1.commit();
		Thread.sleep(3000);

		for (String e : eventos) {
			System.out.println(e);
		}

	}

	static class Select extends Thread {

		public void run() {
			try {
				// SELECT VERSION TRX 1
				eventos.add("CONNECTION-1 run");
				PreparedStatement p = connection1.prepareStatement("SELECT ID FROM SANDBOX.JDBC4_PRODUCTS WHERE VERSION IN(0,1)");
				ResultSet rs = p.executeQuery();
				int i = 0;
				while (rs.next()) {
					i++;
				}
				eventos.add("CONNECTION-1 Count result: " + i);
				rs.close();
			} catch (Exception e) {
				eventos.add(e.getMessage());
			}
		}
	}

	static class Update extends Thread {

		public void run() {
			try {
				// INCREMENT VERSION TRX 2
				eventos.add("CONNECTION-2 run");
				PreparedStatement p = connection2.prepareStatement("UPDATE SANDBOX.JDBC4_PRODUCTS SET VERSION = 2 WHERE ID = ?");
				p.setInt(1, PRODUCT_ID);
				p.executeUpdate();
				p.close();
				eventos.add("CONNECTION-2 Update, Commited");// Autocommit:true
			} catch (Exception e) {
				eventos.add(e.getMessage());
			}
		}
	}

	static void exit() {
		try {
			if (connection1 != null && !connection1.isClosed()) {
				connection1.close();
				// System.out.println("CONNECTION-1 closed");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		try {
			if (connection2 != null && !connection2.isClosed()) {
				connection2.close();
				// System.out.println("CONNECTION-2 closed");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

}
