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

    String getBibFilePath();

    Map<String, String[]> getCriteria();
}
