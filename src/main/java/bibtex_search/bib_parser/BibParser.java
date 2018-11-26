package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BibParser {

    private Set<Record> records = new HashSet<>();

    private Map<String, String> stringVars = new HashMap<>();

    /**
     * Wrapper method provided to add testability of actual parsing.
     * @param filePath absolute path to .bib file
     */
    public void parse(String filePath) throws IOException {
        File file = getFile(filePath);
        parseFile(file);
    }

    public Set<Record> getRecords() {
        return this.records;
    }

    /**
     * Adds all records found in a file to `records` set.
     * @param file File object for input .bib file
     * @throws IOException
     */
    public void parseFile(File file) throws IOException {
        String fileContent = null;
        try {
            /* Read all of file contents to file. */
            fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new IOException("Error reading .bib file! " + e.getMessage());
        }

        if (fileContent != null) {
            /* Match all String and Record blocks. */
            Matcher recordMatcher = match(fileContent);

            while (recordMatcher.find()) {
                String category = recordMatcher.group("category");
                String content = recordMatcher.group("content");

                /* Only process actual categories and String variable declarations. */
                if (RecordType.names.contains(category)) {
                    /* Record encountered. */
                    parseRecord(category, content);
                } else if (category.equals("STRING")) {
                    /* String variable declaration encountered. */
                    parseStringVar(content);
                }
            }
        }
    }

    /* --------------------------- PRIVATES ---------------------------- */

    /**
     *
     * @param category String naming the record category
     * @param recordContent String containing contents of the record
     */
    private void parseRecord(String category, String recordContent) {
        RecordParser recordParser = new RecordParser(stringVars);
        // TODO: throw exception if e.g. mandatory fields not provided etc.
        Record result = recordParser.parseRecord(category, recordContent);

        /* Only adds new entries. */
        //records.add(result);
    }

    /**
     *
     * @param varContent String containing variable definition
     */
    private void parseStringVar(String varContent) {
        StringVarParser varParser = new StringVarParser(stringVars);
        Pair stringVar = varParser.parse(varContent);

        /* Old variable value (if present) is replaced. */
        //stringVars.put(stringVar.getName(), stringVar.getValue());
        System.out.println(varContent);
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
