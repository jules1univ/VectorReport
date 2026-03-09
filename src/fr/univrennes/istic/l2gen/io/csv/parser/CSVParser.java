package fr.univrennes.istic.l2gen.io.csv.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public final class CSVParser {

    private char delimiter;
    private char quoteChar;
    private boolean hasHeaders;
    private boolean trimWhitespace;

    public CSVParser() {
        this(',', '"', false, true);
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

    public CSVTable parse(String csvData) throws CSVParseException {
        try (StringReader reader = new StringReader(csvData)) {
            return parse(reader);
        }
    }

    public CSVTable parse(Reader reader) throws CSVParseException {
        CSVRow header = null;
        List<CSVRow> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            int lineNumber = 0;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    CSVRow row = parseLine(line);

                    if (firstLine && hasHeaders) {
                        header = row;
                        firstLine = false;
                    } else {
                        rows.add(row);
                        firstLine = false;
                    }
                } catch (CSVParseException e) {
                    throw new CSVParseException(e.getMessage(), lineNumber);
                }
            }

        } catch (IOException e) {
            throw new CSVParseException("Error reading CSV data", e);
        }

        return new CSVTable(header, rows);
    }

    private CSVRow parseLine(String line) throws CSVParseException {
        CSVRow row = new CSVRow();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean fieldStarted = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == quoteChar) {
                if (inQuotes) {
                    if (i + 1 < line.length() && line.charAt(i + 1) == quoteChar) {
                        field.append(quoteChar);
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    if (fieldStarted && field.length() > 0) {
                        throw new CSVParseException("Unexpected quote character in field");
                    }
                    inQuotes = true;
                }
                fieldStarted = true;
            } else if (c == delimiter && !inQuotes) {
                String value = field.toString();
                if (trimWhitespace) {
                    value = value.trim();
                }
                row.addCell(value);
                field.setLength(0);
                fieldStarted = false;
            } else {
                field.append(c);
                fieldStarted = true;
            }
        }

        if (inQuotes) {
            throw new CSVParseException("Unclosed quote in line");
        }

        String value = field.toString();
        if (trimWhitespace) {
            value = value.trim();
        }
        row.addCell(value);

        return row;
    }

}