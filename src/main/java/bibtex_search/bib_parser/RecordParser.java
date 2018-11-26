package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Author;
import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordParser {
    // TODO: fill these.
    private static Map<RecordType, Set<String>> mandatoryFields;
    private static Map<RecordType, Set<String>> optionalFields;

    private Map<String, String> stringVars;

    public RecordParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Record parseRecord(String category, String recordContent) throws ParseException {
        Record record = new Record();
        /* Type of the record is known already. */
        // TODO: use constructor
        record.setType(RecordType.valueOf(category));

        String foundKey = parseKey(recordContent);
        String foundAuthor = parseAuthor(recordContent);
        Map<String, String> foundFields = parseFields(recordContent);

        // TODO: check if we have mandatory fields - only then return, otherwise WARNING - throw ParseException.
        System.out.println(foundKey);
        System.out.println(foundAuthor);

        return record;
    }

    /**
     *
     * @param recordContent String with record contents
     * @return String with record's key
     */
    private String parseKey(String recordContent) throws ParseException {
        Pattern keyPattern = Pattern.compile("^(?<key>[^,|\\s]+),");
        Matcher keyMatcher = keyPattern.matcher(recordContent);

        if (keyMatcher.find()) {
            return keyMatcher.group("key");
        } else {
            throw new ParseException("Error parsing record's key!", -1);
        }
    }

    /**
     *
     * @param recordContent String with record contents
     * @return map describing encountered fields
     */
    private Map<String, String> parseFields(String recordContent) throws ParseException {
        Map<String, String> fields = new HashMap<>();
        Pattern fieldPattern = Pattern.compile("\\s*(?<field>[^,|]+),");
        Matcher fieldMatcher = fieldPattern.matcher(recordContent);

        while (fieldMatcher.find()) {
            FieldParser fieldParser = new FieldParser(stringVars);
            /* trim() to get rid of trailing whitespace. */
            Pair result = fieldParser.parse(fieldMatcher.group("field").trim());
            System.out.println(result);
        }

        return fields;
    }

    /**
     *
     * @param recordContent String with record contents
     * @return String with author personal data (TODO: change to Author instance)
     */
    private String parseAuthor(String recordContent) {
        Pattern keyPattern = Pattern.compile("author\\s=\\s\"(?<author>[^\",|]+)\"\\|");
        Matcher keyMatcher = keyPattern.matcher(recordContent);

        if (keyMatcher.find()) {
            return keyMatcher.group("author");
        }

        /* No exception is thrown - there may be no author */
        return null;
    }
}
