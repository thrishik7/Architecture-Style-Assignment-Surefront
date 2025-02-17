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

	public String authCreateUser(String username, String password) throws Exception {
		String entry = registry.getProperty("AuthServices");
		String host = entry.split(":")[0];
		int port = Integer.parseInt(entry.split(":")[1]);
		Registry reg = LocateRegistry.getRegistry(host, port);
		AuthServicesAI authService = (AuthServicesAI) reg.lookup("AuthServices");
		return authService.createUser(username, password);
	}
	
	public String authLoginUser(String username, String password) throws Exception {
		String entry = registry.getProperty("AuthServices");
		String host = entry.split(":")[0];
		int port = Integer.parseInt(entry.split(":")[1]);
		Registry reg = LocateRegistry.getRegistry(host, port);
		AuthServicesAI authService = (AuthServicesAI) reg.lookup("AuthServices");
		return authService.loginUser(username, password);
	}
	
	public String authLogoutUser(String token) throws Exception {
		String entry = registry.getProperty("AuthServices");
		String host = entry.split(":")[0];
		int port = Integer.parseInt(entry.split(":")[1]);
		Registry reg = LocateRegistry.getRegistry(host, port);
		AuthServicesAI authService = (AuthServicesAI) reg.lookup("AuthServices");
		return authService.logoutUser(token);
	}
	
	// Order creation
	public String newOrder(String token, String date, String first, String last, String address, String phone) throws Exception {
		String entry = registry.getProperty("CreateServices");
		String host = entry.split(":")[0];
		int port = Integer.parseInt(entry.split(":")[1]);
		Registry reg = LocateRegistry.getRegistry(host, port);
		CreateServicesAI createService = (CreateServicesAI) reg.lookup("CreateServices");
		return createService.newOrder(token, date, first, last, address, phone);
	}
	
	// Order retrieval
	public String retrieveOrders(String token) throws Exception {
		String entry = registry.getProperty("RetrieveServices");
		String host = entry.split(":")[0];
		int port = Integer.parseInt(entry.split(":")[1]);
		Registry reg = LocateRegistry.getRegistry(host, port);
		RetrieveServicesAI retrieveService = (RetrieveServicesAI) reg.lookup("RetrieveServices");
		return retrieveService.retrieveOrders(token);
	}
	
	public String retrieveOrders(String token, String orderId) throws Exception {
		String entry = registry.getProperty("RetrieveServices");
		String host = entry.split(":")[0];
		int port = Integer.parseInt(entry.split(":")[1]);
		Registry reg = LocateRegistry.getRegistry(host, port);
		RetrieveServicesAI retrieveService = (RetrieveServicesAI) reg.lookup("RetrieveServices");
		return retrieveService.retrieveOrders(token, orderId);
	}
	
	// Order deletion
	public String deleteOrder(String token, String orderId) throws Exception {
		String entry = registry.getProperty("DeleteServices");
		String host = entry.split(":")[0];
		int port = Integer.parseInt(entry.split(":")[1]);
		Registry reg = LocateRegistry.getRegistry(host, port);
		DeleteServicesAI deleteService = (DeleteServicesAI) reg.lookup("DeleteServices");
		return deleteService.deleteOrder(token, orderId);
	}
}