package bibtex_search.bib_parser;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

// TODO: use assertions.
// TODO: test lack of mandatory fields.
// TODO: test optional fields.
// TODO: test cross-references.
// TODO: test matching balanced blocks.
// TODO: test lacking commas, vertical bars, non-matching braces, braces outside quotation marks etc.

public class RecordParserTest {

    @Test
    public void correctRecordTest() throws IOException, ParseException {
        String fileName = "/record_test/correct_record.bib";
        String outputFileName = "/record_test/correct_record_out.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                    .collect(Collectors.joining("\n"));

        File outFile = new File(this.getClass().getResource(outputFileName).getFile());
        String expectedOutput = Files.lines(outFile.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        RecordParser recordParser = new RecordParser(new HashMap<String, String>() {{
            put("var1", "stuff ");
        }});

        recordParser.setLineBeginnings(fileContent, 1);
        String output = recordParser.parseRecord(fileContent).toString();
        assertEquals(output, expectedOutput);
    }

    @Test
    public void wrongCommasTest() throws IOException, ParseException {
        String fileName = "/record_test/wrong_commas.bib";
        String outputFileName = "/record_test/wrong_commas_out.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        File outFile = new File(this.getClass().getResource(outputFileName).getFile());
        String expectedOutput = Files.lines(outFile.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        RecordParser recordParser = new RecordParser(new HashMap<String, String>() {{
            put("var1", "stuff ");
        }});

        recordParser.setLineBeginnings(fileContent, 1);
        String output = recordParser.parseRecord(fileContent).toString();
        assertEquals(output, expectedOutput);
    }


    @Test
    public void lineBreakTest() throws IOException, ParseException {
        String fileName = "/record_test/line_break.bib";
        String outputFileName = "/record_test/line_break_out.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        File outFile = new File(this.getClass().getResource(outputFileName).getFile());
        String expectedOutput = Files.lines(outFile.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        RecordParser recordParser = new RecordParser(new HashMap<String, String>() {{
            put("var1", "stuff ");
            put("var2", "more stuff ");
        }});

        recordParser.setLineBeginnings(fileContent, 1);
        String output = recordParser.parseRecord(fileContent).toString();
        assertEquals(output, expectedOutput);
    }
}