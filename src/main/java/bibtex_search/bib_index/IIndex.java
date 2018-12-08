package bibtex_search.bib_index;

import bibtex_search.bib_index.bib_search_criterion.ISearchCriterion;
import bibtex_search.bib_parser.record.IRecord;

import java.util.List;
import java.util.Set;

/**
 * A data structure that allows searching through given IRecord instances
 * based on passed criteria.
 */
public interface IIndex {

    /**
     *
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
    ISearchResults search(List<ISearchCriterion> criteria);
}
