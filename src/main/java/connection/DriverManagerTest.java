package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DriverManagerTest {

	private static String URL = "jdbc:mysql://localhost:3306/sandbox?useSSL=false";
	private static String USER = "root";
	private static String PASSWORD = "root";

	public static Connection createConnection(Integer isolationLevel, Boolean autocommit) throws SQLException {
		
		Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		connection.setTransactionIsolation(isolationLevel);
		connection.setAutoCommit(autocommit);

		System.out.print("New Connection: ");
		System.out.print(" AutoCommit: " + connection.getAutoCommit());
		System.out.print(" ReadOnly: " + connection.isReadOnly());
		int ti = connection.getTransactionIsolation();
		System.out.println(" Isolation Level: " + (ti == 0 ? "NONE" : ti == 1 ? "READ_UNCOMMITTED" : ti == 2 ? "READ_COMMITTED" : ti == 4 ? "REPEATABLE_READ" : ti == 8 ? "SERIALIZABLE" : ti));

		return connection;
	}

}
