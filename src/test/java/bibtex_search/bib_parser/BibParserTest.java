package bibtex_search.bib_parser;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// TODO: use assertions.
// TODO: test case independence for categories.
// TODO: test general correctness - parsing entire file without additional criteria.

public class BibParserTest {
    @Test
    public void parseFileTest() throws IOException {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());

        BibParser bibParser = new BibParser();
        bibParser.parse(file);
    }

    @Test
    public void lineEndIndexTest() throws IOException {
        String fileName = "/xampl_record1.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());

        /* Read all of file contents to file. */
        String fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        Pattern lineEnds = Pattern.compile("\n");
        Matcher matcher = lineEnds.matcher(fileContent);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int cumSum = 0;
            while (matcher.find()) {
                String line = br.readLine();
                System.out.println(matcher.start() + " " + matcher.end());
                System.out.println(line + " ! " + (cumSum += line.length()) + "\n");
            }
        }

//        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//            br.skip(10347);
//            System.out.println(Character.toChars(br.read()));
//        }
    }
}