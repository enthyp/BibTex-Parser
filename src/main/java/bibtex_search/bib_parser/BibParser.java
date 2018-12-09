package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.IRecord;
import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;
import org.apache.commons.cli.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BibParser extends WarningHandler implements IBibParser {

    private Set<IRecord> records = new LinkedHashSet<>();
    private Map<String, String> stringVars = new HashMap<>();

    public Set<IRecord> getRecords() {
        return this.records;
    }

    /**
     * {@inheritDoc}
     * Here it is a wrapper method provided to add testability of actual parsing.
     */
    public void parse(String filePath) throws IOException {
        File file = getFile(filePath);
        parse(file);
    }

    /**
     *
     * @param filePath path to .bib file
     * @return corresponding File object
     */
    private File getFile(String filePath) {
        return new File(filePath);
    }

    /**
     * Adds all records found in a file to `this.records` set.
     * @param file File object for input .bib file
     */
    public void parse(File file) throws IOException {
        String fileContent;
        try {
            /* Read all of the file contents at once. */
            fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new IOException("Error reading the input file! " + e.getMessage());
        }

        /* Set up line beginnings. */
        this.setLineBeginnings(fileContent, 1);

        /* Match all String and Record blocks. */
        Matcher recordMatcher = this.matchEntries(fileContent);

        while (recordMatcher.find()) {
            /* Go over all matched blocks. */
            String category = recordMatcher.group("category").toUpperCase();
            String content = recordMatcher.group("content");
            int recordStart = this.getLineNumber(recordMatcher.start());

            /* Only process actual categories and String variable declarations. */
            if (RecordType.names.contains(category)) {
                /* Record encountered. */
                parseRecord(recordMatcher.group(), recordStart);
            } else if (category.equals("STRING")) {
                /* String variable declaration encountered. */
                parseStringVar(content, recordStart);
            }
        }
    }

    /**
     *
     * @param recordContent record's string representation
     * @param recordStart index of the first character of the record's representation
     */
    private void parseRecord(String recordContent, int recordStart) {
        RecordParser recordParser = new RecordParser(stringVars);
        recordParser.setLineBeginnings(recordContent, recordStart);
        try {
            Record result = recordParser.parseRecord(recordContent);
            /* Only adds new entries. */
            records.add(result);
        } catch (ParseException exc) {
            this.handle(exc);
        }
    }

    /**
     *
     * @param varContent string variable definition
     * @param varStart index of the first character of the variable's definition
     */
    private void parseStringVar(String varContent, int varStart) {
        FieldParser varParser = new FieldParser(stringVars);
        varParser.setLineBeginnings(varContent, varStart);
        try {
            Pair stringVar = varParser.parse(varContent);
            stringVars.put(stringVar.getFirst().toUpperCase(), stringVar.getSecond());
        } catch (ParseException exc) {
            this.handle(exc);
        }
    }

    /**
     * This method assumes no closing braces inside a record (simplification).
     *
     * @param fileContent String with file contents
     * @return Matcher object for all the records and variable definitions (and more)
     */
    private Matcher matchEntries(String fileContent) {
        String regex = "@(?<category>\\w+)\\{(?<content>.+?)}";
        Pattern recordPattern = Pattern.compile(regex, Pattern.DOTALL);

        return recordPattern.matcher(fileContent);
    }
}
