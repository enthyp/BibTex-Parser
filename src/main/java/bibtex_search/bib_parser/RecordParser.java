package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Person;
import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;
import org.apache.commons.cli.ParseException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RecordParser extends WarningHandler {
    // TODO: this slightly breaks (?) the single responsibility principle (large methods).
    // TODO: empty field values don't count!!!
    private Map<String, String> stringVars;
    public RecordParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Record parseRecord(String recordContent) throws ParseException {
        /* Break down the record again (to decouple from BibParser class). */
        String regex = "@(?<category>\\w+)\\{(?<content>.+?)}";
        Pattern recordPattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher recordMatcher = recordPattern.matcher(recordContent);
        recordMatcher.find();
        String category = recordMatcher.group("category").toUpperCase();
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
        Map<String, Set<Person>> foundPeople = parsePeople(content);
        Map<String, String> foundFields = parseFields(content);

        /* Check for mandatory and ignored fields. */
        Set<String> mandatory =  new HashSet<>(mandatoryFields.get(RecordType.valueOf(category)));
        Set<String> optional = new HashSet<>(optionalFields.get(RecordType.valueOf(category)));
        HashSet<String> found = new HashSet<>(foundFields.keySet());
        if (!foundPeople.isEmpty()) {
            found.addAll(foundPeople.keySet());
        }

        /* Get all mandatory and optional fields together. */
        Set<String> admissible = optional;
        admissible.addAll(mandatory);

        /* See what's left of mandatory. */
        mandatory.removeAll(found);
        if (mandatory.isEmpty()) {
            /* All mandatory fields were found - ignore non-mandatory nor optional. */
            found.retainAll(admissible);

            /* Remove non-admissible fields. */
            foundFields.entrySet().removeIf(field -> !found.contains(field.getKey()));
        } else {
            // TODO: if we want multiple alternatives - must have a set as the map value
            // and iterate over these sets!!!
            /* Check if alternative fields appeared. */
            Set<String> alternativeMandatory = mandatory.stream()
                    .map(e -> (alternatives.containsKey(RecordType.valueOf(category)) ?
                            alternatives.get(RecordType.valueOf(category)).get(e) : null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            admissible.addAll(alternativeMandatory);
            /* See what's left of mandatory after switching to alternatives. */
            alternativeMandatory.removeAll(found);
            if (alternativeMandatory.isEmpty()) {
                /* All mandatory fields were found - ignore non-mandatory nor optional. */
                found.retainAll(admissible);

                /* Remove non-admissible fields. */
                foundFields.entrySet().removeIf(field -> !found.contains(field.getKey()));
            } else {
                /* Report a faulty record. */
                throw new ParseException(this.getLocation() +
                        String.format("Record lacks mandatory fields: %s\n",
                        mandatory.stream().collect(Collectors.joining(", "))));
            }
        }

        return new Record(RecordType.valueOf(category), foundKey, foundPeople, foundFields);
    }

    /**
     *
     * @param content record's body
     * @return record's key
     */
    private String parseKey(String content) throws ParseException {
        Pattern keyPattern = Pattern.compile("^(?<key>[^,|=\\s]+),");
        Matcher keyMatcher = keyPattern.matcher(content);

        if (keyMatcher.find()) {
            return keyMatcher.group("key");
        } else {
            throw new ParseException("Error parsing record's key!");
        }
    }

    /**
     *
     * @param content record's body
     * @return map describing encountered fields
     */
    private Map<String, String> parseFields(String content) {
        Map<String, String> fields = new LinkedHashMap<>();
        Pattern fieldPattern = Pattern.compile("\\s*(?<field>[^,|=]+\\s*=\\s*[^,|=]+)(,|$)");
        Matcher fieldMatcher = fieldPattern.matcher(content);

        while (fieldMatcher.find()) {
            int fieldStart = this.getLineNumber(fieldMatcher.start());
            /* `trim` method is used to get rid of trailing whitespace. */
            String fieldContent = fieldMatcher.group("field").trim();

            FieldParser fieldParser = new FieldParser(stringVars);
            fieldParser.setLineBeginnings(fieldContent, fieldStart);

            try {
                Pair result = fieldParser.parse(fieldContent);
                /* Only retain the first occurrence. */
                if (!fields.containsKey(result.getFirst().toLowerCase()) && !result.getSecond().equals("")) {
                    fields.put(result.getFirst().toLowerCase(), result.getSecond());
                }
            } catch (ParseException exc) {
                this.handle(exc);
            }
        }

        return fields;
    }

    /**
     *
     * @param content record's body
     * @return personal data of all found people (authors, editors, etc.)
     */
    private Map<String, Set<Person>> parsePeople(String content) {
        Pattern personPattern = Pattern.compile("(?<type>\\w+)\\s*=\\s*\"(?<person>[^|\"]+)\"\\|",
                Pattern.CASE_INSENSITIVE);
        Matcher personMatcher = personPattern.matcher(content);
        /* A map from person type (e.g. editor) to all the people of this type in the record. */
        Map<String, Set<Person>> results = new LinkedHashMap<>();

        while (personMatcher.find()) {
            PersonParser personParser = new PersonParser();

            try {
                /* TODO: test multiline people. */
                String peopleType = personMatcher.group("type").toLowerCase();
                String peopleString = personMatcher.group("person").trim();

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

        /* No exception is thrown - there may be no author */
        return results;
    }


    /* Behold BibTex rules in all their glory. */
    private static Map<RecordType, Set<String>> mandatoryFields = new HashMap<RecordType, Set<String>>() {{
        put(RecordType.ARTICLE, new HashSet<String>(){{
            add("author");
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

    private static Map<RecordType, Map<String, String>> alternatives =
            new HashMap<RecordType, Map<String, String>>() {{
                put(RecordType.BOOK, new HashMap<String, String>(){{
                    put("author", "editor");
                }});
                put(RecordType.INPROCEEDINGS, new HashMap<String, String>(){{
                    put("volume", "number");
                }});
                put(RecordType.INBOOK, new HashMap<String, String>(){{
                    put("author", "editor");
                    put("volume", "number");
                }});
                put(RecordType.INCOLLECTION, new HashMap<String, String>(){{
                    put("volume", "number");
                }});
                put(RecordType.TECHREPORT, new HashMap<String, String>(){{
                    put("volume", "number");
                }});
            }};
}
