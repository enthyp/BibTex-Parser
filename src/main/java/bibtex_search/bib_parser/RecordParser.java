package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Person;
import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RecordParser extends Parser {
    // TODO: empty field values don't count!!!
    private Map<String, String> stringVars;

    public RecordParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Record parseRecord(String category, ParseBlock recordBlock) throws ParseException {
        String foundKey = parseKey(recordBlock);
        Map<String, Set<Person>> foundPeople = parsePeople(recordBlock);
        Map<String, String> foundFields = parseFields(recordBlock, category);

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
                throw new ParseException(String.format("Lines %d-%d\nRecord lacks mandatory fields: %s\n",
                        recordBlock.getLineStart(),
                        recordBlock.getLineEnd(),
                        mandatory.stream().collect(Collectors.joining(", "))), -1);
            }
        }

        return new Record(RecordType.valueOf(category), foundKey, foundPeople, foundFields);
    }

    /**
     *
     * @param recordBlock object with record contents
     * @return String with record's key
     */
    private String parseKey(ParseBlock recordBlock) throws ParseException {
        Pattern keyPattern = Pattern.compile("^(?<key>[^,|\\s]+),");
        Matcher keyMatcher = keyPattern.matcher(recordBlock.getContent());

        if (keyMatcher.find()) {
            return keyMatcher.group("key");
        } else {
            throw new ParseException(String.format("Line %d\nError parsing record's key: %s\n\n",
                    recordBlock.getLineStart(), recordBlock), -1);
        }
    }

    /**
     *
     * @param recordBlock object with record contents
     * @return map describing encountered fields
     */
    private Map<String, String> parseFields(ParseBlock recordBlock, String category) {
        Map<String, String> fields = new LinkedHashMap<>();
        Pattern fieldPattern = Pattern.compile("\\s*(?<field>[^,|=]+\\s=\\s[^,|]+)(,|$)");
        Matcher fieldMatcher = fieldPattern.matcher(recordBlock.getContent());

        while (fieldMatcher.find()) {
            int fieldStart = this.getLineNumber(fieldMatcher.start());
            int fieldEnd = this.getLineNumber(fieldMatcher.end());
            /* `trim` method is used to get rid of trailing whitespace. */
            String fieldContent = fieldMatcher.group("field").trim();

            FieldParser fieldParser = new FieldParser(stringVars);
            fieldParser.setLineBeginnings(fieldContent, fieldStart);

            try {
                Pair result = fieldParser.parse(new ParseBlock(fieldStart, fieldEnd, fieldContent));
                /* Only retain the first occurrence. */
                if (!fields.containsKey(result.getFirst().toLowerCase())
                        && (alternatives.get(RecordType.valueOf(category)) == null
                                || !fields.containsKey(alternatives.get(RecordType.valueOf(category))
                                    .get(result.getFirst().toLowerCase())))) {
                    fields.put(result.getFirst().toLowerCase(), result.getSecond());
                }
            } catch (ParseException e) {
                System.out.println("WARNING: " + e.getMessage());
            }
        }

        return fields;
    }

    /**
     *
     * @param recordBlock object with record contents
     * @return personal data of all found people (authors, editors...)
     */
    private Map<String, Set<Person>> parsePeople(ParseBlock recordBlock) {
        Pattern personPattern = Pattern.compile("(?<type>\\w+)\\s+=\\s+\"(?<person>[^|\"]+)\"\\|",
                Pattern.CASE_INSENSITIVE);
        Matcher personMatcher = personPattern.matcher(recordBlock.getContent());
        Map<String, Set<Person>> results = new LinkedHashMap<>();

        while (personMatcher.find()) {
            PersonParser personParser = new PersonParser();

            try {
                /* TODO: test multiline people. */
                String peopleString = personMatcher.group("person").trim();
                String peopleType = personMatcher.group("type").toLowerCase();

                int personStart = this.getLineNumber(personMatcher.start());
                int personEnd = this.getLineNumber(personMatcher.end());
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
                        Person person = personParser.parse(new ParseBlock(personStart, personEnd, personString));
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
                System.out.println("WARNING: " + e.getMessage());
            }
        }

        /* No exception is thrown - there may be no author */
        return results;
    }

    /* Behold. */

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
