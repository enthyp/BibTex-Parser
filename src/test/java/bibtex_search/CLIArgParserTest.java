package bibtex_search;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CLIArgParserTest {

    @Test
    public void parseArgsTest() {
        String filePath = "file.bib";
        String[] authors = new String[] {"john", "doe"};
        String[] categories = new String[] {"book", "article", "journal", "book"};
        String[] args = new String[]{"-f","file.bib", "-a", "john", "doe", "-c", "book", "article", "journal", "book"};

        try {
            CLIArgParser argParser = new CLIArgParser();
            argParser.parseArgs(args);
            assertEquals(argParser.getBibFilePath(), filePath);
            assertTrue(argParser.getCriteria().containsKey("authors"));
            assertArrayEquals(argParser.getCriteria().get("authors"), authors);
            assertTrue(argParser.getCriteria().containsKey("categories"));
            assertArrayEquals(argParser.getCriteria().get("categories"), categories);

            /* Check help (exception message). */
            argParser.parseArgs(new String[] {"some", "stuff"});
        } catch (ParseException exc) {
            System.out.println(exc.getMessage());
        }
    }
}