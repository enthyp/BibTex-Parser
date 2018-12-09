package bibtex_search.bib_index.bib_filter;

import bibtex_search.bib_index.ISearchResults;
import bibtex_search.bib_parser.record.IRecord;

import java.util.Set;

/**
 * A filter object used to retain only matching IRecord instances. It is specific for a set
 * of IRecord instances and for a type of search criterion, e.g. search by author's last name.
 *  However, it is not specific for a single value of the criterion. Implementations should
 *  keep data structures that allow for efficient examination of IRecord instances (see examples).
 */
public abstract class Filter {

    /**
     * Upon construction some data structure can be created to allow for faster
     * filtering later on, even when the criteria change.
     * @param records records for which data structure is built
     */
    public Filter(Set<IRecord> records) {}

    /**
     *
     * @param currentResults what is to be filtered
     * @param criterion specific
     * @return a new ISearchResults object, containing only the results
     * that passed through the filter.
     */
    public abstract ISearchResults apply(ISearchResults currentResults, ISearchCriterion criterion);
}
