package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BibParser extends Parser {

    private Set<Record> records = new HashSet<>();

    private Map<String, String> stringVars = new HashMap<>();

    /**
     * Wrapper method provided to add testability of actual parsing.
     * @param filePath absolute path to .bib file
     */
    public void parse(String filePath) throws IOException {
        File file = getFile(filePath);
        parse(file);
    }

    public Set<Record> getRecords() {
        return this.records;
    }

    /**
     * Adds all records found in a file to `records` set.
     * @param file File object for input .bib file
     * @throws IOException
     */
    public void parse(File file) throws IOException {
        String fileContent;
        try {
            /* Read all of file contents to file. */
            fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new IOException("Error reading .bib file! " + e.getMessage());
        }

        /* Set up line beginnings. */
        this.setLineBeginnings(fileContent, 1);

        /* Match all String and Record blocks. */
        Matcher recordMatcher = match(fileContent);

        /* TODO: handle parsing errors. */
        while (recordMatcher.find()) {
            String category = recordMatcher.group("category");
            String content = recordMatcher.group("content");
            int recordStart = this.getLineNumber(recordMatcher.start());
            int recordEnd = this.getLineNumber(recordMatcher.end() - 1);
            ParseBlock contentBlock = new ParseBlock(recordStart, recordEnd, content);

            /* Only process actual categories and String variable declarations. */
            if (RecordType.names.contains(category)) {
                /* Record encountered. */
                parseRecord(category, contentBlock);
            } else if (category.equals("STRING")) {
                /* String variable declaration encountered. */
                parseStringVar(contentBlock);
            }
        }

        // TODO: pass to Index
        for (Record record : records)
            System.out.println(record);
    }

    /* --------------------------- PRIVATES ---------------------------- */

    /**
     *
     * @param category String naming the record category
     * @param recordBlock contains contents of the record and its position
     */
    private void parseRecord(String category, ParseBlock recordBlock) {
        RecordParser recordParser = new RecordParser(stringVars);
        recordParser.setLineBeginnings(recordBlock.getContent(), recordBlock.getLineStart());
        try {
            Record result = recordParser.parseRecord(category, recordBlock);
            /* Only adds new entries. */
            records.add(result);
        } catch (ParseException e) {
            System.out.println("WARNING: " + e.getMessage());
        }
    }

    /**
     *
     * @param varBlock contains variable definition
     */
    private void parseStringVar(ParseBlock varBlock) {
        FieldParser varParser = new FieldParser(stringVars);
        varParser.setLineBeginnings(varBlock.getContent(), varBlock.getLineStart());
        try {
            Pair stringVar = varParser.parse(varBlock);
            stringVars.put(stringVar.getFirst().toUpperCase(), stringVar.getSecond());
        } catch (ParseException e) {
            System.out.println("WARNING: " + e.getMessage());
        }
    }

    /**
     *
     * @param fileContent String with file contents
     * @return Matcher object for all the records and variable definitions (and more)
     */
    private Matcher match(String fileContent) {
        String regex = "@(?<category>\\w+)\\{(?<content>.+?)}";
        Pattern recordPattern = Pattern.compile(regex, Pattern.DOTALL);

        return recordPattern.matcher(fileContent);
    }

    /**
     *
     * @param filePath path to .bib file
     * @return corresponding File object
     */
    private File getFile(String filePath) {
        return new File(filePath);
    }
}
