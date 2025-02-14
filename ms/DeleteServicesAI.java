/******************************************************************************************************************
* File: DeleteServicesAI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*   1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This interface provides the abstract methods for the delete microservices, DeleteServices.
* The implementation of these abstract methods can be found in the DeleteServices.java class.
* The microservices are partitioned as Create, Retrieve, Update, Delete (CRUD) service packages. 
* Each service is its own process (e.g., executing in a separate JVM).
* This interface is used to define the contract for deleting orders from the database.
*
* Parameters: None
*
* Internal Methods:
*  String deleteOrder() - deletes an order from the orderinfo database using the provided order ID.
*
* External Dependencies: None
******************************************************************************************************************/

import java.rmi.*;

public interface DeleteServicesAI extends java.rmi.Remote
{
    /*******************************************************
    * Deletes an order based on the provided order ID.
    * Returns a success or error message.
    *******************************************************/

    String deleteOrder(int orderId) throws RemoteException;
}
