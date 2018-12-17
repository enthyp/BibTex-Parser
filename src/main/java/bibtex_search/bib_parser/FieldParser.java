package bibtex_search.bib_parser;

import org.apache.commons.cli.ParseException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser of entries' fields, excluding ones describing people, e.g. "editor".
 */
public class FieldParser extends WarningHandler {

    /**
     * A map between a string variable name and its value.
     */
    private Map<String, String> stringVars;

    public FieldParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    /**
     * Parse a string of characters that forms field's specification, e.g. 'year = "1987"'.
     * @param fieldContent string representation of record field's name and value.
     * @return a pair consisting of field's name and field's value.
     */
    public Pair parse(String fieldContent) throws ParseException {
        Pattern fieldPattern = Pattern.compile("^(?<name>\\w+)\\s*=\\s*(?<value>[^,|]+)");
        Matcher fieldMatcher = fieldPattern.matcher(fieldContent);

        if (fieldMatcher.find()) {
            String value;

            try {
                value = parseValue(fieldMatcher.group("value"));
            } catch (ParseException exc) {
                throw new ParseException(this.getLocation() + exc.getMessage());
            }

            return new Pair(fieldMatcher.group("name").toLowerCase(), value);
        } else {
            throw new ParseException(this.getLocation() +
                    String.format("Could not parse record's field: %s", fieldContent));
        }
    }

    /**
     * Parse field's value and substitute string variables as necessary.
     * @param valueContent string representation of field's value.
     * @return final form of the field's value with variables substituted.
     */
    private String parseValue(String valueContent) throws ParseException {
        String initialContent = valueContent;
        StringBuilder output = new StringBuilder();
        Pattern wordPattern = Pattern.compile("^\\s*(?<word>(\"[^,|\"]*\"|\\w+))(\\s*#\\s*(?<tail>.+))?",
                Pattern.DOTALL);
        Matcher wordMatcher = wordPattern.matcher(valueContent);

        while (wordMatcher.find()) {
            if (valueContent == null) {
                /*  */
                throw new ParseException(String.format("Could not parse field's value: %s", initialContent));
            }

            String word = wordMatcher.group("word");

            if (wordMatcher.end("word") < valueContent.length() && wordMatcher.group("tail") == null) {
                /* Word was not matched fully. */
                throw new ParseException(String.format("Could not parse field's value: %s", initialContent));
            }

            if (word.contains("\"")) {
                /* Not a named variable. */
                word = word.replaceFirst("^\"+", "").replaceFirst("\"$", "");
                output.append(word);
            } else {
                if (stringVars.containsKey(word.toLowerCase())) {
                    /* Named variable encountered. */
                    output.append(stringVars.get(word.toLowerCase()));
                } else
                    throw new ParseException(String.format("Variable %s not found!", word));
            }

            valueContent = wordMatcher.group("tail");
            if (valueContent != null) {
                wordMatcher = wordPattern.matcher(valueContent);
            }
        }

        if (valueContent == null) {
            return output.toString();
        } else {
            /* Something still remains at the end but is not matched. */
            throw new ParseException(String.format("Could not parse field's value: %s", initialContent));
        }
    }
}
