package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Author;
import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordParser {
    // TODO: fill these.
    private static Map<RecordType, Set<String>> mandatoryFields;
    private static Map<RecordType, Set<String>> optionalFields;

    private Author author;
    private Map<String, String> fields;
    private Map<String, String> stringVars;

    public RecordParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Record parseRecord(String category, String recordContent) {
        Record record = new Record();
        /* Type of the record is known already. */
        record.setType(RecordType.valueOf(category));

        String foundAuthor = parseAuthor(recordContent);

        Map<String, String> foundFields = parseFields(recordContent);

        return record;
    }

    /**
     *
     * @param recordContent String with record contents
     * @return map describing encountered fields
     */
    private Map<String, String> parseFields(String recordContent) {
        return new HashMap<>();
    }

    /**
     *
     * @param recordContent String with record contents
     * @return String with author personal data (TODO: change to Author instance)
     */
    private String parseAuthor(String recordContent) {
        return "author";
    }


    /**
     *
     * @param recordContent String with record contents
     * @return Matcher object for author and other fields
     */
    private Matcher match(String recordContent) {
        //String regex = "@(?<category>\\w+)\\{(?<content>.+?)}";
        String regex = "";
        Pattern recordPattern = Pattern.compile("(?<key>[^,\\s]+),\\s*()", Pattern.DOTALL);

        return recordPattern.matcher(recordContent);
    }

}
