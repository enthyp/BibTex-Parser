package bibtex_search.bib_index;

import bibtex_search.bib_index.bib_filter.FilterFactory;
import bibtex_search.bib_index.bib_filter.IFilter;
import bibtex_search.bib_index.bib_filter.ISearchCriterion;
import bibtex_search.bib_parser.record.IRecord;

import java.util.*;

/**
 * This class allows to search through given set of IRecord instances based on
 * passed search criteria.
 */
public class Index implements IIndex {

    private Map<String, IRecord> keyToRecord = new LinkedHashMap<>();
    private Map<ISearchCriterion, IFilter> filters = new HashMap<>();

    @Override
    public void build(Set<IRecord> records) {
        for (IRecord record: records)
            keyToRecord.put(record.getKey(), record);
    }

    @Override
    public ISearchResults search(List<ISearchCriterion> criteria) {
        ISearchResults searchResults = new SearchResults(keyToRecord.keySet());

        for (ISearchCriterion criterion : criteria) {
            IFilter filter;
            if (!filters.containsKey(criterion)) {
                /* Build the filter if it was not accessed before. */
                filter = FilterFactory.getFilter(criterion, (Set<IRecord>)this.keyToRecord.values());
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

    @Override
    public void show(ISearchResults keys) {
        for (String key : keys.getResultKeys()) {
            // This if is not necessary by default.
            if (keyToRecord.containsKey(key)) {
                System.out.println(keyToRecord.get(key));
            }
        }
    }


}
