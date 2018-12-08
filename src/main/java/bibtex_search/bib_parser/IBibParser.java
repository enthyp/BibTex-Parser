package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.IRecord;

import java.io.IOException;
import java.util.Set;

public interface IBibParser {

    void parse(String filePath) throws IOException;

    Set<IRecord> getRecords();
}
