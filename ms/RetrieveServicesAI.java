/******************************************************************************************************************
* File: RetrieveServicesAI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the abstract interface for the retrieve micro service, RetrieveServices.
* The implementation of these abstract interfaces can be found in the RetrieveServices.java class.
* The micro services are partitioned as Create, Retrieve, Update, Delete (CRUD) service packages. Each service 
* is its own process (eg. executing in a separate JVM). It would be a good practice to follow this convention
* when adding and modifying services. Note that services can be duplicated and differentiated by IP
* and/or port# they are hosted on. For this assignment, create and retrieve services have been provided and are
* services are hosted on the local host, on the default RMI port (1099). 
*
* Parameters: None
*
* Internal Methods:
*  String retrieveOrders() - gets and returns all the orders in the orderinfo database
*  String retrieveOrders(String id) - gets and returns the order associated with the order id
*
* External Dependencies: None
******************************************************************************************************************/

import java.rmi.*;
		
public interface RetrieveServicesAI extends java.rmi.Remote
{
	/*******************************************************
	* Retrieves all orders from the orderinfo database and 
	* returns them in the form of a string in ordered pairs 
	* format.
	*******************************************************/

	String retrieveOrders() throws RemoteException;

	/*******************************************************
	* Retrieves the order corresponding to the order id in 
	* method argument form the orderinfo database and 
	* returns the order in the form of a string in ordered 
	* pairs format.
	*******************************************************/	

	String retrieveOrders(String id ) throws RemoteException;
}