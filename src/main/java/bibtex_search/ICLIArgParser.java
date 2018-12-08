package bibtex_search;

import org.apache.commons.cli.ParseException;

/**
 * An Apache Commons CLI-based argument parser.
 */
public interface ICLIArgParser {
    /**
     *
     * @param args command line arguments
     * This method parses at least .bib file path, author's last names and categories.
     */
    void parseArgs(String[] args) throws ParseException;

    String getBibFilePath();

    String[] getAuthors();

    String[] getCategories();
}
