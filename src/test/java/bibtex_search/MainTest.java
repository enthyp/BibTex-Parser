package bibtex_search;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MainTest {
    @Test
    public void parseArgumentsTest() {
        String filePath = "file.bib";
        String[] authors = new String[] {"john", "doe"};
        String[] categories = new String[] {"book", "article", "journal", "book"};
        String[] args = new String[]{"-f","file.bib", "-a", "john", "doe", "-c", "book", "article", "journal", "book"};

        CommandLine cmd = Main.parseArguments(args);
        assertEquals(cmd.getOptionValue("f"), filePath);
        assertArrayEquals(cmd.getOptionValues("a"), authors);
        assertArrayEquals(cmd.getOptionValues("c"), categories);

        /* Print some. */
        Arrays.stream(cmd.getOptionValues("a")).forEach(System.out::println);

        /* Show help. */
        Main.parseArguments(new String[] {"some", "stuff"});
    }
}