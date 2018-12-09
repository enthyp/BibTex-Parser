package bibtex_search.bib_parser.record;

import java.util.Map;
import java.util.Set;

public interface IRecord {
    RecordType getType();
    String getKey();
    Map<String, Set<Person>> getPeople();
}
