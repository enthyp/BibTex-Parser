package bibtex_search.bib_parser;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RecordParserTest {

    @Test
    public void parseRecord() throws IOException, ParseException {
        String fileName = "/xampl_record.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                    .collect(Collectors.joining("\n"));

        RecordParser recordParser = new RecordParser(new HashMap<String, String>() {{
            put("VAR1", "wazzup ");
        }});
        System.out.println(recordParser.parseRecord("MISC", fileContent));
    }
}