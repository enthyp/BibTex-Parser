package bibtex_search.bib_parser;

import java.util.HashMap;
import java.util.Map;

public class FieldParser {

    protected Map<String, String> stringVars;

    public FieldParser(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }

    public Pair parse(String fieldContent) {
        Pair field = new Pair("bob", "mob");

        return field;
    }
}
