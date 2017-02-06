package isolation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.DriverManagerTest;

public class DirtyReadTest {

	private static final String UPDATE = "update product set price = (price + ?) where id = ?";
	private static final String SELECT = "select price from product where id = ?";
	private static Connection connection1 = null;
	private static Connection connection2 = null;

	static int PRODUCT_ID = 1;

	public static void main(String[] args) throws SQLException {

		// CONNECTIONS
		System.out.print("[connection-1]: ");
		connection1 = DriverManagerTest.createConnection(Connection.TRANSACTION_READ_UNCOMMITTED, false);

		System.out.print("[connection-2]: ");
		connection2 = DriverManagerTest.createConnection(Connection.TRANSACTION_SERIALIZABLE, false);

		PreparedStatement p = null;
		ResultSet rs = null;

		// SELECT VERSION TRX 1
		System.out.println("[connection-1]: " + SELECT);
		p = connection1.prepareStatement(SELECT);
		p.setInt(1, PRODUCT_ID);
		rs = p.executeQuery();
		rs.next();
		System.out.println("[connection-1]: El Precio es : '" + rs.getDouble(1) + "'");
		rs.close();

		// INCREMENT VERSION TRX 2
		System.out.println("[connection-2]: " + UPDATE);
		p = connection2.prepareStatement(UPDATE);
		p.setDouble(1, 2.75);
		p.setInt(2, PRODUCT_ID);
		p.executeUpdate();
		p.close();
		System.out.println("[connection-2]: Precio ha sido incrementado, No se ha hecho commit!");

		// SELECT VERSION TRX1
		System.out.println("[connection-1]: " + SELECT);
		p = connection1.prepareStatement(SELECT);
		p.setInt(1, PRODUCT_ID);
		rs = p.executeQuery();
		rs.next();
		System.err.println("[connection-1]: El precio es: '" + rs.getDouble(1) + "'");
		rs.close();

		// ROLLBACK TRX2
		connection2.rollback();
		System.out.println("[connection-2]: Rollback");

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
