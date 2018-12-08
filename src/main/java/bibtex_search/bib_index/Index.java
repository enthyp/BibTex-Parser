package bibtex_search.bib_index;

import bibtex_search.bib_index.bib_search_criterion.IFilter;
import bibtex_search.bib_index.bib_search_criterion.ISearchCriterion;
import bibtex_search.bib_parser.record.IRecord;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This class allows to search through given set of IRecord instances based on
 * passed search criteria.
 */
public class Index implements IIndex {

    private LinkedHashSet<IRecord> records;

    private Set<IFilter> filters;

    @Override
    public void build(Set<IRecord> records) {
        this.records = (LinkedHashSet<IRecord>)records;
    }

    @Override
    public ISearchResults search(List<ISearchCriterion> criteria) {

    }
}
