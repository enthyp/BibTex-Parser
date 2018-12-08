package bibtex_search.bib_index.bib_search_criterion;

import bibtex_search.bib_parser.record.RecordType;

import java.util.List;

public class CategorySearchCriterion implements ISearchCriterion {
    private List<RecordType> categories;

    public CategorySearchCriterion(List<RecordType> categories) {
        this.categories = categories;
    }

    public List<RecordType> getCategories() {
        return this.categories;
    }
}
