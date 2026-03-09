package fr.univrennes.istic.l2gen.application.cli.util.parser;

import java.util.ArrayList;
import java.util.List;

public final class CommandParser {

    public static ParsedCommand parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ParsedCommand("", new String[0]);
        }

        input = input.trim();
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"' || c == '\'') {
                inQuotes = !inQuotes;
            } else if (Character.isWhitespace(c) && !inQuotes) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        if (tokens.isEmpty()) {
            return new ParsedCommand("", new String[0]);
        }

        String commandName = tokens.get(0);
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

        return new ParsedCommand(commandName, args);
    }
}
