package com.galactic.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SINGLETON PATTERN
 * Gestionnaire de logs centralisé.
 */
public class Logger {
    private static Logger instance;
    private PrintWriter writer;
    private static final String LOG_FILE = "game_trace.log";
    private static final DateTimeFormatter DNF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Logger() {
        try {
            // true pour mode 'append' (ne pas écraser le fichier à chaque lancement)
            FileWriter fw = new FileWriter(LOG_FILE, false);
            writer = new PrintWriter(fw, true);
            log("INFO", "Logger initialized.");
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String type, String message) {
        if (writer != null) {
            String timestamp = LocalDateTime.now().format(DNF);
            String logEntry = String.format("[%s] [%s] %s", timestamp, type, message);
            writer.println(logEntry);
            System.out.println(logEntry); // Console output as well
        }
    }

    public void close() {
        if (writer != null) {
            log("INFO", "Closing logger.");
            writer.close();
        }
    }
}