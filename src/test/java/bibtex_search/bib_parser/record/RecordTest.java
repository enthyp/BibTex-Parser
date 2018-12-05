package bibtex_search.bib_parser.record;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class RecordTest {

    @Test
    public void toStringTest() {
        Person person = new Person("John", "Doe", "von", "Grossingen");

        Map<String, String> fields = new HashMap<>();
        fields.put("year", "1984");
        fields.put("title", "Bababoo");

        Record record = new Record(RecordType.BOOKLET, "bddd-gogo", person, fields);
        System.out.println(record);
    }
}