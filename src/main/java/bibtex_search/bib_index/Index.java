package bibtex_search.bib_index;

import bibtex_search.bib_index.bib_filter.BaseSearchCriterion;
import bibtex_search.bib_index.bib_filter.FilterFactory;
import bibtex_search.bib_index.bib_filter.Filter;
import bibtex_search.bib_parser.record.IRecord;

import java.util.*;

/**
 * This class allows to search through given set of IRecord instances based on
 * passed search criteria.
 */
public class Index implements IIndex {

    /**
     * A map between record keys and records themselves.
     */
    private Map<String, IRecord> keyToRecord = new LinkedHashMap<>();

    /**
     * A map between search criteria and filters that can apply them.
     */
    private Map<BaseSearchCriterion, Filter> filters = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void build(Set<IRecord> records) {
        for (IRecord record: records)
            keyToRecord.put(record.getKey(), record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISearchResults search(List<BaseSearchCriterion> criteria) {
        /* No criterion from command line arguments was recognized. */
        if (criteria == null)
            return null;

        ISearchResults searchResults = new SearchResults(keyToRecord.keySet());

        for (BaseSearchCriterion criterion : criteria) {
            Filter filter;
            if (!filters.containsKey(criterion)) {
                /* Build the filter if it was not accessed before. */
                filter = FilterFactory.getFilter(criterion, new HashSet<>(this.keyToRecord.values()));
                if (filter != null) {
                    filters.put(criterion, filter);
                }
            } else {
                filter  = filters.get(criterion);
            }

            if (filter != null) {
                /* A filter applicable to given criterion was found. */
                searchResults = filter.apply(searchResults, criterion);
            }
        }

        return searchResults;
    }

    public IRecord getRecord(String key) {
        return this.keyToRecord.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(ISearchResults keys) {
        if (keys != null) {
            for (String key : keys.getResultKeys()) {
                // This if is not necessary by default.
                if (keyToRecord.containsKey(key)) {
                    System.out.println(keyToRecord.get(key));
                }
            }
        }
    }
}
