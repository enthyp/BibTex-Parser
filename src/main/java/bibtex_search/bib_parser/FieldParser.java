package bibtex_search.bib_parser;

import java.text.ParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldParser extends Parser {

    protected Map<String, String> stringVars;

    public FieldParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Pair parse(ParseBlock fieldContentBlock) throws ParseException {
        Pattern fieldPattern = Pattern.compile("(?<name>\\w+)\\s=\\s(?<value>[^,|]+)");
        Matcher fieldMatcher = fieldPattern.matcher(fieldContentBlock.getContent());

        if (fieldMatcher.find()) {
            String value = parseValue(new ParseBlock(fieldContentBlock.getLineStart(),
                    fieldContentBlock.getLineEnd(), fieldMatcher.group("value")));
            return new Pair(fieldMatcher.group("name"), value);
        } else {
            throw new ParseException(String.format("Line %d\nError parsing record's field: %s\n\n",
                    fieldContentBlock.getLineStart(), fieldContentBlock), -1);
        }
    }

    public String parseValue(ParseBlock valueBlock) throws ParseException {
        StringBuilder output = new StringBuilder();
        Pattern wordPattern = Pattern.compile("(?<word>(\"[^,|\"]+\"|\\w+))(\\s#\\s(?<tail>.+))?");
        Matcher wordMatcher = wordPattern.matcher(valueBlock.getContent());

        while (wordMatcher.find()) {
            String word = wordMatcher.group("word");

            if (word.contains("\"")) {
                /* Not a named variable. */
                word = word.replaceFirst("^\"+", "").replaceFirst("\"$", "");
                output.append(word);
            } else {
                /* Named variable encountered. */
                if (stringVars.containsKey(word.toUpperCase()))
                    output.append(stringVars.get(word.toUpperCase()));
                else
                    throw new ParseException(String.format("Line %d\nVariable %s not found!",
                            valueBlock.getLineStart(), word), -1);
            }

            if (wordMatcher.group("tail") != null) {
                wordMatcher = wordPattern.matcher(wordMatcher.group("tail"));
            }
        }

        return output.toString();
    }
}
