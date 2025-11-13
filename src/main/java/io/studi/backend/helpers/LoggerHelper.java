package io.studi.backend.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerHelper {

    public LoggerHelper() {
    }

    private static Logger getLogger(Object obj) {
        return LoggerFactory.getLogger(obj.getClass());
    }

    public static void info(Object obj, String message) {
        getLogger(obj).info(message);
    }

    public static void warn(Object obj, String message) {
        getLogger(obj).warn(message);
    }

    public static void error(Object obj, String message, Throwable throwable) {
        getLogger(obj).error(message, throwable);
    }

    public static void debug(Object obj, String message) {
        getLogger(obj).debug(message);
    }
}
