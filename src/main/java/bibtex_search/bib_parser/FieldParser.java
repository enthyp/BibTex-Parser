package bibtex_search.bib_parser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldParser {

    protected Map<String, String> stringVars;

    public FieldParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Pair parse(String fieldContent) {
        // TODO: add stringVar handling!!!
        Pattern fieldPattern = Pattern.compile("(?<name>\\w+)\\s=\\s\"(?<value>[^,|\"]+)\"");
        Matcher fieldMatcher = fieldPattern.matcher(fieldContent);

        if (fieldMatcher.find()) {
            return new Pair(fieldMatcher.group("name"), fieldMatcher.group("value"));
        }

        /* No exception is thrown. */
        return null;
    }
}
