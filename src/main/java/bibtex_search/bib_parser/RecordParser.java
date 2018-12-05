package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Author;
import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
            add("editor");
            add("volume");
            add("series");
            add("pages");
            add("address");
            add("month");
            add("organization");
            add("publisher");
            add("note");
            add("key");
        }});
        put(RecordType.CONFERENCE, new HashSet<String>(){{
            add("editor");
            add("volume");
            add("series");
            add("pages");
            add("address");
            add("month");
            add("organization");
            add("publisher");
            add("note");
            add("key");
        }});
        put(RecordType.BOOKLET, new HashSet<String>(){{
            add("author");
            add("howpublished");
            add("address");
            add("month");
            add("year");
            add("note");
            add("key");
        }});
        put(RecordType.INBOOK, new HashSet<String>(){{
            add("volume");
            add("series");
            add("type");
            add("address");
            add("edition");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.INCOLLECTION, new HashSet<String>(){{
            add("editor");
            add("volume");
            add("series");
            add("type");
            add("chapter");
            add("pages");
            add("address");
            add("edition");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.MANUAL, new HashSet<String>(){{
            add("author");
            add("organization");
            add("address");
            add("edition");
            add("month");
            add("year");
            add("note");
            add("key");
        }});
        put(RecordType.MASTERSTHESIS, new HashSet<String>(){{
            add("type");
            add("address");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.PHDTHESIS, new HashSet<String>(){{
            add("type");
            add("address");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.TECHREPORT, new HashSet<String>(){{
            add("editor");
            add("volume");
            add("series");
            add("address");
            add("month");
            add("organization");
            add("publisher");
            add("note");
            add("key");
        }});
        put(RecordType.MISC, new HashSet<String>(){{
                add("author");
                add("title");
                add("howpublished");
                add("month");
                add("year");
                add("note");
                add("key");
        }});
        put(RecordType.UNPUBLISHED, new HashSet<String>(){{
            add("month");
            add("year");
            add("key");
        }});
    }};

    private Map<String, String> stringVars;

    public RecordParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Record parseRecord(String category, String recordContent) throws ParseException {
        String foundKey = parseKey(recordContent);
        HashSet<Author> foundAuthors = parseAuthor(recordContent);
        Map<String, String> foundFields = parseFields(recordContent);

        /* Check for mandatory and ignored fields. */
        Set<String> mandatory =  new HashSet<>(mandatoryFields.get(RecordType.valueOf(category)));
        Set<String> optional = new HashSet<>(optionalFields.get(RecordType.valueOf(category)));
        HashSet<String> found = new HashSet<>(foundFields.keySet());
        if (foundAuthors != null)
            found.add("author");

        mandatory.removeAll(found);
        if (mandatory.isEmpty()) {
            /* All mandatory fields were found - ignore non-mandatory nor optional. */
            Set<String> admissible = optional;
            admissible.addAll(mandatory);
            found.retainAll(admissible);

            /* Remove non-admissible fields. */
            foundFields.entrySet().removeIf(field -> !found.contains(field.getKey()));
        } else {
            /* Report a faulty record. */
            throw new ParseException("Record lacks mandatory fields: " +
                    mandatory.stream().collect(Collectors.joining(", ")), -1);
        }

        return new Record(RecordType.valueOf(category), foundKey, foundAuthors, foundFields);
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
            throw new ParseException("Error parsing record's key!\nFaulty record:\n" + recordContent + "\n", -1);
        }
    }

    /**
     *
     * @param recordContent String with record contents
     * @return map describing encountered fields
     */
    private Map<String, String> parseFields(String recordContent) {
        Map<String, String> fields = new HashMap<>();
        //Pattern fieldPattern = Pattern.compile("\\s*(?<field>[^,|]+),");
        Pattern fieldPattern = Pattern.compile("\\s*(?<field>[^,|=]+\\s=\\s[^,|]+),");
        Matcher fieldMatcher = fieldPattern.matcher(recordContent);

        while (fieldMatcher.find()) {
            FieldParser fieldParser = new FieldParser(stringVars);
            /* trim() to get rid of trailing whitespace. */
            try {
                Pair result = fieldParser.parse(fieldMatcher.group("field").trim());
                fields.put(result.getName(), result.getValue());
            } catch (ParseException e) {
                System.out.println("WARNING: " + e.getMessage());
            }
        }

        return fields;
    }

    /**
     *
     * @param recordContent String with record contents
     * @return personal data of all found authors
     */
    private HashSet<Author> parseAuthor(String recordContent) {
        Pattern keyPattern = Pattern.compile("author\\s=\\s\"(?<author>[^|]+)\"\\|");
        Matcher keyMatcher = keyPattern.matcher(recordContent);

        if (keyMatcher.find()) {
            AuthorParser authorParser = new AuthorParser();
            try {
                String authorsString = keyMatcher.group("author").trim();
                HashSet<Author> result = new HashSet<>();
                String[] words = authorParser.splitIntoWords(authorsString);

                /* Split on and into multiple authors. */
                for (int i = 0; i < words.length; i++) {
                    int j = i;
                    while (j < words.length && !words[j].equals("and"))
                        j++;

                    if (i < j) {
                        String authorString = Arrays.stream(Arrays.copyOfRange(words, i, j))
                                .collect(Collectors.joining(" "));
                        Author author = authorParser.parse(authorString);
                        result.add(author);
                    }

                    i = j;
                }

                return result;
            } catch (ParseException e) {
                System.out.println("WARNING: " + e.getMessage());
            }
        }

        /* No exception is thrown - there may be no author */
        return null;
    }
}
