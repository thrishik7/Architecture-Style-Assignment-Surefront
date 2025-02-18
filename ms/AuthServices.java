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
            Logger.log(true, "AuthServices", "binding error: " + e.getMessage());
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
            Logger.log(false, "AuthServices", "User Createred: " + username);
            logAction(username, "createUser", "SUCCESS");
            return "SUCCESS";
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.log(true, "AuthServices", "AuthServices binding error: " + e.getMessage());
            logAction(username, "createUser", "FAIL: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(true, "AuthServices", "FAIL: " + e.getMessage());
            logAction(username, "createUser", "FAIL: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception e){
                Logger.log(true, "AuthServices", e.getMessage());
            }
            try { if (conn != null) conn.close(); } catch (Exception e){
                Logger.log(true, "AuthServices", e.getMessage());
            }
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

            // 1) Check if the user exists with matching username/password
            String sql = "SELECT id FROM users WHERE username=? AND password=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();
            if (!rs.next()) {
                logAction(username, "loginUser", "FAIL: Invalid credentials");
                return "ERROR: Invalid credentials";
            }

            // 2) If we get here, credentials are valid
            int userId = rs.getInt("id");
            rs.close();
            ps.close();

            // 3) Generate a session token (for example, using UUID)
            String token = java.util.UUID.randomUUID().toString();

            // 4) Insert the session token into the sessions table
            //    The table might have columns: session_token, user_id, valid_until
            String insertSession = "INSERT INTO sessions (session_token, user_id, valid_until) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(insertSession);
            ps.setString(1, token);
            ps.setInt(2, userId);

            // Example: session is valid for 24 hours
            java.time.LocalDateTime validUntil = java.time.LocalDateTime.now().plusHours(24);
            ps.setString(3, validUntil.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            ps.executeUpdate();

            // 5) Log success and return the token
            logAction(username, "loginUser", "SUCCESS");
            return token;

        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(true, "AuthServices", e.getMessage());
            return "ERROR: " + e.getMessage();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e){
                Logger.log(true, "AuthServices", e.getMessage());
            }
            try { if (ps != null) ps.close(); } catch (Exception e){
                Logger.log(true, "AuthServices", e.getMessage());
            }
            try { if (conn != null) conn.close(); } catch (Exception e){
                Logger.log(true, "AuthServices", e.getMessage());
            }
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
            Logger.log(true, "AuthServices", e.getMessage());
            return "ERROR: " + e.getMessage();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception e){
                Logger.log(true, "AuthServices", e.getMessage());
            }
            try { if (conn != null) conn.close(); } catch (Exception e){
                Logger.log(true, "AuthServices", e.getMessage());
            }
        }
    }

    private void logAction(String username, String operation, String status) {
        Logger.log(username, "AuthServices", operation, status);
    }
}
