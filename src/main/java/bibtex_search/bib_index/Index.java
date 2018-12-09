package bibtex_search.bib_index;

import bibtex_search.bib_index.bib_search_criterion.FilterFactory;
import bibtex_search.bib_index.bib_search_criterion.IFilter;
import bibtex_search.bib_index.bib_search_criterion.ISearchCriterion;
import bibtex_search.bib_parser.record.IRecord;

import java.util.*;

/**
 * This class allows to search through given set of IRecord instances based on
 * passed search criteria.
 */
public class Index implements IIndex {

    private LinkedHashSet<IRecord> records;
    private Map<ISearchCriterion, IFilter> filters = new HashMap<>();

    @Override
    public void build(Set<IRecord> records) {
        this.records = (LinkedHashSet<IRecord>)records;
    }

    @Override
    public ISearchResults search(List<ISearchCriterion> criteria) {
        ISearchResults searchResults = new SearchResults(this.records);

        for (ISearchCriterion criterion : criteria) {
            IFilter filter = null;
            if (!filters.containsKey(criterion)) {
                /* Build the filter if it was not accessed before. */
                filter = FilterFactory.getFilter(criterion, this.records);
                if (filter != null) {
                    filters.put(criterion, filter);
                }
            }

            if (filter != null) {
                /* A filter applicable to given criterion was found. */
                searchResults = filter.apply(searchResults, criterion);
            }
        }

        return searchResults;
    }
}
