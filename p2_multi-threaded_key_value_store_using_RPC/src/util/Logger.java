package util;

/**
 * logger to record logs
 */
public interface Logger {
    /**
     * store and print log with timestamp
     * @param log log message
     */
    void printLog(String log);

    /**
     * clear all stored logs
     */
    void clearAllLogs();

    /**
     * get a string of all logs.
     * @return string of logs
     */
    String getAllLogs();
}
