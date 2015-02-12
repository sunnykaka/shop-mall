package com.kariqu.common.iptools.ip;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogFactory {
    private static final Logger logger = Logger.getLogger("stdout");

    static {
        logger.setLevel(Level.DEBUG);
    }

    public static void log(String info, Level level, Throwable ex) {
        logger.log(level, info, ex);
    }

    public static Level getLogLevel() {
        return logger.getLevel();
    }
}
