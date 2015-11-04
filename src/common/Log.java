package common;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public final class Log {
    private static final String LOG_LOCATION = "log.txt";
    private static final String LOG_NEW_INSTANCE = "---------------------" + "Started new log instance";
    private static PrintWriter cPr;

    private static void init() {
        if (cPr == null) {
            try {
                cPr = new PrintWriter(new FileWriter(LOG_LOCATION, true));
            } catch (IOException e) {
                e.printStackTrace(System.out);
            } finally {
                cPr.println(String.format(LOG_NEW_INSTANCE));
            }
        }
    }

    /**
     * Double prints the line one to console one to file
     */
    private static void print(String s) {
        s = "[" + new Date() + "] " + s;
        init();
        cPr.println(s);
        cPr.flush();
        System.out.println(s);
    }

    public static void log(String s) {
        print(s);
    }

    public static void log(String s, Object className) {
        log(className + ": " + s);
    }
}
