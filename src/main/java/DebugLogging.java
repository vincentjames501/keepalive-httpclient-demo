import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.*;

public class DebugLogging {
    // Keep references to loggers to prevent their configuration from being GC'd.
    private static final CopyOnWriteArraySet<Logger> configuredLoggers = new CopyOnWriteArraySet<>();

    public static void enable(Class<?> loggerClass) {
        enable(loggerClass.getName());
    }

    public static void enable(String loggerClass) {
        Logger logger = Logger.getLogger(loggerClass);
        if (configuredLoggers.add(logger)) {
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.FINEST);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            logger.setLevel(Level.FINEST);
        }
    }
}