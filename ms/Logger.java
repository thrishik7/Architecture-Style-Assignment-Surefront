import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Logger {

    private static final String ACTION_LOG_FILE = "ms_action_log.txt";
    private static final String MS_LOG_FILE = "ms_log.txt";
    private static final ExecutorService logExecutor = Executors.newSingleThreadExecutor();
    /**
     * Appends a single line to ms_log.txt capturing
     * timestamp, username, which microservice, operation, status
     */
    public static void logAction(String user, String serviceName, String operation, String status) {
        logExecutor.execute(() -> {
            try (PrintWriter out = new PrintWriter(new FileWriter(ACTION_LOG_FILE, true))) {
                String timestamp = LocalDateTime.now().toString();
                out.printf("%s | user=%s | service=%s | op=%s | status=%s\n",
                            timestamp, user, serviceName, operation, status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void log(boolean isError, String serviceName, String message) {
        // Decide which file to log to based on whether it's an error
        String logType = isError ? "ERROR" : "INFO";

        logExecutor.execute(() -> {
            try (PrintWriter out = new PrintWriter(new FileWriter(MS_LOG_FILE, true))) {
                String timestamp = LocalDateTime.now().toString();
                out.printf("%s | [%s] service=%s | message=%s\n",
                            timestamp, logType, serviceName, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
