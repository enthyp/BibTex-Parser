package bibtex_search.bib_index;

import bibtex_search.bib_index.bib_filter.BaseSearchCriterion;
import bibtex_search.bib_parser.record.IRecord;

import java.util.List;
import java.util.Set;

/**
 * A data structure that allows searching through given IRecord instances
 * based on passed criteria.
 */
public interface IIndex {

    /**
     * This method does all the preprocessing necessary for efficient searching.
     * @param records that should be searched through
     */
    void build(Set<IRecord> records);

    /**
     * This method iterates over given criteria and applies them
     *  (ones recognized by a particular implementation) to obtain
     *  some results.
     *
     * @param criteria of the search
     * @return search results
     */
    ISearchResults search(List<BaseSearchCriterion> criteria);

    /**
     * This method prints all the records with given keys to std out.
     * @param keys of the records to be printed.
     */
    void show(ISearchResults keys);
}
