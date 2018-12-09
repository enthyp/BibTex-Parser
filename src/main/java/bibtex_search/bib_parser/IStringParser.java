package bibtex_search.bib_parser;

import org.apache.commons.cli.ParseException;

public interface IStringParser {
    /**
     * This method parses given string in an implementation-specific manner.
     *
     * @param content to be parsed
     */
    void parse(String content) throws ParseException;
}
