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


public class BibParserTest {
    @Test
    public void parseFileTest() throws IOException {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());

        BibParser bibParser = new BibParser();
        bibParser.parse(file);
    }

    // TODO: error messages should pinpoint the fault exactly (line in file..?) (do 2nd)
    // IDEA: pass the line number or interval down to the parser - so that it can print out appropriate error
    // there should be a wrapper class for the return types or for arguments - so that we can either pass
    // up the information about a problem (a warning, not an exception) or pass down the information about
    // current position in the text - so that exact information can be printed out.

    @Test
    public void lineEndIndexTest() throws IOException {
        // TODO: use matcher.start(), matcher.end()
        String fileName = "/xampl_simplified.bib";
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

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.skip(10347);
            System.out.println(Character.toChars(br.read()));
        }
    }
}