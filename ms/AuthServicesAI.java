import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthServicesAI extends Remote {

    /**
     * Registers a new user with the given username and password.
     * @return "SUCCESS" or an error message
     */
    String createUser(String username, String password) throws RemoteException;

    /**
     * Attempts to log the user in with the given username/password.
     * On success, returns a new session token that the client must keep
     * and pass to other microservice calls. On failure, returns "ERROR:..."
     */
    String loginUser(String username, String password) throws RemoteException;

    /**
     * Logs out the user associated with this token.
     */
    String logoutUser(String sessionToken) throws RemoteException;
}