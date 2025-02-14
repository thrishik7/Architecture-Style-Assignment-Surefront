import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public interface MySqlConstants {
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

	static Registry getRegistry() {
		Registry registry = null;
		try {
			registry = LocateRegistry.getRegistry(MySqlConstants.getRemoteHost(), 1099);
		} catch (Exception e) {
			System.out.println("Execption occured on locate registry " + e.getMessage());
		}

		try {
			if (registry == null) {
				registry = LocateRegistry.createRegistry(1099);
			}
		} catch (Exception e) {
			System.out.println("Execption occured on locate registry " + e.getMessage());
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
		final String key = "RMI_REMOTE_PORT";
		final String port = System.getenv(key);

		if (port == null) throw new IllegalStateException(String.format("env var [%s] is not set", key));

		return Integer.valueOf(port);
	}
}