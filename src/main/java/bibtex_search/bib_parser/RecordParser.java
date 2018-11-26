package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Author;
import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordParser {
    private static Map<RecordType, Set<String>> mandatoryFields = new HashMap<RecordType, Set<String>>() {{
        put(RecordType.ARTICLE, new HashSet<String>(){{
            add("author"); // TODO: how to indicate 'editor' alternative?
            add("title");
            add("journal");
            add("year");
        }});
        put(RecordType.BOOK, new HashSet<String>(){{
            add("author");
            add("title");
            add("publisher");
            add("year");
        }});
        put(RecordType.INPROCEEDINGS, new HashSet<String>(){{
            add("author");
            add("title");
            add("booktitle");
            add("year");
        }});
        put(RecordType.CONFERENCE, new HashSet<String>(){{
            add("author");
            add("title");
            add("booktitle");
            add("year");
        }});
        put(RecordType.BOOKLET, new HashSet<String>(){{
            add("title");
        }});
        put(RecordType.INBOOK, new HashSet<String>(){{
            add("author");
            add("title");
            add("chapter");
            add("publisher");
            add("year");
        }});
        put(RecordType.INCOLLECTION, new HashSet<String>(){{
            add("author");
            add("title");
            add("booktitle");
            add("publisher");
            add("year");
        }});
        put(RecordType.MANUAL, new HashSet<String>(){{
            add("title");
        }});
        put(RecordType.MASTERSTHESIS, new HashSet<String>(){{
            add("author");
            add("title");
            add("school");
            add("year");
        }});
        put(RecordType.PHDTHESIS, new HashSet<String>(){{
            add("author");
            add("title");
            add("school");
            add("year");
        }});
        put(RecordType.TECHREPORT, new HashSet<String>(){{
            add("author");
            add("title");
            add("institution");
            add("year");
        }});
        put(RecordType.MISC, new HashSet<>());
        put(RecordType.UNPUBLISHED, new HashSet<String>(){{
            add("author");
            add("title");
            add("note");
        }});
    }};


    // TODO: fill these.
    private static Map<RecordType, Set<String>> optionalFields = new HashMap<RecordType, Set<String>>() {{
        put(RecordType.ARTICLE, new HashSet<String>(){{
            add("volume");
            add("number");
            add("pages");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.BOOK, new HashSet<String>(){{
            add("volume");
            add("series");
            add("address");
            add("edition");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.INPROCEEDINGS, new HashSet<String>(){{
            add("author");
            add("title");
            add("booktitle");
            add("year");
        }});
        put(RecordType.CONFERENCE, new HashSet<String>(){{
            add("author");
            add("title");
            add("booktitle");
            add("year");
        }});
        put(RecordType.BOOKLET, new HashSet<String>(){{
            add("title");
        }});
        put(RecordType.INBOOK, new HashSet<String>(){{
            add("author");
            add("title");
            add("chapter");
            add("publisher");
            add("year");
        }});
        put(RecordType.INCOLLECTION, new HashSet<String>(){{
            add("author");
            add("title");
            add("booktitle");
            add("publisher");
            add("year");
        }});
        put(RecordType.MANUAL, new HashSet<String>(){{
            add("title");
        }});
        put(RecordType.MASTERSTHESIS, new HashSet<String>(){{
            add("author");
            add("title");
            add("school");
            add("year");
        }});
        put(RecordType.PHDTHESIS, new HashSet<String>(){{
            add("author");
            add("title");
            add("school");
            add("year");
        }});
        put(RecordType.TECHREPORT, new HashSet<String>(){{
            add("author");
            add("title");
            add("institution");
            add("year");
        }});
        put(RecordType.MISC, new HashSet<>());
        put(RecordType.UNPUBLISHED, new HashSet<String>(){{
            add("author");
            add("title");
            add("note");
        }});
    }};;

    private Map<String, String> stringVars;

    public RecordParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Record parseRecord(String category, String recordContent) throws ParseException {
        String foundKey = parseKey(recordContent);
        String foundAuthor = parseAuthor(recordContent);
        Map<String, String> foundFields = parseFields(recordContent);
        Author author = new Author("", foundAuthor, "", "");

        // TODO: check if we have mandatory fields - only then return, otherwise WARNING - throw ParseException.
        return new Record(RecordType.valueOf(category), foundKey, author, foundFields);
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
    private Map<String, String> parseFields(String recordContent) {
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
