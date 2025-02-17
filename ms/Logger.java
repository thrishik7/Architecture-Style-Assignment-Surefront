import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Logger {

    private static final String LOG_FILE = "ms_log.txt";

    /**
     * Appends a single line to ms_log.txt capturing
     * timestamp, username, which microservice, operation, status
     */
    public static synchronized void log(String user, String serviceName, String operation, String status) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().toString();
            out.printf("%s | user=%s | service=%s | op=%s | status=%s\n",
                        timestamp, user, serviceName, operation, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
