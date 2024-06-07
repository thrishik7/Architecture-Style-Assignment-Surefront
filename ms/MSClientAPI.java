/******************************************************************************************************************
* File: MSClientAPI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides access to the webservices via RMI. Users of this class need not worry about the
* details of RMI (provided the services are running and registered via rmiregistry).  
*
* Parameters: None
*
* Internal Methods:
*  String retrieveOrders() - gets and returns all the orders in the orderinfo database
*  String retrieveOrders(String id) - gets and returns the order associated with the order id
*  String newOrder(String Date, String FirstName, String LastName, String Address, String Phone) - creates a new 
*  order in the orderinfo database
*
*
* External Dependencies: None
******************************************************************************************************************/
import java.util.Properties;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming; 
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MSClientAPI
{
	String response = null;
	Properties registry = null;

	public MSClientAPI() throws IOException {
		  // Loads the registry from 'registry.properties'
		  // This files contains entries like:
		  //    <Service> = <host>:<port>
		  // indicating that a service is registered in 
		  // an RMI registry at host on port
		  registry = new Properties();
		  registry.load(new FileReader("registry.properties"));
	}

	/********************************************************************************
	* Description: Retrieves all the orders in the orderinfo database. Note 
	*              that this method is serviced by the RetrieveServices server 
	*			   process.
	* Parameters: None
	* Returns: String of all the current orders in the orderinfo database
	********************************************************************************/

	public String retrieveOrders() throws Exception
	{
		   // Get the registry entry for RetrieveServices service
		   String entry = registry.getProperty("RetrieveServices");
		   String host = entry.split(":")[0];
		   String port = entry.split(":")[1];
		   // Get the RMI registry
		   Registry reg = LocateRegistry.getRegistry(host, Integer.parseInt(port));
		   RetrieveServicesAI obj = (RetrieveServicesAI )reg.lookup("RetrieveServices");
		   response = obj.retrieveOrders();
		   return response;
	}
	
	/********************************************************************************
	* Description: Retrieves the order based on the id argument provided from the
	*              orderinfo database. Note that this method is serviced by the 
	*			   RetrieveServices server process.
	* Parameters: None
	* Returns: String of all the order corresponding to the order id argument 
	*          in the orderinfo database.
	********************************************************************************/

	public String retrieveOrders(String id) throws Exception
	{
		   // Get the registry entry for RetrieveServices service
		   String entry = registry.getProperty("RetrieveServices");
		   String host = entry.split(":")[0];
		   String port = entry.split(":")[1];
		   // Get the RMI registry
		   Registry reg = LocateRegistry.getRegistry(host, Integer.parseInt(port));
		   RetrieveServicesAI obj = (RetrieveServicesAI )reg.lookup("RetrieveServices");
           response = obj.retrieveOrders(id);
           return(response);	

	}

	/********************************************************************************
	* Description: Creates the new order to the orderinfo database
	* Parameters: None
	* Returns: String that contains the status of the create operatation
	********************************************************************************/

   	public String newOrder(String Date, String FirstName, String LastName, String Address, String Phone) throws Exception
	{
		   // Get the registry entry for CreateServices service
		   String entry = registry.getProperty("CreateServices");
		   String host = entry.split(":")[0];
		   String port = entry.split(":")[1];
		   // Get the RMI registry
		   Registry reg = LocateRegistry.getRegistry(host, Integer.parseInt(port));
           CreateServicesAI obj = (CreateServicesAI) reg.lookup("CreateServices"); 
           response = obj.newOrder(Date, FirstName, LastName, Address, Phone);
           return(response);	
    }

}