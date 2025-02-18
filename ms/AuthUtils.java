import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthUtils {
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    /**
     * Check if the given token is a valid session in the DB
     * and not expired.
     */
    public static boolean isAuthenticated(String token) {
        if (token == null || token.trim().isEmpty()) return false;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName(JDBC_CONNECTOR);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            String sql = "SELECT valid_until FROM sessions WHERE session_token=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            rs = ps.executeQuery();

            if (!rs.next()) {
                return false;
            }     

            Timestamp ts = rs.getTimestamp("valid_until");
            LocalDateTime validUntil = ts.toLocalDateTime();
            return LocalDateTime.now().isBefore(validUntil);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore){}
            try { if (ps != null) ps.close(); } catch (Exception ignore){}
            try { if (conn != null) conn.close(); } catch (Exception ignore){}
        }
    }

    /**
     * Returns the username for the user owning this session token.
     */
    public static String getUsername(String token) {
        if (token == null || token.trim().isEmpty()) return "UNKNOWN";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName(JDBC_CONNECTOR);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            String sql = "SELECT u.username FROM sessions s JOIN users u ON s.user_id = u.user_id "
                       + "WHERE s.session_token = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            rs = ps.executeQuery();

            if (!rs.next()) {
                return "UNKNOWN";
            }
            return rs.getString("username");

        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN";
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore){}
            try { if (ps != null) ps.close(); } catch (Exception ignore){}
            try { if (conn != null) conn.close(); } catch (Exception ignore){}
        }
    }
}
