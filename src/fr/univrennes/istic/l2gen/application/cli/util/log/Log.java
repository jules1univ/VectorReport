package fr.univrennes.istic.l2gen.application.cli.util.log;

import java.io.IOException;
import java.util.Optional;

import fr.univrennes.istic.l2gen.application.VectorReport;

public final class Log {
    public static void message(String message, Object... args) {
        System.out.println(String.format(message, args));
    }

    public static void error(Exception e, String message, Object... args) {
        System.err.println("ERROR: " + String.format(message, args));
        if (VectorReport.DEBUG_MODE) {
            e.printStackTrace(System.err);
        }
    }

    public static void error(String message, Object... args) {
        System.err.println("ERROR: " + String.format(message, args));
    }

    public static void log(LogLevel level, String message, Object... args) {
        System.out.println(
                String.format("[%s] [%s] %s", level, Thread.currentThread().getName(), String.format(message, args)));
    }

    public static Optional<String> input(String prompt, Object... args) {
        System.out.print(String.format(prompt, args));
        try {
            byte[] inputBytes = new byte[256];
            int bytesRead = System.in.read(inputBytes);

            if (bytesRead == -1) {
                return Optional.empty();
            }

            return Optional.of(new String(inputBytes, 0, bytesRead).trim());
        } catch (IOException e) {
            error("Log failed to read input: %s", e.getMessage());
            return Optional.of("");
        }
    }

    public static boolean confirm(String prompt, Object... args) {
        String response = input(prompt + " (y/n): ", args).orElse("").trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }
}
