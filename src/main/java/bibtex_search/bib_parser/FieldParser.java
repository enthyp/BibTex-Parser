package bibtex_search.bib_parser;

import org.apache.commons.cli.ParseException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldParser extends WarningHandler {

    private Map<String, String> stringVars;

    public FieldParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Pair parse(String fieldContent) throws ParseException {
        Pattern fieldPattern = Pattern.compile("(?<name>\\w+)\\s*=\\s*(?<value>[^,|]+)");
        Matcher fieldMatcher = fieldPattern.matcher(fieldContent);

        if (fieldMatcher.find()) {
            String value;

            try {
                value = parseValue(fieldMatcher.group("value"));
            } catch (ParseException exc) {
                throw new ParseException(this.getLocation() + exc.getMessage());
            }

            return new Pair(fieldMatcher.group("name"), value);
        } else {
            throw new ParseException(this.getLocation() +
                    String.format("Could not parse record's field: %s", fieldContent));
        }
    }

    private String parseValue(String valueContent) throws ParseException {
        StringBuilder output = new StringBuilder();
        Pattern wordPattern = Pattern.compile("(?<word>(\"[^,|\"]+\"|\\w+))(\\s*#\\s*(?<tail>.+))?");
        Matcher wordMatcher = wordPattern.matcher(valueContent);

        while (wordMatcher.find()) {
            String word = wordMatcher.group("word");

            if (word.contains("\"")) {
                /* Not a named variable. */
                word = word.replaceFirst("^\"+", "").replaceFirst("\"$", "");
                output.append(word);
            } else {
                /* Named variable encountered. */
                if (stringVars.containsKey(word.toLowerCase()))
                    output.append(stringVars.get(word.toLowerCase()));
                else
                    throw new ParseException(String.format("Variable %s not found!", word));
            }

            if (wordMatcher.group("tail") != null) {
                wordMatcher = wordPattern.matcher(wordMatcher.group("tail"));
            }
        }

        return output.toString();
    }
}
