import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.sql.*;

public class DeleteServices extends UnicastRemoteObject implements DeleteServicesAI
{
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    // Do nothing constructor
    public DeleteServices() throws RemoteException {}

    // Main service loop
    public static void main(String args[]) 
    {    
        try 
        { 
            // Instantiate the DeleteServices object
            DeleteServices obj = new DeleteServices();

            // Create the RMI registry and bind the DeleteServices object to it
            Registry registry = Configuration.createRegistry();
            registry.bind("DeleteServices", obj);

            // List and print the registered services
            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }

        } catch (Exception e) {
            System.out.println("DeleteServices binding err: " + e.getMessage()); 
            e.printStackTrace();
        } 
    } 

    // This method deletes an order from the ms_orderinfo database using the provided order ID
    public String deleteOrder(int orderId) throws RemoteException
    {
        // Local declarations
        Connection conn = null;                   // Connection to the orderinfo database
        Statement stmt = null;                     // Statement object for executing SQL queries
        String returnString = "Order Deleted";    // Default success message if the deletion is successful
        
        try
        {
            // Load and initialize the JDBC connector
            Class.forName(JDBC_CONNECTOR);

            // Open the connection to the orderinfo database
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create the SQL statement to delete the order with the given order ID
            stmt = conn.createStatement();
            
            // Define the SQL DELETE query
            String sql = "DELETE FROM orders WHERE order_id = " + orderId;

            // Execute the DELETE query
            int rowsAffected = stmt.executeUpdate(sql);

            // Check if the order ID was found and deleted
            if (rowsAffected == 0) {
                // If no rows were affected, it means the order was not found
                returnString = "Order ID " + orderId + " not found.";
            }

            // Clean up resources: close statement and connection
            stmt.close();
            conn.close();

        } catch(Exception e) {
            // In case of an error, return the error message
            returnString = e.toString();
        } 
        
        // Return the success or error message
        return returnString;
    } 
}
