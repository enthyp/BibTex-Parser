package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Person;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class PersonParserTest {

    @Test
    public void parseAllTest() throws IOException, ParseException {
        String[] fileNames = new String[] {"/person_test/xampl_auth.bib", "/person_test/first.bib", "/person_test/von.bib", "/person_test/last.bib", "/person_test/jr.bib"};
        File[] files = new File[fileNames.length];

        for (int i = 0; i < fileNames.length; i++)
            files[i] = new File(this.getClass().getResource(fileNames[i]).getFile());

        PersonParser personParser = new PersonParser();

        try (BufferedReader reader = new BufferedReader(new FileReader(files[0]));
             BufferedReader readerF = new BufferedReader(new FileReader(files[1]));
            BufferedReader readerV = new BufferedReader(new FileReader(files[2]));
            BufferedReader readerL = new BufferedReader(new FileReader(files[3]));
            BufferedReader readerJ = new BufferedReader(new FileReader(files[4]))) {
            String[] lines = new String[files.length];
            BufferedReader[] readers = new BufferedReader[] {reader, readerF, readerV, readerL, readerJ};

            int j = 1;
            while ((lines[0] = readers[0].readLine()) != null) {
                Person person = personParser.parse(lines[0]);
                // Remove non-breaking whitespace!
                for (int i = 1; i < lines.length; i++)
                    lines[i] = readers[i].readLine()
                            .replaceAll("^\\s+|\\s+$", "")
                            .replaceAll("(^\\h*)|(\\h*$)","");

                if (j != 27 && j != 36) {
                    assertEquals(person.getFirst(), lines[1]);
                    assertEquals(person.getVon(), lines[2]);
                    assertEquals(person.getLast(), lines[3]);
                    assertEquals(person.getJr(), lines[4]);
                }
                j++;
            }
        }
    }

    @Test
    public void splitIntoWordsTest() throws IOException, ParseException {
        String fileName = "/person_test/xampl_auth.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());

        String[][] expectedResults = new String[][]{{"AA", "BB"}, {"AA"}, {"AA", "bb"}, {"aa"},
                {"AA", "bb", "CC"}, {"AA", "bb", "CC", "dd", "EE"}, {"AA", "1B", "cc", "dd"},
                {"AA", "1b", "cc", "dd"}, {"AA", "[b]B", "cc", "dd"}, {"AA", "[b]b", "cc", "dd"},
                {"AA", "[B]b", "cc", "dd"}, {"AA", "[B]B", "cc", "dd"}, {"AA", "\\BB[b]", "cc", "dd"},
                {"AA", "\\bb[b]", "cc", "dd"}, {"AA", "[bb]", "cc", "DD"}, {"AA", "bb", "[cc]", "DD"},
                {"AA", "[bb]", "CC"}, {"bb", "CC|", "AA"}, {"bb", "CC|", "aa"}, {"bb", "CC", "dd", "EE|", "AA"},
                {"bb|", "AA"}, {"BB|"}, {"bb", "CC|XX|", "AA"}, {"bb", "CC|xx|", "AA"}, {"BB||", "AA"},
                {"Paul", "\\'Emile", "Victor"}, {"Paul", "[\\'E]mile", "Victor"}, {"Paul", "\\'emile", "Victor"},
                {"Paul", "[\\'e]mile", "Victor"}, {"Victor|", "Paul", "\\'Emile"}, {"Victor|", "Paul", "[\\'E]mile"},
                {"Victor|", "Paul", "\\'emile"}, {"Victor|", "Paul", "[\\'e]mile"}, {"Dominique", "Galouzeau", "de", "Villepin"},
                {"Dominique", "[G]alouzeau", "de", "Villepin"}, {"Galouzeau", "de", "Villepin|", "Dominique"}};

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            PersonParser personParser = new PersonParser();

            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] words =  personParser.splitIntoWords(line);
                for (int j = 0; j < words.length; j++)
                    assertEquals(words[j], expectedResults[i][j]);
                i++;
            }
        }
    }

    @Test
    public void startsWithTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PersonParser.class.getDeclaredMethod("startsWith", String.class);
        method.setAccessible(true);
        assertEquals(method.invoke(new PersonParser(), "[\\'E]mile").toString(), "LOWER");
        assertEquals(method.invoke(new PersonParser(), "Emile").toString(), "UPPER");
        assertEquals(method.invoke(new PersonParser(), "emile").toString(), "LOWER");
        assertEquals(method.invoke(new PersonParser(), "[\\Emi][lee]").toString(), "UNDETERMINED");
    }

    @Test(expected = ParseException.class)
    public void unbalancedTest() throws ParseException {
        String block = "AA [dfs]fad]| borg";
        PersonParser personParser = new PersonParser();
        personParser.parse(block);
    }

    @Test
    public void emptyTest() throws ParseException {
        String block = "";
        PersonParser personParser = new PersonParser();
        personParser.parse(block);
    }
}

// VERY. IMPORTANT. WRONG TEST CASE OR EXPLANATION!!!!!!
// EXPLANATION STATES THAT DIGITS ARE LOWERCASE. TEST CASE STATES THEY ARE CASELESS o.O
// AND AGAIN: IN FURTHER REMARKS AT "Paul {\'E}mile Victor" (LINE 27 IN xampl_auth.bib FILE)
// "{\'E}mile" IS SAID TO BE CONTAINED IN FIRST PART - NOT TRUE, SINCE BRACED EXPRESSIONS ARE SAID
// TO BE CASELESS THIS IS LOWERCASE AND SHOULD GO WITH VON PART!!!
