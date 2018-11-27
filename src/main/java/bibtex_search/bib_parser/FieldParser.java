package bibtex_search.bib_parser;

import java.text.ParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldParser {

    protected Map<String, String> stringVars;

    public FieldParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Pair parse(String fieldContent) throws ParseException {
        Pattern fieldPattern = Pattern.compile("(?<name>\\w+)\\s=\\s(?<value>[^,|]+)");
        Matcher fieldMatcher = fieldPattern.matcher(fieldContent);

        if (fieldMatcher.find()) {
            String value = parseValue(fieldMatcher.group("value"));
            return new Pair(fieldMatcher.group("name"), value);
        } else {
            throw new ParseException("Error parsing record's field!\nFaulty field:\n" + fieldContent + "\n", -1);
        }
    }

    public String parseValue(String value) {
        StringBuilder output = new StringBuilder();
        Pattern wordPattern = Pattern.compile("(?<word>(\"[^,|\"]+\"|\\w+))(\\s#\\s(?<tail>.+))?");
        Matcher wordMatcher = wordPattern.matcher(value);

        while (wordMatcher.find()) {
            String word = wordMatcher.group("word");

            if (word.contains("\"")) {
                /* Not a named variable. */
                word = word.replaceFirst("^\"+", "").replaceFirst("\"$", "");
                output.append(word);
            } else {
                /* Named variable encountered. */
                if (stringVars.containsKey(word))
                    output.append(stringVars.get(word));
                else
                    System.out.println(String.format("WARNING: Variable %s not found!", word));
            }

            if (wordMatcher.group("tail") != null) {
                wordMatcher = wordPattern.matcher(wordMatcher.group("tail"));
            }
        }

        return output.toString();
    }
}
