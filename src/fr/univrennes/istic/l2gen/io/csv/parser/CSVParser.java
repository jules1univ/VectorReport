package fr.univrennes.istic.l2gen.io.csv.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public final class CSVParser {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 64;

    private final char quoteChar;
    private final boolean hasHeaders;
    private final boolean trimWhitespace;
    private final Character configuredDelimiter;

    public CSVParser(char quoteChar, boolean hasHeaders, boolean trimWhitespace) {
        this.configuredDelimiter = null;
        this.quoteChar = quoteChar;
        this.hasHeaders = hasHeaders;
        this.trimWhitespace = trimWhitespace;
    }

    public CSVParser(char delimiter, char quoteChar, boolean hasHeaders, boolean trimWhitespace) {
        this.configuredDelimiter = delimiter;
        this.quoteChar = quoteChar;
        this.hasHeaders = hasHeaders;
        this.trimWhitespace = trimWhitespace;
    }

    public CSVTable parse(File file) throws CSVParseException {
        try (FileReader reader = new FileReader(file)) {
            return parse(reader);
        } catch (IOException e) {
            throw new CSVParseException("Error reading file: " + file.getAbsolutePath(), e);
        }
    }

    public CSVTable parse(String raw) throws CSVParseException {
        return parse(new StringReader(raw));
    }

    public CSVTable parse(Reader reader) throws CSVParseException {
        List<String[]> data = new ArrayList<>();
        char activeDelimiter = configuredDelimiter != null ? configuredDelimiter : 0;
        int expectedRowSize = -1;

        try (BufferedReader bufferedReader = new BufferedReader(reader, DEFAULT_BUFFER_SIZE)) {
            String line;
            int lineNumber = 0;

            while ((line = bufferedReader.readLine()) != null) {
                lineNumber++;
                if (line.isEmpty()) {
                    continue;
                }

                if (activeDelimiter == 0) {
                    activeDelimiter = detectDelimiter(line);
                }

                try {
                    List<String> fields = parseLine(line, activeDelimiter);

                    if (expectedRowSize == -1) {
                        expectedRowSize = fields.size();
                    } else if (fields.size() != expectedRowSize) {
                        throw new CSVParseException(
                                "Expected " + expectedRowSize + " fields but got " + fields.size(),
                                lineNumber);
                    }

                    data.add(fields.toArray(new String[0]));
                } catch (CSVParseException e) {
                    throw new CSVParseException(e.getMessage(), lineNumber);
                }
            }
        } catch (IOException e) {
            throw new CSVParseException("Error reading CSV data", e);
        }

        return new CSVTable(data, hasHeaders);
    }

    private List<String> parseLine(String line, char delimiter) throws CSVParseException {
        List<String> fields = new ArrayList<>();
        char[] buffer = new char[line.length()];
        int bufferLength = 0;
        boolean inQuotes = false;
        boolean fieldStarted = false;

        for (int charIndex = 0; charIndex < line.length(); charIndex++) {
            char currentChar = line.charAt(charIndex);

            if (currentChar == quoteChar) {
                if (inQuotes) {
                    if (charIndex + 1 < line.length() && line.charAt(charIndex + 1) == quoteChar) {
                        buffer[bufferLength++] = quoteChar;
                        charIndex++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    if (fieldStarted && bufferLength > 0) {
                        throw new CSVParseException("Unexpected quote character in field");
                    }
                    inQuotes = true;
                }
                fieldStarted = true;
            } else if (currentChar == delimiter && !inQuotes) {
                fields.add(extractField(buffer, bufferLength));
                bufferLength = 0;
                fieldStarted = false;
            } else {
                buffer[bufferLength++] = currentChar;
                fieldStarted = true;
            }
        }

        if (inQuotes) {
            throw new CSVParseException("Unclosed quote in line");
        }

        fields.add(extractField(buffer, bufferLength));
        return fields;
    }

    private char detectDelimiter(String line) {
        char[] candidates = { ',', ';', '\t', '|', ':' };
        int[] counts = new int[candidates.length];

        boolean inQuotes = false;
        for (int charIndex = 0; charIndex < line.length(); charIndex++) {
            char currentChar = line.charAt(charIndex);

            if (currentChar == quoteChar) {
                inQuotes = !inQuotes;
                continue;
            }
            if (inQuotes) {
                continue;
            }

            for (int candidateIndex = 0; candidateIndex < candidates.length; candidateIndex++) {
                if (currentChar == candidates[candidateIndex]) {
                    counts[candidateIndex]++;
                }
            }
        }

        int bestIndex = 0;
        for (int candidateIndex = 1; candidateIndex < candidates.length; candidateIndex++) {
            if (counts[candidateIndex] > counts[bestIndex]) {
                bestIndex = candidateIndex;
            }
        }

        return counts[bestIndex] > 0 ? candidates[bestIndex] : ',';
    }

    private String extractField(char[] buffer, int length) {
        if (trimWhitespace) {
            int start = 0;
            int end = length;
            while (start < end && buffer[start] <= ' ') {
                start++;
            }
            while (end > start && buffer[end - 1] <= ' ') {
                end--;
            }
            return new String(buffer, start, end - start);
        }
        return new String(buffer, 0, length);
    }
}