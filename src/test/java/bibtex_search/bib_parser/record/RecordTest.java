package bibtex_search.bib_parser.record;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;

public class RecordTest {

    @Test
    public void toStringTest() {
        Author author = new Author("John", "Doe", "von", "Grossingen");

        Map<String, String> fields = new HashMap<>();
        fields.put("year", "1984");
        fields.put("title", "Bababoo");

        Record record = new Record(RecordType.BOOKLET, "bddd-gogo", author, fields);
        System.out.println(record);
    }
}