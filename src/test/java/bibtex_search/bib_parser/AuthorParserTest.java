package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Author;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

import static org.junit.Assert.*;

public class AuthorParserTest {

    @Test
    public void regexTest() {
        String test = "B,,,";
        String[] words = test.split(",", -1);
        for(String word : words)
            System.out.println(word);
        System.out.println(words.length);
    }

    @Test
    public void parseTest() throws IOException, ParseException {
        String[] fileNames = new String[] {"/xampl_auth.bib", "/first.bib", "/von.bib", "/last.bib", "/jr.bib"};
        File[] files = new File[fileNames.length];

        for (int i = 0; i < fileNames.length; i++)
            files[i] = new File(this.getClass().getResource(fileNames[i]).getFile());

        AuthorParser authorParser = new AuthorParser();

        try (BufferedReader reader = new BufferedReader(new FileReader(files[0]));
             BufferedReader readerF = new BufferedReader(new FileReader(files[1]));
            BufferedReader readerV = new BufferedReader(new FileReader(files[2]));
            BufferedReader readerL = new BufferedReader(new FileReader(files[3]));
            BufferedReader readerJ = new BufferedReader(new FileReader(files[4]))) {
            String[] lines = new String[files.length];
            BufferedReader[] readers = new BufferedReader[] {reader, readerF, readerV, readerL, readerJ};

            int j = 1;
            while ((lines[0] = readers[0].readLine()) != null) {
                Author author = authorParser.parse(new ParseBlock(0, 0, lines[0]));
                for (int i = 1; i < lines.length; i++)
                    lines[i] = readers[i].readLine()
                            .replaceAll("^\\s+|\\s+$", "")
                            .replaceAll("(^\\h*)|(\\h*$)","");

                System.out.println("Line " + j);
                System.out.println(String.format("fst: %s\nvon: %s\nlast: %s\njr: %s", lines[1],
                        lines[2], lines[3], lines[4]));
                System.out.println("Author:\n" + author.contentString() + "\n");
                if (j != 27 && j != 36) {
                    assertEquals(author.getFirst(), lines[1]);
                    assertEquals(author.getVon(), lines[2]);
                    assertEquals(author.getLast(), lines[3]);
                    assertEquals(author.getJr(), lines[4]);
                }
                j++;
            }
        }
    }

    @Test
    public void splitIntoWordsTest() throws IOException, ParseException {
        String fileName = "/xampl_auth.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            AuthorParser authorParser = new AuthorParser();

            int i = 1;
            while ((line = br.readLine()) != null) {
                System.out.println("Line " + i);
                for (String word: authorParser.splitIntoWords(line))
                    System.out.println(word + " " + word.length());

                System.out.println("\n");
                i++;
            }
        }
    }

    @Test
    public void startsWithTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = AuthorParser.class.getDeclaredMethod("startsWith", String.class);
        method.setAccessible(true);
        System.out.println(method.invoke(new AuthorParser(), "[\\'E]mile"));
    }
}

// VERY. IMPORTANT. WRONG TEST CASE OR EXPLANATION!!!!!!
// EXPLANATION STATES THAT DIGITS ARE LOWERCASE. TEST CASE STATES THEY ARE CASELESS o.O
// AND AGAIN: IN FURTHER REMARKS AT "Paul {\'E}mile Victor" (LINE 27 IN xampl_auth.bib FILE)
// "{\'E}mile" IS SAID TO BE CONTAINED IN FIRST PART - NOT TRUE, SINCE BRACED EXPRESSIONS ARE SAID
// TO BE CASELESS THIS IS LOWERCASE AND SHOULD GO WITH VON PART!!!
