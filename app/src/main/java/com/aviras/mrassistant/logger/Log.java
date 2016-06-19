package com.aviras.mrassistant.logger;

import android.text.TextUtils;

import com.aviras.mrassistant.BuildConfig;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Proxy for android.util.Log. Also writes logs to the file using default java.util.logging
 * framework
 * <p/>
 * Created by ashish on 2/2/16.
 */
public class Log {

    private static final Logger logger;

    // Logging to file will be enabled only after this variable is initialized with file path.
    private static String LOG_FILE_NAME;

    private static final int BYTE = 1;
    private static final int KILOBYTE = BYTE * 1024;
    private static final int MEGABYTE = KILOBYTE * 1024;
    // Size in MB
    private static final int FILE_SIZE_LIMIT = 4 * MEGABYTE;

    private static final int FILE_COUNT = 5;

    // When we are building production apk, we set the logging level to WARNING
    private static Level DEFAULT_LOGGING_LEVEL = BuildConfig.DEBUG ? Level.ALL : Level.WARNING;

    private static boolean handlerAdded = false;

    private static FileHandler logFileHandler;

    private Log() {
    }

    static {
        logger = Logger.getLogger("Log");
        addFileHandlerIfNotAdded();
    }

    /**
     * Sets path for log file to which all logs will be written.
     *
     * @param filePath Absolute path of log file.
     */
    @SuppressWarnings("unused")
    public static void setLogFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            throw new RuntimeException("filePath must point to some valid location where caller must have write access.");
        }
        LOG_FILE_NAME = filePath;
        if (null != logFileHandler) {
            logFileHandler.flush();
            logger.removeHandler(logFileHandler);
            logFileHandler.close();
            logFileHandler = null;
        }
        addFileHandlerIfNotAdded();
    }

    /**
     * Changes the logging level to {@link android.util.Log#VERBOSE}
     */
    @SuppressWarnings("unused")
    public static void changeLoggingLevelToVerbose() {
        if (TextUtils.isEmpty(LOG_FILE_NAME)) {
            throw new RuntimeException("This method only covers level for logging done to file. Please set the log file using setLogFile(String) method to use this functionality");
        }
        DEFAULT_LOGGING_LEVEL = Level.ALL;
        setLogFilePath(LOG_FILE_NAME);
    }

    /**
     * Changes the logging level to {@link android.util.Log#WARN}
     */
    @SuppressWarnings("unused")
    public static void changeLoggingLevelToWarning() {
        if (TextUtils.isEmpty(LOG_FILE_NAME)) {
            throw new RuntimeException("This method only covers level for logging done to file. Please set the log file using setLogFile(String) method to use this functionality");
        }
        DEFAULT_LOGGING_LEVEL = Level.WARNING;
        setLogFilePath(LOG_FILE_NAME);
    }

    private static void addFileHandlerIfNotAdded() {
        if (!TextUtils.isEmpty(LOG_FILE_NAME)) {
            try {
                if (null == logFileHandler) {
                    logFileHandler = new FileHandler(LOG_FILE_NAME, FILE_SIZE_LIMIT, FILE_COUNT, true);
                }
                logFileHandler.setLevel(DEFAULT_LOGGING_LEVEL);
                logFileHandler.setFormatter(new Formatter() {
                    Object[] args = new Object[1];

                    @Override
                    public String format(LogRecord r) {
                        StringBuilder sb = new StringBuilder();
                        args[0] = System.currentTimeMillis();
                        sb.append(MessageFormat.format("{0, date} {0, time} ", args));
                        sb.append(r.getLevel().getName()).append(": ");
                        sb.append(formatMessage(r)).append("\n");
                        Throwable t = r.getThrown();
                        if (t != null) {
                            sb.append("Throwable occurred: ");
                            PrintWriter pw = null;
                            try {
                                StringWriter sw = new StringWriter();
                                pw = new PrintWriter(sw);
                                t.printStackTrace(pw);
                                sb.append(sw.toString());
                            } finally {
                                closeQuietly(pw);
                            }
                        }
                        return sb.toString();
                    }
                });
                logger.setUseParentHandlers(false);
                logger.setLevel(DEFAULT_LOGGING_LEVEL);
                logger.addHandler(logFileHandler);
                handlerAdded = true;
            } catch (SecurityException e) {
                android.util.Log.e("Log", "SecurityException while setting FileHandler to logger", e);
            } catch (IOException e) {
                android.util.Log.e("Log", "IOException while setting FileHandler to logger", e);
            } catch (NullPointerException e) {
                android.util.Log.e("Log", "NullPointerException while setting FileHandler to logger", e);
            } catch (IllegalArgumentException e) {
                android.util.Log.e("Log", "IllegalArgumentException while setting FileHandler to logger", e);
            }
        }
    }

    private static void closeQuietly(Closeable pw) {
        if (null != pw) {
            try {
                pw.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Send a {@link android.util.Log#VERBOSE} log message.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param msg           The message you would like logged.
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int v(String tag, String msg, boolean printToLogCat) {
        return println(android.util.Log.VERBOSE, tag, msg, printToLogCat);
    }

    /**
     * Send a {@link android.util.Log#VERBOSE} log message and log the exception.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param msg           The message you would like logged.
     * @param tr            An exception to log
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int v(String tag, String msg, Throwable tr, boolean printToLogCat) {
        return println(android.util.Log.VERBOSE, tag, msg + '\n' + android.util.Log.getStackTraceString(tr), printToLogCat);
    }

    /**
     * Send a {@link android.util.Log#DEBUG} log message.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param msg           The message you would like logged.
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int d(String tag, String msg, boolean printToLogCat) {
        return println(android.util.Log.DEBUG, tag, msg, printToLogCat);
    }

    /**
     * Send a {@link android.util.Log#DEBUG} log message and log the exception.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param msg           The message you would like logged.
     * @param tr            An exception to log
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int d(String tag, String msg, Throwable tr, boolean printToLogCat) {
        return println(android.util.Log.DEBUG, tag, msg + '\n' + android.util.Log.getStackTraceString(tr), printToLogCat);
    }

    /**
     * Send an {@link android.util.Log#INFO} log message.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param msg           The message you would like logged.
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int i(String tag, String msg, boolean printToLogCat) {
        return println(android.util.Log.INFO, tag, msg, printToLogCat);
    }

    /**
     * Send a {@link android.util.Log#INFO} log message and log the exception.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param msg           The message you would like logged.
     * @param tr            An exception to log
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int i(String tag, String msg, Throwable tr, boolean printToLogCat) {
        return println(android.util.Log.INFO, tag, msg + '\n' + android.util.Log.getStackTraceString(tr), printToLogCat);
    }

    /**
     * Send a {@link android.util.Log#WARN} log message.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param msg           The message you would like logged.
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int w(String tag, String msg, boolean printToLogCat) {
        return println(android.util.Log.WARN, tag, msg, printToLogCat);
    }

    /**
     * Send a {@link android.util.Log#WARN} log message and log the exception.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param msg           The message you would like logged.
     * @param tr            An exception to log
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int w(String tag, String msg, Throwable tr, boolean printToLogCat) {
        return println(android.util.Log.WARN, tag, msg + '\n' + android.util.Log.getStackTraceString(tr), printToLogCat);
    }

    /**
     * Send a {@link android.util.Log#WARN} log message and log the exception.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param tr            An exception to log
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int w(String tag, Throwable tr, boolean printToLogCat) {
        return println(android.util.Log.WARN, tag, android.util.Log.getStackTraceString(tr), printToLogCat);
    }

    /**
     * Send an {@link android.util.Log#ERROR} log message.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param msg           The message you would like logged.
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int e(String tag, String msg, boolean printToLogCat) {
        return println(android.util.Log.ERROR, tag, msg, printToLogCat);
    }

    /**
     * Send a {@link android.util.Log#ERROR} log message and log the exception.
     *
     * @param tag           Used to identify the source of a log message.  It usually identifies
     *                      the class or activity where the log call occurs.
     * @param msg           The message you would like logged.
     * @param tr            An exception to log
     * @param printToLogCat Whether to print log to logcat. By default logs will only be printed to
     *                      logcat in debug mode.
     */
    @SuppressWarnings("unused")
    public static int e(String tag, String msg, Throwable tr, boolean printToLogCat) {
        return println(android.util.Log.ERROR, tag, msg + '\n' + android.util.Log.getStackTraceString(tr), printToLogCat);
    }


    /**
     * Send a {@link android.util.Log#VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public static int v(String tag, String msg) {
        return println(android.util.Log.VERBOSE, tag, msg, !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Send a {@link android.util.Log#VERBOSE} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    @SuppressWarnings("unused")
    public static int v(String tag, String msg, Throwable tr) {
        return println(android.util.Log.VERBOSE, tag, msg + '\n' + android.util.Log.getStackTraceString(tr), !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Send a {@link android.util.Log#DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public static int d(String tag, String msg) {
        return println(android.util.Log.DEBUG, tag, msg, !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Send a {@link android.util.Log#DEBUG} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    @SuppressWarnings("unused")
    public static int d(String tag, String msg, Throwable tr) {
        return println(android.util.Log.DEBUG, tag, msg + '\n' + android.util.Log.getStackTraceString(tr), !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Send an {@link android.util.Log#INFO} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public static int i(String tag, String msg) {
        return println(android.util.Log.INFO, tag, msg, !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Send a {@link android.util.Log#INFO} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    @SuppressWarnings("unused")
    public static int i(String tag, String msg, Throwable tr) {
        return println(android.util.Log.INFO, tag, msg + '\n' + android.util.Log.getStackTraceString(tr), !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Send a {@link android.util.Log#WARN} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public static int w(String tag, String msg) {
        return println(android.util.Log.WARN, tag, msg, !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Send a {@link android.util.Log#WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    @SuppressWarnings("unused")
    public static int w(String tag, String msg, Throwable tr) {
        return println(android.util.Log.WARN, tag, msg + '\n' + android.util.Log.getStackTraceString(tr), !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Send a {@link android.util.Log#WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param tr  An exception to log
     */
    @SuppressWarnings("unused")
    public static int w(String tag, Throwable tr) {
        return println(android.util.Log.WARN, tag, android.util.Log.getStackTraceString(tr), !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Send an {@link android.util.Log#ERROR} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public static int e(String tag, String msg) {
        return println(android.util.Log.ERROR, tag, msg, !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Send a {@link android.util.Log#ERROR} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    @SuppressWarnings("unused")
    public static int e(String tag, String msg, Throwable tr) {
        return println(android.util.Log.ERROR, tag, msg + '\n' + android.util.Log.getStackTraceString(tr), !BuildConfig.FLAVOR.contains("production"));
    }

    /**
     * Low-level logging call.
     *
     * @param priority The priority/type of this log message
     * @param tag      Used to identify the source of a log message.  It usually identifies
     *                 the class or activity where the log call occurs.
     * @param msg      The message you would like logged.
     * @return The number of bytes written.
     */
    @SuppressWarnings("unused")
    public static int println(int priority, String tag, String msg, boolean printToLogcat) {
        if (!handlerAdded) {
            addFileHandlerIfNotAdded();
        }
        int retVal = 0;
        if (TextUtils.isEmpty(msg)) {
            return retVal;
        }
        String[] lines = msg.split("\\n");
        for (String line : lines) {
            if (TextUtils.isEmpty(line)) {
                line = " ";
            }
            msg = line;
            switch (priority) {
                case android.util.Log.VERBOSE:
                    if (null != logFileHandler && (logFileHandler.getLevel().intValue() <= Level.ALL.intValue() || android.util.Log.isLoggable(tag.substring(0, Math.min(tag.length(), 23)), priority))) {
                        if (printToLogcat)
                            retVal = android.util.Log.v(tag, msg);
                        logger.log(Level.ALL, tag + ": " + msg);
                    }
                    break;
                case android.util.Log.DEBUG:
                    if (null != logFileHandler && (logFileHandler.getLevel().intValue() <= Level.FINE.intValue() || android.util.Log.isLoggable(tag.substring(0, Math.min(tag.length(), 23)), priority))) {
                        if (printToLogcat)
                            retVal = android.util.Log.d(tag, msg);
                        logger.log(Level.FINE, tag + ": " + msg);
                    }
                    break;
                case android.util.Log.INFO:
                    if (null != logFileHandler && (logFileHandler.getLevel().intValue() <= Level.INFO.intValue() || android.util.Log.isLoggable(tag.substring(0, Math.min(tag.length(), 23)), priority))) {
                        if (printToLogcat)
                            retVal = android.util.Log.i(tag, msg);
                        logger.log(Level.INFO, tag + ": " + msg);
                    }
                    break;
                case android.util.Log.WARN:
                    if (null != logFileHandler && (logFileHandler.getLevel().intValue() <= Level.WARNING.intValue() || android.util.Log.isLoggable(tag.substring(0, Math.min(tag.length(), 23)), priority))) {
                        if (printToLogcat)
                            retVal = android.util.Log.w(tag, msg);
                        logger.log(Level.WARNING, tag + ": " + msg);
                    }
                    break;
                case android.util.Log.ERROR:
                    if (null != logFileHandler && (logFileHandler.getLevel().intValue() <= Level.SEVERE.intValue() || android.util.Log.isLoggable(tag.substring(0, Math.min(tag.length(), 23)), priority))) {
                        if (printToLogcat)
                            retVal = android.util.Log.e(tag, msg);
                        logger.log(Level.SEVERE, tag + ": " + msg);
                    }
                    break;
                case android.util.Log.ASSERT:
                    if (null != logFileHandler && (logFileHandler.getLevel().intValue() <= Level.SEVERE.intValue() || android.util.Log.isLoggable(tag.substring(0, Math.min(tag.length(), 23)), priority))) {
                        if (printToLogcat)
                            retVal = android.util.Log.wtf(tag, msg);
                        logger.log(Level.SEVERE, tag + ": " + msg);
                    }
                    break;
            }
        }
        return retVal;
    }
}
