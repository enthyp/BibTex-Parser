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

// TODO: add cross-references handling.
// TODO: add a method to match balanced character blocks.
// (with list of admissible characters as a parameter)
// TODO: then use that method in `this.matchEntries` instead of regex.
// TODO: use interfaces instead of implementations of lower-level parsers.

/**
 * A parser of .bib files.
 * This implementation is capable of parsing .bib files simplified
 * in accordance with specification (not provided here).
 */
public class BibParser extends WarningHandler implements IBibParser {

    /**
     * All entries found in the input file.
     */
    private Set<IRecord> records = new LinkedHashSet<>();

    /**
     * A map between a string variable name and its value.
     */
    private Map<String, String> stringVars = new HashMap<>();

    public Set<IRecord> getRecords() {
        return this.records;
    }

    public void parse(String filePath) throws IOException {
        File file = new File(filePath);
        parse(file);
    }

    /**
     * Adds all records found in a file to the `records` set.
     * @param file File object for input .bib file
     */
    public void parse(File file) throws IOException {
        /* Check if file exists. */
        if (!file.exists())
            throw new FileNotFoundException(String.format("Input file '%s' not found! ", file.getName()));

        /* Read all of the file contents at once. */
        String fileContent;
        try {
            fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                    .collect(Collectors.joining("\n"));
        } catch (IOException exc) {
            throw new IOException("IO exception occured! " + exc.getMessage());
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
     * Parses given record and adds it to `records` set.
     *
     * @param recordContent record's string representation.
     * @param recordStart index of the first character of the record's representation.
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
     * Parses given variable declaration and adds it to `stringVars` map.
     *
     * @param varContent string variable definition.
     * @param varStart index of the first character of the variable's definition.
     */
    private void parseStringVar(String varContent, int varStart) {
        FieldParser varParser = new FieldParser(stringVars);
        varParser.setLineBeginnings(varContent, varStart);
        try {
            Pair stringVar = varParser.parse(varContent);
            stringVars.put(stringVar.getFirst().toLowerCase(), stringVar.getSecond());
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

    /**
     * Runs over all found entries and checks if they have all mandatory fields (including cross-refs).
     *
     */
    private void validate() {

    }
}
