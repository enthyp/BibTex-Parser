package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Person;
import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;
import org.apache.commons.cli.ParseException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parser of individual entries in a .bib file.
 */
public class RecordParser extends WarningHandler {

    /**
     * A map between a string variable name and its value.
     */
    private Map<String, String> stringVars;

    public RecordParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    /**
     * Parse given string representation of a BibTeX entry and return appropriate object.
     *
     * @param recordContent string representation of a BibTeX entry obtained from a .bib file.
     * @return an object representation of given BibTeX entry.
     */
    public Record parseRecord(String recordContent) throws ParseException {
        /* Break down the record again (to decouple from BibParser class). */
        String regex = "@(?<category>\\w+)\\{(?<content>.+?)}";
        Pattern recordPattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher recordMatcher = recordPattern.matcher(recordContent);
        recordMatcher.find();
        String category = recordMatcher.group("category").toUpperCase();
        if (!RecordType.names.contains(category))
            throw new ParseException(String.format(this.getLocation() + "Unknown entry type: %s", category));
        String content = recordMatcher.group("content");

        /* Get record's key. */
        String foundKey;
        try {
            foundKey = parseKey(content);
        } catch (ParseException exc) {
            /* If failed to do so - report a faulty record. */
            throw new ParseException(this.getLocation() + exc.getMessage());
        }

        /* Get record's fields - people (authors, editors, etc.) and other. */
        Map<String, Set<Person>> foundPeople = parsePeople(recordContent);
        Map<String, String> foundFields = parseFields(recordContent);

        return new Record(RecordType.valueOf(category), foundKey, foundPeople, foundFields);
    }

    /**
     * Returns the key of an entry or throws an exception if none is found.
     * @param content record's body.
     * @return record's key.
     */
    private String parseKey(String content) throws ParseException {
        Pattern keyPattern = Pattern.compile("^(?<key>[^,|=\\s]+),");
        Matcher keyMatcher = keyPattern.matcher(content);

        if (keyMatcher.find()) {
            return keyMatcher.group("key").toLowerCase();
        } else {
            throw new ParseException("Error parsing record's key!");
        }
    }

    /**
     * Parse record's content for all fields describing people (currently just authors and editors)
     * and return their personal data.
     * @param content record's body.
     * @return a map between person category ("author" or "editor") and a set of object representations
     * of all found people of that category.
     */
    private Map<String, Set<Person>> parsePeople(String content) {
        Pattern personPattern = Pattern.compile("(?<type>\\w+)\\s*=\\s*\"(?<person>[^,\"]+)\"(,|(\\s*}))");
        Matcher personMatcher = personPattern.matcher(content);
        /* A map from person type (e.g. editor) to all the people of this type in the record. */
        Map<String, Set<Person>> results = new LinkedHashMap<>();

        while (personMatcher.find()) {
            String peopleType = personMatcher.group("type").toLowerCase();
            if (peopleType.equals("author") || peopleType.equals("editor")) {
                PersonParser personParser = new PersonParser();

                try {
                    String peopleString = personMatcher.group("person");

                    int personStart = this.getLineNumber(personMatcher.start());
                    personParser.setLineBeginnings(peopleString, personStart);

                    Set<Person> partialResult = new LinkedHashSet<>();
                    String[] words = personParser.splitIntoWords(peopleString);

                    /* Split on "and" into multiple authors. */
                    for (int i = 0; i < words.length; i++) {
                        int j = i;
                        while (j < words.length && !words[j].toUpperCase().equals("AND"))
                            j++;

                        if (i < j) {
                            String personString = Arrays.stream(Arrays.copyOfRange(words, i, j))
                                    .collect(Collectors.joining(" "));
                            Person person = personParser.parse(personString);
                            person.setType(peopleType);
                            partialResult.add(person);
                        }

                        i = j;
                    }

                    /* Only retain the first occurence. */
                    if (!results.containsKey(peopleType)) {
                        results.put(peopleType, partialResult);
                    }
                } catch (ParseException e) {
                    /* `PersonParser.splitIntoWords` and `PersonParser.parse` provide location details already. */
                    System.out.println("WARNING: " + e.getMessage());
                }
            }
        }

        /* No exception is thrown - there may be no author */
        return results;
    }

    /**
     * Parse record's content for all fields except ones describing people
     * and return a map describing them.
     * @param content record's body.
     * @return map between a field's name and a field's value (after variable substitution).
     */
    private Map<String, String> parseFields(String content) {
        Map<String, String> fields = new LinkedHashMap<>();
        Pattern fieldPattern = Pattern.compile("\\s*(?<field>[^,|=\"\\s]+\\s*=\\s*[^,|]+)(,|(\\s*}))");
        Matcher fieldMatcher = fieldPattern.matcher(content);

        while (fieldMatcher.find()) {
            int fieldStart = this.getLineNumber(fieldMatcher.start("field"));
            /* `trim` method is used to get rid of trailing whitespace. */
            String fieldContent = fieldMatcher.group("field").trim();

            FieldParser fieldParser = new FieldParser(stringVars);
            fieldParser.setLineBeginnings(fieldContent, fieldStart);

            try {
                Pair result = fieldParser.parse(fieldContent);
                /* Only retain the first occurrence. */
                if (!fields.containsKey(result.getFirst()) && !result.getSecond().equals("")) {
                    /* Don't repeat yourself. */
                    if (!result.getFirst().equals("author") && !result.getFirst().equals("editor"))
                        fields.put(result.getFirst(), result.getSecond());
                }
            } catch (ParseException exc) {
                this.handle(exc);
            }
        }

        return fields;
    }
}
