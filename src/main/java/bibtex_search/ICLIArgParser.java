package bibtex_search;

import org.apache.commons.cli.ParseException;

import java.util.Map;

/**
 * An Apache Commons CLI-based argument parser.
 */
public interface ICLIArgParser {
    /**
     * This method parses the input for the path to the input .bib file
     * and additional search criteria specified by a particular implementation.
     * @param args command line arguments
     */
    void parseArgs(String[] args) throws ParseException;

    /**
     *
     * @return just what was given as path to .bib file
     */
    String getBibFilePath();

    /**
     *
     * @return a map from criterion name (implementation specific!)
     * to an array of values of that criterion we want to see in the search results
     */
    Map<String, String[]> getCriteria();
}
