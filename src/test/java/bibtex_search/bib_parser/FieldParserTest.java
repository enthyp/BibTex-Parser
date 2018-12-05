package bibtex_search.bib_parser;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FieldParserTest {

    @Test
    public void parseTest() throws IOException, ParseException {
        String fileName = "/xampl_var.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        FieldParser fieldParser = new FieldParser(new HashMap<String, String>() {{
            put("var1", "mofo as fuck");
        }});
        System.out.println(fieldParser.parse(new ParseBlock(0, 0, fileContent)));
    }
}