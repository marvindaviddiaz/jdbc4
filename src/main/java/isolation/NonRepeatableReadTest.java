package isolation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connection.DriverManagerTest;

/**
 * TRANSACTION_REPEATABLE_READ = In the example, 1er: select record, 2nd: update
 * record, 3rd: select record, in different threads. The results are: the 2nd
 * transaction waited until the repeatable transactions finish.
 */
public class NonRepeatableReadTest {

	protected static Connection connection1 = null;
	protected static Connection connection2 = null;

	static int PRODUCT_ID = 1;

	protected static ArrayList<String> eventos = new ArrayList<String>();

	public static void main(String[] args) throws SQLException {
		try {
			System.out.println("~~~~~~~~~~~~~~~~~~ NON REPEATABLE_READ");
			execute(Connection.TRANSACTION_READ_COMMITTED);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			exit();
		}

		eventos.clear();

		try {
			System.out.println("~~~~~~~~~~~~~~~~~~ REPEATABLE_READ");
			execute(Connection.TRANSACTION_REPEATABLE_READ);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			exit();
		}
	}

	static void execute(Integer transactionIsolation) throws SQLException, InterruptedException {

		// CONNECTIONS
		connection1 = DriverManagerTest.createConnection();
		connection1.setTransactionIsolation(transactionIsolation);
		connection1.setAutoCommit(false);
		DriverManagerTest.printConnection(connection1);

		connection2 = DriverManagerTest.createConnection();
		DriverManagerTest.printConnection(connection2);

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
				PreparedStatement p = connection1.prepareStatement("select version from product where id = ?");
				p.setInt(1, PRODUCT_ID);
				ResultSet rs = p.executeQuery();
				rs.next();
				eventos.add("CONNECTION-1 Product version is : '" + rs.getInt(1) + "'");
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static class Update extends Thread {

		public void run() {
			try {
				// INCREMENT VERSION TRX 2
				eventos.add("CONNECTION-2 run");
				PreparedStatement p = connection2.prepareStatement("update product set version = (version+1) where id = ?");
				p.setInt(1, PRODUCT_ID);
				p.executeUpdate();
				p.close();
				eventos.add("CONNECTION-2 Increased product version, Commited");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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
