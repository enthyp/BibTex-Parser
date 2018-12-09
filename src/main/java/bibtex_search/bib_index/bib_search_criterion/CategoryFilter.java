package bibtex_search.bib_index.bib_search_criterion;

import bibtex_search.bib_index.ISearchResults;
import bibtex_search.bib_parser.record.IRecord;

import java.util.Set;

public class CategoryFilter extends IFilter {
    public CategoryFilter(Set<IRecord> records) {
        super(records);
    }

    @Override
    public ISearchResults apply(ISearchResults currentResults, ISearchCriterion criterion) {
        return null;
    }
}
