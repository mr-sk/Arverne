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

        l.info("Executing R Model");
        Integer result = ArverneModel.execute(
                ArverneModel.Models.CDF,
                103.00,
                103.06,
                30.00);
        System.out.println("Model result: " + result);
    }
}

class ArverneModel {

    enum Models {
        CDF // Currently Cumulative Distribution Frequency is the only model supported
    };

    private final static String libPath  = "lib/";
    private final static String rBinary  = "Rscript";
    private final static String cdfR     = "cdf-cl.r";

    static public Integer execute(Models model, Double currentPrice, Double strikePrice, Double cost) {

        String s;

        try {
            String scriptName;
            switch (model) {
                case CDF:
                    scriptName = String.format(ArverneModel.cdfR);
                    break;
                default:
                    throw new RuntimeException("Unknown model type: " + model);
            }

            String cmd = String.format("%s%s %s%s %s %s %s",
                    ArverneModel.libPath,
                    ArverneModel.rBinary,
                    ArverneModel.libPath,
                    scriptName,
                    currentPrice,
                    strikePrice,
                    cost);
            System.out.println(cmd);
            Process proc = Runtime.getRuntime().exec(cmd);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while((s = stdInput.readLine()) != null) {
                break;
            }

        } catch (IOException e) {
           e.printStackTrace();
           throw new RuntimeException("Unable to execute R script");
        }

        return Integer.parseInt(s.substring(4));
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