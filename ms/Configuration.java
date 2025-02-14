import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException; 

public interface Configuration {
	public static final String MYSQL_PASSWORD="<PASSWORD>";

	static String getRemoteHost() {
		final String key = "RMI_REMOTE_HOST";
		final String host = System.getenv(key);

		if (host == null) throw new IllegalStateException(String.format("env var [%s] is not set", key));

		return host;
	}

	static int getRemotePort() {
		final String key = "RMI_REMOTE_PORT";
		final String port = System.getenv(key);

		if (port == null) throw new IllegalStateException(String.format("env var [%s] is not set", key));

		return Integer.valueOf(port);
	}

	static Registry getRegistry() throws RemoteException {
		System.out.println("Getting registry for " + getRemoteHost() + ":" + getRemotePort());
		Registry registry;
		registry = LocateRegistry.getRegistry(getRemoteHost(), getRemotePort());
        return registry;
	}

	static Registry createRegistry() throws RemoteException {
		Registry registry;
		try {
			registry = LocateRegistry.createRegistry(getRemotePort());
		}
		catch (Exception e) {
			registry = LocateRegistry.getRegistry(getRemotePort());
		}
		return registry;
	}

	static String getMySqlHost() {
		final String key = "MYSQL_REMOTE_HOST";
		final String host = System.getenv(key);

		if (host == null) throw new IllegalStateException(String.format("env var [%s] is not set", key));

		return host;
	}

	static int getMySqlPort() {
		final String key = "MYSQL_REMOTE_PORT";
		final String port = System.getenv(key);

		if (port == null) throw new IllegalStateException(String.format("env var [%s] is not set", key));

		return Integer.valueOf(port);
	}

	static String getJDBCConnection() {
		return "jdbc:mysql://" + getMySqlHost() + ":" + getMySqlPort() + "/ms_orderinfo?autoReconnect=true&useSSL=false";
	}
}