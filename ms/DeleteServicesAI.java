import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DeleteServicesAI extends Remote {

    /**
     * Deletes an order by its ID, if the caller is authenticated.
     * @param sessionToken The token identifying the user’s session
     * @param orderId The ID of the order to delete
     * @return A message indicating success or error
     * @throws RemoteException if RMI fails
     */
    String deleteOrder(String sessionToken, String orderId) throws RemoteException;
}