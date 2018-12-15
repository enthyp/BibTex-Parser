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
     * @param args command line arguments.
     */
    void parseArgs(String[] args) throws ParseException;

    /**
     * Return path to .bib file.
     * @return command line argument containing path to .bib file.
     */
    String getBibFilePath();

    /**
     * Returns a map from a name of a criterion to an array of values of that criterion
     * that are being looked for.
     * E.g. the name could be "authors". The values would then be last names of authors
     * whose publication are being looked for.
     * If some criterion was not specified, then there is no map entry for it.
     * @return a map from name of a criterion to array of values of that criterion.
     */
    Map<String, String[]> getCriteria();
}
