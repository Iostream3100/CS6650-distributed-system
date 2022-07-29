package util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of logger interface to record and print logs
 */
public class LoggerImpl implements Logger, Serializable {
    List<String> logs;

    /**
     * initialize a logger
     */
    public LoggerImpl() {
        logs = new ArrayList<>();
    }

    @Override
    public void printLog(String log) {
        String logWithTime = "[" + LocalDateTime.now() + "] " + log;
        logs.add(logWithTime);
        System.out.println(logWithTime);
    }


    @Override
    public void clearAllLogs() {
        logs.clear();
    }

    @Override
    public String getAllLogs() {
        return String.join("\n", logs);
    }
}
