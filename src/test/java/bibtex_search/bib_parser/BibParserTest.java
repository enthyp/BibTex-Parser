package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.RecordType;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class BibParserTest {
    @Test
    public void parseFileTest() throws IOException {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());

        BibParser bibParser = new BibParser();
        bibParser.parseFile(file);
    }
}