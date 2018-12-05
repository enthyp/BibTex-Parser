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

    // TODO: error messages should pinpoint the fault exactly (line in file..?) (do 2nd)
    // IDEA: pass the line number or interval down to the parser - so that it can print out appropriate error
    // there should be a wrapper class for the return types or for arguments - so that we can either pass
    // up the information about a problem (a warning, not an exception) or pass down the information about
    // current position in the text - so that exact information can be printed out.
}