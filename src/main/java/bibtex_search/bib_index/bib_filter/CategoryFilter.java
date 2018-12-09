package bibtex_search.bib_index.bib_filter;

import bibtex_search.bib_index.ISearchResults;
import bibtex_search.bib_index.SearchResults;
import bibtex_search.bib_parser.record.IRecord;
import bibtex_search.bib_parser.record.RecordType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CategoryFilter extends IFilter {

    /**
     * A map from categories to keys of appropriate records.
     */
    private Map<RecordType, Set<String>> categoryToKey = new HashMap<>();

    public CategoryFilter(Set<IRecord> records) {
        super(records);

        for (IRecord record : records) {
            String key = record.getKey();
            RecordType type = record.getType();

            if (!categoryToKey.containsKey(type))
                categoryToKey.put(type, new HashSet<>());
            categoryToKey.get(type).add(key);
        }
    }

    @Override
    public ISearchResults apply(ISearchResults currentResults, ISearchCriterion criterion) {
        if (criterion instanceof CategorySearchCriterion && currentResults instanceof SearchResults) {
            /* Get keys for all records of specified categories. */
            Set<String> categoryKeys = new HashSet<>();
            for (RecordType type : ((CategorySearchCriterion) criterion).getCategories()) {
                if (categoryToKey.containsKey(type)) {
                    categoryKeys.addAll(categoryToKey.get(type));
                }
            }

            /* Remove all the records of different categories. */
            Set<String> currentKeys = currentResults.getResultKeys();
            currentKeys.retainAll(categoryKeys);

            return new SearchResults(currentKeys);
        }

        return null;
    }
}
