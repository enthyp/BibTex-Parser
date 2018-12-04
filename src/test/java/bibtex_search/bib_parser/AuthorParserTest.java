package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Author;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import static org.junit.Assert.*;

public class AuthorParserTest {

    @Test
    public void parseTest() throws IOException, ParseException {
        String fileName = "/xampl_auth.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());

        AuthorParser authorParser = new AuthorParser();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Author author = authorParser.parse(line);
                System.out.println(author.contentString());
            }
        }
    }
}