package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.IRecord;

import java.io.IOException;
import java.util.Set;

/**
 * A parser of .bib files.
 */
public interface IBibParser {

    /**
     * This method turns a (simplified) .bib file into a Set of IRecord
     * instances.
     *
     * @param filePath to input .bib file
     */
    void parse(String filePath) throws IOException;

    /**
     *
     * @return parse results - individual entries of the input file
     */
    Set<IRecord> getRecords();
}
