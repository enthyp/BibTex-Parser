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
    public void parseRecord() throws IOException, ParseException {
        String fileName = "/xampl_record.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                    .collect(Collectors.joining("\n"));

        RecordParser recordParser = new RecordParser(new HashMap<String, String>() {{
            put("var1", "stuff ");
        }});

        recordParser.setLineBeginnings(fileContent, 1);

        System.out.println(recordParser.parseRecord(fileContent));
    }
}