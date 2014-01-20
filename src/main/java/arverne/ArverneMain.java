package arverne;

import quickfix.*;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.MessageFactory;
import quickfix.field.Password;
import quickfix.fix44.Logon;
import quickfix.fix50.*;

import java.io.*;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


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

        try {
            Application app = new ArverneFIX();
            SessionSettings settings          = new SessionSettings("sessionSettings.txt");
            FileStoreFactory fileStoreFactory = new FileStoreFactory(settings);
            FileLogFactory logFactory         = new FileLogFactory(settings);
            MessageFactory messageFactory     = new DefaultMessageFactory();
            SocketInitiator socketInitiator   = new SocketInitiator(app,
                    fileStoreFactory, settings, logFactory, messageFactory);
            socketInitiator.start();
            SessionID sessionId = socketInitiator.getSessions().get(0);
            ArverneFIX.sendLogonRequest(sessionId);


            int i = 0;
            do {
                try {
                    Thread.sleep(1000);
                    System.out.println(socketInitiator.isLoggedOn());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            } while ((!socketInitiator.isLoggedOn()) && (i < 5));

        } catch (ConfigError e) {
            e.printStackTrace();
            e.getMessage();
            throw new RuntimeException("Unable to load session settings");
        } catch (SessionNotFound e) {
            e.printStackTrace();
            throw new RuntimeException("Session error");
        }

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

    private final static String rPath  = "/usr/bin/";
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

            String cmd = String.format("%s%s lib/%s %s %s %s", // /usr/bin/Rscript lib/cdf.r x y z
                    ArverneModel.rPath,
                    ArverneModel.rBinary,
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


class ArverneFIX extends MessageCracker implements Application {
    @Override
    public void fromAdmin(Message arg0, SessionID arg1) throws FieldNotFound,
            IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        System.out.println("Successfully called fromAdmin for sessionId : "
                + arg0);
    }

    @Override
    public void fromApp(Message arg0, SessionID arg1) throws FieldNotFound,
            IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        System.out.println("Successfully called fromApp for sessionId : "
                + arg0);
    }

    @Override
    public void onCreate(SessionID arg0) {
        System.out.println("Successfully called onCreate for sessionId : "
                + arg0);
    }

    @Override
    public void onLogon(SessionID arg0) {
        System.out.println("Successfully logged on for sessionId : " + arg0);
    }

    @Override
    public void onLogout(SessionID arg0) {
        System.out.println("Successfully logged out for sessionId : " + arg0);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        System.out.println("Inside toAdmin");
        System.out.println("Message: " + message.toString());
        System.out.println("XML: " + message.toXML());
    }

    @Override
    public void toApp(Message arg0, SessionID arg1) throws DoNotSend {
        System.out.println("Message : " + arg0 + " for sessionid : " + arg1);
    }

    public static void sendLogonRequest(SessionID sessionId)
            throws SessionNotFound {

        quickfix.fixt11.Logon logon = new quickfix.fixt11.Logon();
        quickfix.Message.Header header = logon.getHeader();

        header.setField(new quickfix.field.BeginString("FIX.5.0SP2"));
        logon.set(new quickfix.field.HeartBtInt(30));
        logon.set(new quickfix.field.ResetSeqNumFlag(true));
        boolean sent = Session.sendToTarget(logon, sessionId);
        System.out.println("Logon Message Sent : " + sent);
    }
}