package bibtex_search;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import static org.junit.Assert.*;

public class CLIArgParserTest {

    @Test(expected = ParseException.class)
    public void parseNoArgsTest() throws ParseException {
        String[] args = new String[]{};

        CLIArgParser argParser = new CLIArgParser();
        argParser.parseArgs(args);
    }

    @Test
    public void parsePathTest() throws ParseException {
        String filePath = "file.bib";
        String[] args = new String[]{"-f","file.bib"};

        CLIArgParser argParser = new CLIArgParser();
        argParser.parseArgs(args);
        assertEquals(argParser.getBibFilePath(), filePath);
    }

    @Test(expected = ParseException.class)
    public void parseEmptyPathTest() throws ParseException {
        String[] args = new String[]{"-f"};

        CLIArgParser argParser = new CLIArgParser();
        argParser.parseArgs(args);
    }

    @Test(expected = ParseException.class)
    public void parseNoCriterionValueTest() throws ParseException {
        String[] args = new String[]{"-f","file.bib", "-a"};

        CLIArgParser argParser = new CLIArgParser();
        argParser.parseArgs(args);
    }

    @Test
    public void parseEmptyCriterionTest() throws ParseException {
        String[] args = new String[]{"-f","file.bib"};

        CLIArgParser argParser = new CLIArgParser();
        argParser.parseArgs(args);
        assertFalse(argParser.getCriteria().containsKey("authors"));
    }

    @Test
    public void parseAuthorTest() throws ParseException {
        String[] authors = new String[] {"john", "doe"};
        String[] args = new String[]{"-f","file.bib", "-a", "john", "doe"};

        CLIArgParser argParser = new CLIArgParser();
        argParser.parseArgs(args);
        assertTrue(argParser.getCriteria().containsKey("authors"));
        assertArrayEquals(argParser.getCriteria().get("authors"), authors);
    }

    @Test
    public void parseYearsTest() throws ParseException {
        String[] years = new String[] {"20121", "2122"};
        String[] args = new String[]{"-f","file.bib", "-y", "20121", "2122"};

        CLIArgParser argParser = new CLIArgParser();
        argParser.parseArgs(args);
        assertTrue(argParser.getCriteria().containsKey("years"));
        assertArrayEquals(argParser.getCriteria().get("years"), years);
    }

    @Test
    public void parseCategoriesTest() throws ParseException {
        String[] categories = new String[] {"book", "article", "journal", "book"};
        String[] args = new String[]{"-f","file.bib", "-c", "book",
                "article", "journal", "book"};

        CLIArgParser argParser = new CLIArgParser();
        argParser.parseArgs(args);

        assertTrue(argParser.getCriteria().containsKey("categories"));
        assertArrayEquals(argParser.getCriteria().get("categories"), categories);
    }


    @Test
    public void helpTest() {
        String msg = "usage: java -jar BibTex-Parser.jar [-a <authors>] [-c <categories>] -f <file> [-y <years>]\n" +
                " -a,--authors <authors>         last names of authors\n" +
                " -c,--categories <categories>   names of categories\n" +
                " -f,--filepath <file>           path to .bib file\n" +
                " -y,--years <years>\n";

        CLIArgParser argParser = new CLIArgParser();

        try {
            argParser.parseArgs(new String[]{"some", "stuff"});
        } catch (ParseException exc) {
            assertEquals(exc.getMessage(), msg);
        }
    }

    @Test
    public void showHelp() {
        CLIArgParser argParser = new CLIArgParser();

        try {
            argParser.parseArgs(new String[]{"some", "stuff"});
        } catch (ParseException exc) {
            System.out.println(exc.getMessage());
        }
    }
}