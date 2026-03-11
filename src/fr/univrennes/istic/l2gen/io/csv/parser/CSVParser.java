package fr.univrennes.istic.l2gen.io.csv.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public final class CSVParser {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 64;

    private Character delimiter = null;
    private final char quoteChar;
    private final boolean hasHeaders;
    private final boolean trimWhitespace;

    public CSVParser(char quoteChar, boolean hasHeaders, boolean trimWhitespace) {
        this.delimiter = null;
        this.quoteChar = quoteChar;
        this.hasHeaders = hasHeaders;
        this.trimWhitespace = trimWhitespace;
    }

    public CSVParser(char delimiter, char quoteChar, boolean hasHeaders, boolean trimWhitespace) {
        this.delimiter = delimiter;
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
        CSVRow[] headerHolder = { null };
        List<CSVRow> rows = new ArrayList<>();

        this.stream(reader, (row) -> {
            if (hasHeaders && headerHolder[0] == null) {
                headerHolder[0] = row;
            } else {
                rows.add(row);
            }
        });

        return new CSVTable(headerHolder[0], rows);
    }

    public void stream(File file, Consumer<CSVRow> rowConsumer) throws CSVParseException {
        try (FileReader reader = new FileReader(file)) {
            stream(reader, rowConsumer);
        } catch (IOException e) {
            throw new CSVParseException("Error reading file: " + file.getAbsolutePath(), e);
        }
    }

    public void stream(Reader reader, Consumer<CSVRow> rowConsumer) throws CSVParseException {
        try (BufferedReader br = new BufferedReader(reader, DEFAULT_BUFFER_SIZE)) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    CSVRow row = parseLine(line);
                    row.trimToSize();
                    rowConsumer.accept(row);
                } catch (CSVParseException e) {
                    throw new CSVParseException(e.getMessage(), lineNumber);
                }
            }
        } catch (IOException e) {
            throw new CSVParseException("Error reading CSV data", e);
        }
    }

    private CSVRow parseLine(String line) throws CSVParseException {
        if (this.delimiter == null) {
            this.detectDelimiter(line);
        }

        CSVRow row = new CSVRow();

        char[] buffer = new char[line.length()];
        int length = 0;

        boolean inQuotes = false;
        boolean fieldStarted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == quoteChar) {
                if (inQuotes) {
                    if (i + 1 < line.length() && line.charAt(i + 1) == quoteChar) {
                        buffer[length++] = quoteChar;
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    if (fieldStarted && length > 0) {
                        throw new CSVParseException("Unexpected quote character in field");
                    }
                    inQuotes = true;
                }
                fieldStarted = true;
            } else if (c == delimiter && !inQuotes) {
                row.addCell(extractField(buffer, length));
                length = 0;
                fieldStarted = false;
            } else {
                buffer[length++] = c;
                fieldStarted = true;
            }
        }

        if (inQuotes) {
            throw new CSVParseException("Unclosed quote in line");
        }

        row.addCell(extractField(buffer, length));
        return row;
    }

    private void detectDelimiter(String line) {
        char[] options = { ',', ';', '\t', '|', ':' };
        int[] counts = new int[options.length];

        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == quoteChar) {
                inQuotes = !inQuotes;
                continue;
            }
            if (inQuotes) {
                continue;
            }

            for (int j = 0; j < options.length; j++) {
                if (c == options[j]) {
                    counts[j]++;
                }
            }

        }

        int bestIndex = 0;
        for (int j = 1; j < options.length; j++) {
            if (counts[j] > counts[bestIndex]) {
                bestIndex = j;

            }
        }

        this.delimiter = counts[bestIndex] > 0 ? options[bestIndex] : delimiter;
    }

    private String extractField(char[] buf, int len) {
        if (len == 0) {
            return null;
        }

        if (trimWhitespace) {
            int start = 0, end = len;
            while (start < end && buf[start] <= ' ') {
                start++;
            }

            while (end > start && buf[end - 1] <= ' ') {
                end--;
            }

            return (end > start) ? new String(buf, start, end - start) : null;
        }
        return new String(buf, 0, len);
    }
}