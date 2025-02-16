import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.sql.*;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthServices extends UnicastRemoteObject implements AuthServicesAI {

    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();

    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    public AuthServices() throws RemoteException {}

    public static void main(String[] args) {
        try {
            AuthServices obj = new AuthServices();
            Registry registry = Configuration.createRegistry();
            registry.bind("AuthServices", obj);

            System.out.println("AuthServices is running...");

        } catch (Exception e) {
            System.out.println("AuthServices binding error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String createUser(String username, String password) throws RemoteException {
        Connection conn = null;
        PreparedStatement ps = null;
    
        try {
            Class.forName(JDBC_CONNECTOR);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
    
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
    
            logAction(username, "createUser", "SUCCESS");
            return "SUCCESS";
        } catch (SQLException e) {
            e.printStackTrace();
            logAction(username, "createUser", "FAIL: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            logAction(username, "createUser", "FAIL: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignore){}
            try { if (conn != null) conn.close(); } catch (Exception ignore){}
        }
    }

    @Override
    public String loginUser(String username, String password) throws RemoteException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
    
        try {
            Class.forName(JDBC_CONNECTOR);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
    
            String sql = "SELECT id FROM users WHERE username=? AND password=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
    
            rs = ps.executeQuery();
            if (!rs.next()) {
                logAction(username, "loginUser", "FAIL: Invalid credentials");
                return "ERROR: Invalid credentials";
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore){}
            try { if (ps != null) ps.close(); } catch (Exception ignore){}
            try { if (conn != null) conn.close(); } catch (Exception ignore){}
        }
    }

    @Override
    public String logoutUser(String sessionToken) throws RemoteException {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            Class.forName(JDBC_CONNECTOR);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Optionally remove session
            String sql = "DELETE FROM sessions WHERE session_token=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, sessionToken);
            int deleted = ps.executeUpdate();

            // For logging, figure out whose token it was
            String who = AuthUtils.getUsername(sessionToken);
            logAction(who, "logoutUser", (deleted > 0) ? "SUCCESS" : "NO SESSION");

            return (deleted > 0)? "SUCCESS" : "No such session";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignore){}
            try { if (conn != null) conn.close(); } catch (Exception ignore){}
        }
    }

    private void logAction(String username, String operation, String status) {
        Logger.log(username, "AuthServices", operation, status);
    }
}
