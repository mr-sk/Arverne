package arverne;

import java.io.*;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import arverne.ArverneLogger;

public class ArverneMain {

    private final static Logger l = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
        try {
            ArverneLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create log file");
        }

        l.setLevel(Level.INFO);
        l.info("Arverne startup");
    }
}

class ArverneLogger {

    public static final String logFilePath = "log.txt";
    static private FileHandler logFile;

    static public void setup() throws IOException {
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.INFO);

        logFile = new FileHandler(logFilePath);
        logFile.setFormatter(new SimpleFormatter());

        logger.addHandler(logFile);
    }
}