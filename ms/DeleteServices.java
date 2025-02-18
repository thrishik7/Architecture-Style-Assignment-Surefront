import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.sql.*;

public class DeleteServices extends UnicastRemoteObject implements DeleteServicesAI {

    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();

    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    public DeleteServices() throws RemoteException {}

    public static void main(String args[]) {
        try {
            DeleteServices obj = new DeleteServices();
            Registry registry = Configuration.createRegistry();
            registry.bind("DeleteServices", obj);
            Logger.log(false, "DeleteServices", "running...");
            System.out.println("DeleteServices is running...");

        } catch (Exception e) {
            Logger.log(true, "DeleteServices", "binding error: " + e.getMessage());
            System.out.println("DeleteServices binding error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String deleteOrder(String sessionToken, String orderId) throws RemoteException {
        // 1) Check that the session token is valid
        if (!AuthUtils.isAuthenticated(sessionToken)) {
            logAction("UNKNOWN", "deleteOrder(" + orderId + ")", "AUTH FAIL");
            return "ERROR: Not Authenticated or invalid session.";
        }

        // 2) Attempt to delete from ms_ordersinfo
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName(JDBC_CONNECTOR);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            String sql = "DELETE FROM orders WHERE order_id=" + orderId;
            int rowsAffected = stmt.executeUpdate(sql);

            // 3) Log success/failure
            String username = AuthUtils.getUsername(sessionToken);
            if (rowsAffected > 0) {
                Logger.log(false, "DeleteServices", "deleteOrder(" + orderId + ")");
                logAction(username, "deleteOrder(" + orderId + ")", "SUCCESS");
                return "Deleted order with ID=" + orderId;
            } else {
                logAction(username, "deleteOrder(" + orderId + ")", "NO MATCH");
                return "No order found with ID=" + orderId;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(true, "DeleteServices", "orderId: " + orderId + "ERROR: " + e.getMessage());
            logAction("UNKNOWN", "deleteOrder(" + orderId + ")", "ERROR: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        } finally {
            // Clean up
            try { if (stmt != null) stmt.close(); } catch (Exception e) {
                Logger.log(true, "DeleteServices", e.getMessage());
            }
            try { if (conn != null) conn.close(); } catch (Exception e) {
                Logger.log(true, "DeleteServices", e.getMessage());
            }
        }
    }

    // Simple helper for logging calls
    private void logAction(String user, String operation, String status) {
        Logger.logAction(user, "DeleteServices", operation, status);
    }
}