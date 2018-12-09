package bibtex_search.bib_index.bib_search_criterion;

import bibtex_search.bib_index.ISearchResults;
import bibtex_search.bib_parser.record.IRecord;

import java.util.Set;

/**
 * A filter object used to retain only matching IRecord instances. It is specific for a set
 * of IRecord instances and for a type of search criterion, e.g. search by author's last name.
 *  However, it is not specific for a single value of the criterion. Implementations should
 *  keep data structures that allow for efficient examination of IRecord instances.
 */
public abstract class IFilter {

    public IFilter(Set<IRecord> records) {

    }

    /**
     *
     * @param currentResults what is to be filtered
     * @param criterion specific
     * @return a new ISearchResults object, containing only the results
     * that passed through the filter.
     */
    public abstract ISearchResults apply(ISearchResults currentResults, ISearchCriterion criterion);
}
