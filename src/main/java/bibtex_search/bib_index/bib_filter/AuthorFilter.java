package bibtex_search.bib_index.bib_filter;

import bibtex_search.bib_index.ISearchResults;
import bibtex_search.bib_index.SearchResults;
import bibtex_search.bib_parser.record.IRecord;
import bibtex_search.bib_parser.record.Person;

import java.util.*;

public class AuthorFilter extends Filter {

    /**
     * A map from keys of records to sets of authors' last names. If there are no authors,
     * then there is no map entry.
     */
    private Map<String, Set<String>> keyToAuthors;

    /**
     * A map from keys of records to records that they cross-reference. If there is no
     * cross-reference, then there is no map entry.
     */
    private Map<String, String> keyToCrossRef;

    public AuthorFilter(Set<IRecord> records) {
        super(records);
    }

    @Override
    protected void setup() {
        this.keyToAuthors = new HashMap<>();
        this.keyToCrossRef = new HashMap<>();
    }

    @Override
    protected void addRecord(IRecord record) {
        String key = record.getKey();

        /* Get all authors mentioned in the record. */
        Map<String, Set<Person>> authorsMap = record.getPeople();

        if (!authorsMap.isEmpty() && authorsMap.containsKey("author")) {
            /* Put authors' last names into the map. */
            Set<Person> authors = authorsMap.get("author");
            Set<String> lastNames = new HashSet<>();

            for (Person author : authors) {
                String lastName = author.getLast();
                lastNames.add(lastName);
            }

            this.keyToAuthors.put(key, lastNames);
        }

        /* Add the cross-reference. */
        this.keyToCrossRef.put(key, record.getCrossRef());
    }

    @Override
    public ISearchResults apply(ISearchResults currentResults, BaseSearchCriterion criterion) {
        if (criterion instanceof AuthorSearchCriterion && currentResults instanceof SearchResults) {
            /* Get all authors we are looking for. */
            Set<String> lastNames = new HashSet<>(((AuthorSearchCriterion)criterion).getAuthors());

            /* Get all records that either have all specified authors or reference a record
             * that has them all.
             */
            Set<String> newResults = new HashSet<>();

            for (String key : currentResults.getResultKeys()) {
                if (this.keyToAuthors.containsKey(key)) {
                    Set<String> recordsLastNames = new HashSet<>(this.keyToAuthors.get(key));
                    recordsLastNames.retainAll(lastNames);

                    if (!recordsLastNames.isEmpty()) {
                        newResults.add(key);
                    }
                } else {
                    String crossRef = this.keyToCrossRef.get(key);

                    if (crossRef != null && this.keyToAuthors.containsKey(crossRef)) {
                        Set<String> crossRefLastNames = new HashSet<>(this.keyToAuthors.get(crossRef));

                        crossRefLastNames.retainAll(lastNames);
                        if (!crossRefLastNames.isEmpty()) {
                            newResults.add(key);
                        }
                    }
                }
            }

            return new SearchResults(newResults);
        }

        return null;
    }
}
