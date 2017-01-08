package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DriverManagerTest {

	private static String URL = "jdbc:mysql://localhost:3306/sandbox?useSSL=false";
	private static String USER = "root";
	private static String PASSWORD = "root";

	public static Connection createConnection() throws SQLException {
		Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		return connection;
	}

	public static void printConnection(Connection connection) throws SQLException {
		System.out.print("### Conexion creada ###");
		System.out.print("\t Catalog: " + connection.getCatalog());
		System.out.print("\t Schema: " + connection.getSchema());
		System.out.print("\t AutoCommit: " + connection.getAutoCommit());
		System.out.print("\t ReadOnly: " + connection.isReadOnly());
		int ti = connection.getTransactionIsolation();
		System.out.print("\t TransactionIsolation: " + (ti == 0 ? "NONE" : ti == 1 ? "READ_UNCOMMITTED" : ti == 2 ? "READ_COMMITTED" : ti == 2 ? "REPEATABLE_READ" : ti == 8 ? "SERIALIZABLE" : ti));
		int h = connection.getHoldability();
		System.out.print("\t Holdability: " + (h == 1 ? "HOLD_CURSORS_OVER_COMMIT" : ti == 2 ? "CLOSE_CURSORS_AT_COMMIT" : h));
		System.out.println("\t NetworkTimeout: " + connection.getNetworkTimeout());

	}

	public static void main(String[] args) throws SQLException {

		Connection c = createConnection();
		c.close();

	}

}
