package bibtex_search.bib_parser.record;

import java.util.Map;
import java.util.Set;

/**
 * General representation of an entry in a .bib file.
 */
public interface IRecord {
    /**
     * Returns the category to which the record belongs.
     * @return category to which the record belongs, e.g. ARTICLE.
     */
    RecordType getType();

    /**
     * Returns key of the record.
     * @return key of the record (unique identifier in the file).
     */
    String getKey();

    /**
     * Returns a map between the category of a person and a collection of all such people.
     * @return a map between the category of a person (e.g. "editor", "author")
     * and a set of all people mentioned in the record who fall into that category.
     */
    Map<String, Set<Person>> getPeople();

    /**
     * Returns a map between the name of a field and it's value. It does not include
     * fields like "author" or "editor" as they are returned by `getPeople` method.
     * @return a map between the name of a field and it's value.
     */
    Map<String, String> getFields();

    /**
     * Returns keys of all cross-referenced records.
     * @return a set of keys of records cross-referenced in this record.
     */
    Set<String> getCrossRefs();
}
