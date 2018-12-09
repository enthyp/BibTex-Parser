package bibtex_search.bib_index.bib_filter;

import bibtex_search.bib_index.ISearchResults;
import bibtex_search.bib_index.SearchResults;
import bibtex_search.bib_parser.record.IRecord;
import bibtex_search.bib_parser.record.Person;

import java.util.*;

public class AuthorFilter extends IFilter {

    /**
     * A map from author's last names to keys of appropriate records.
     */
    private Map<String, Set<String>> authorToKey = new HashMap<>();

    public AuthorFilter(Set<IRecord> records) {
        super(records);
        for (IRecord record : records) {
            String key = record.getKey();

            /* Get all authors mentioned in the record. */
            Map<String, Set<Person>> authorsMap = record.getPeople();

            if (!authorsMap.isEmpty() && authorsMap.containsKey("author")) {
                Set<Person> authors = authorsMap.get("author");

                for (Person author : authors) {
                    String lastName = author.getLast();
                    /* Put the authors' last names and corresponding record keys into the map. */
                    if (!authorToKey.containsKey(lastName))
                        authorToKey.put(lastName, new HashSet<>());
                    authorToKey.get(lastName).add(key);
                }
            }
        }
    }

    @Override
    public ISearchResults apply(ISearchResults currentResults, ISearchCriterion criterion) {
        if (criterion instanceof AuthorSearchCriterion && currentResults instanceof SearchResults) {
            /* Get all keys for all records that contain specified authors. */
            Set<String> authorKeys = new HashSet<>();
            for (String lastName : ((AuthorSearchCriterion) criterion).getAuthors()) {
                if (authorToKey.containsKey(lastName)) {
                    authorKeys.addAll(authorToKey.get(lastName));
                }
            }

            /* Remove all the records that do not contain specified authors. */
            Set<String> currentKeys = currentResults.getResultKeys();
            currentKeys.retainAll(authorKeys);

            return new SearchResults(currentKeys);
        }

        return null;
    }
}
