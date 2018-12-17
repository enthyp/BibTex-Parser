package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.IRecord;

import java.io.IOException;
import java.util.Set;

/**
 * A parser of .bib files.
 */
public interface IBibParser {

    /**
     * This method converts .bib file into a collection of objects
     * containing information about individual records. Obtained
     * collection is then stored internally by IBibParser instance.
     *
     * @param filePath absolute path to input .bib file.
     */
    void parse(String filePath) throws IOException;

    /**
     * Returns all found entries from given file.
     * @return a collection of individual entries from the input file.
     */
    Set<IRecord> getRecords();
}
