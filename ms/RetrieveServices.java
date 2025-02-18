/******************************************************************************************************************
* File: RetrieveServices.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the concrete implementation of the retrieve micro services. These services run
* in their own process (JVM).
*
* Parameters: None
*
* Internal Methods:
*  String retrieveOrders() - gets and returns all the orders in the orderinfo database
*  String retrieveOrders(String id) - gets and returns the order associated with the order id
*
* External Dependencies: 
*	- rmiregistry must be running to start this server
*	= MySQL
	- orderinfo database 
******************************************************************************************************************/
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.sql.*;

public class RetrieveServices extends UnicastRemoteObject implements RetrieveServicesAI
{ 
    // JDBC and DB credentials
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    public RetrieveServices() throws RemoteException {}

    public static void main(String args[]) 
    { 	
        try { 
            RetrieveServices obj = new RetrieveServices();
            Registry registry = Configuration.createRegistry();
            registry.bind("RetrieveServices", obj);
            Logger.log(false, "RetrieveServices", "running...");
            System.out.println("RetrieveServices is running...");

        } catch (Exception e) {
            Logger.log(true, "RetrieveServices", "binding error: " + e.getMessage());
            System.out.println("RetrieveServices binding error: " + e.getMessage()); 
            e.printStackTrace();
        } 
    }


    // Inplmentation of the abstract classes in RetrieveServicesAI happens here.

    // This method will return all the entries in the orderinfo database

    @Override
    public String retrieveOrders(String sessionToken) throws RemoteException
    {
        // 1) Check authentication
        if (!AuthUtils.isAuthenticated(sessionToken)) {
            Logger.logAction("UNKNOWN", "RetrieveServices", "retrieveOrders()", "AUTH FAIL");
            return "ERROR: Not Authenticated.";
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String returnString = "[";

        try {
            Class.forName(JDBC_CONNECTOR);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            String sql = "SELECT * FROM orders";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("order_id");
                String date = rs.getString("order_date");
                String first = rs.getString("first_name");
                String last = rs.getString("last_name");
                String address = rs.getString("address");
                String phone = rs.getString("phone");

                returnString += "{order_id:" + id 
                              + ", order_date:" + date
                              + ", first_name:" + first
                              + ", last_name:" + last
                              + ", address:" + address
                              + ", phone:" + phone + "}";
            }

            returnString += "]";

            // 2) Log success
            String username = AuthUtils.getUsername(sessionToken);
            Logger.logAction(username, "RetrieveServices", "retrieveOrders()", "SUCCESS");

            return returnString;
        } catch (Exception e) {
            // 3) Log error
            Logger.logAction("UNKNOWN", "RetrieveServices", "retrieveOrders()", "ERROR: " + e.getMessage());
            Logger.log(true, "RetrieveServices", "ERROR: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {
                Logger.log(true, "RetrieveServices", "ERROR: " + e.getMessage());
            }
            try { if (stmt != null) stmt.close(); } catch (Exception e) {
                Logger.log(true, "RetrieveServices", "ERROR: " + e.getMessage());
            }
            try { if (conn != null) conn.close(); } catch (Exception e) {
                Logger.log(true, "RetrieveServices", "ERROR: " + e.getMessage());
            }
        }
    }

    // This method will returns the order in the orderinfo database corresponding to the id
    // provided in the argument.

    @Override
    public String retrieveOrders(String sessionToken, String orderId) throws RemoteException
    {
        // 1) Check authentication
        if (!AuthUtils.isAuthenticated(sessionToken)) {
            Logger.logAction("UNKNOWN", "RetrieveServices", "retrieveOrders("+orderId+")", "AUTH FAIL");
            return "ERROR: Not Authenticated.";
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String returnString = "[";

        try {
            Class.forName(JDBC_CONNECTOR);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            String sql = "SELECT * FROM orders WHERE order_id=" + orderId;
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("order_id");
                String date = rs.getString("order_date");
                String first = rs.getString("first_name");
                String last = rs.getString("last_name");
                String address = rs.getString("address");
                String phone = rs.getString("phone");

                returnString += "{order_id:" + id 
                              + ", order_date:" + date
                              + ", first_name:" + first
                              + ", last_name:" + last
                              + ", address:" + address
                              + ", phone:" + phone + "}";
            }

            returnString += "]";

            // 2) Log success
            String username = AuthUtils.getUsername(sessionToken);
            Logger.logAction(username, "RetrieveServices", "retrieveOrders("+orderId+")", "SUCCESS");
            Logger.log(false, "RetrieveServices", "SUCCESS: " + orderId);
            return returnString;
        } catch (Exception e) {
            // 3) Log error
            Logger.logAction("UNKNOWN", "RetrieveServices", "retrieveOrders("+orderId+")", "ERROR: " + e.getMessage());
            Logger.log(true, "RetrieveServices", "ERROR: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {
               Logger.log(true, "RetrieveServices", e.getMessage());
            }
            try { if (stmt != null) stmt.close(); } catch (Exception e) {
               Logger.log(true, "RetrieveServices", e.getMessage());
            }
            try { if (conn != null) conn.close(); } catch (Exception e) {
               Logger.log(true, "RetrieveServices", e.getMessage());
            }
        }
    }

} // RetrieveServices