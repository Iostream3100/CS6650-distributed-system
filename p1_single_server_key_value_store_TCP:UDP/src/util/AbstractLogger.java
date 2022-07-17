package util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class to record and print logs
 */
public abstract class AbstractLogger {
    List<String> logs;

    public AbstractLogger() {
        logs = new ArrayList<>();
    }

    /**
     * print and store log
     * @param log log string
     */
    public void printLog(String log) {
        String logWithTime = "[" + LocalDateTime.now() + "] " + log;
        logs.add(logWithTime);
        System.out.println(logWithTime);
    }

    /**
     * clear all logs
     */
    public void clearAllLogs() {
        logs.clear();
    }

    /**
     * get all logs
     * @return all stored logs
     */
    public String getAllLogs() {
        return String.join("\n", logs);
    }
}
