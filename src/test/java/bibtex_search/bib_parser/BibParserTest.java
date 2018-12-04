package bibtex_search.bib_parser;

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

    // TODO: error messages should pinpoint the fault exactly (line in file..?)
    // IDEA: pass the line number or interval down to the parser - so that it can print out appropriate error
}