/******************************************************************************************************************
* File: CreateServices.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the concrete implementation of the create micro services. These services run
* in their own process (JVM).
*
* Parameters: None
*
* Internal Methods:
*  String newOrder() - creates an order in the ms_orderinfo database from the supplied parameters.
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

public class CreateServices extends UnicastRemoteObject implements CreateServicesAI
{ 
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    // Do nothing constructor
    public CreateServices() throws RemoteException {}

    // Main service loop
    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry. 

        try 
        { 
            CreateServices obj = new CreateServices();

            Registry registry = Configuration.createRegistry();
            registry.bind("CreateServices", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            Logger.log(false, "CreateServices", "Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
                Logger.log(false, "CreateServices", "\t" + name);
            }
            // Bind this object instance to the name RetrieveServices in the rmiregistry 
            // Naming.rebind("//" + Configuration.getRemoteHost() + ":1099/CreateServices", obj); 

        } catch (Exception e) {

            System.out.println("CreateServices binding err: " + e.getMessage()); 
            Logger.log(true, "CreateServices", "CreateServices binding error: " + e.getMessage());
            e.printStackTrace();
        } 

    } // main


    // Inplmentation of the abstract classes in RetrieveServicesAI happens here.

    // This method add the entry into the ms_orderinfo database

    public String newOrder(String sessionToken, String idate, String ifirst, String ilast,
                       String iaddress, String iphone) throws RemoteException {
    // 1) Check that the user is authenticated
    if (!AuthUtils.isAuthenticated(sessionToken)) {
        logAction("UNKNOWN", "newOrder", "AUTH FAIL");
        return "ERROR: Not Authenticated.";
    }

    Connection conn = null;
    Statement stmt = null;

    try {
        Class.forName(JDBC_CONNECTOR);
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        stmt = conn.createStatement();

        String sql = "INSERT INTO orders(order_date, first_name, last_name, address, phone) "
                   + "VALUES ('"+idate+"','"+ifirst+"','"+ilast+"','"+iaddress+"','"+iphone+"')";
        stmt.executeUpdate(sql);

        String username = AuthUtils.getUsername(sessionToken);
        logAction(username, "newOrder", "SUCCESS");
        Logger.log(false, "CreateServices", "newOrder created: " + ifirst + " " + ilast);
        return "Order Created Successfully";
    } catch(Exception e) {
        logAction("UNKNOWN", "newOrder", "ERROR: " + e.getMessage());
        Logger.log(true, "CreateServices",  "ERROR: " + e.getMessage());
        return "ERROR: " + e.getMessage();
    } finally {
        try { if (stmt != null) stmt.close(); } catch (Exception e){
            Logger.log(true, "CreateServices", e.getMessage());
        }
        try { if (conn != null) conn.close(); } catch (Exception e){
            Logger.log(true, "CreateServices", e.getMessage());
        }
    }
}

private void logAction(String user, String operation, String status) {
    Logger.logAction(user, "CreateServices", operation, status);
}

} // RetrieveServices