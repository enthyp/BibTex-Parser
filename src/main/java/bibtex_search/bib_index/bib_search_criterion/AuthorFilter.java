package bibtex_search.bib_index.bib_search_criterion;

import bibtex_search.bib_index.ISearchResults;
import bibtex_search.bib_parser.record.IRecord;

import java.util.Set;

public class AuthorFilter extends IFilter {

    public AuthorFilter(Set<IRecord> records) {
        super(records);

    }

    @Override
    public ISearchResults apply(ISearchResults currentResults, ISearchCriterion criterion) {
        return null;
    }
}
