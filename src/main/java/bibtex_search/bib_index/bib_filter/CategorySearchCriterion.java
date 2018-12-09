package bibtex_search.bib_index.bib_filter;

import bibtex_search.bib_parser.record.RecordType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategorySearchCriterion implements ISearchCriterion {
    private List<RecordType> categories;

    public CategorySearchCriterion(List<String> categories) {
        this.categories = Arrays.stream(categories.toArray())
                .map(cat -> RecordType.valueOf((String)cat))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "categories";
    }

    public List<RecordType> getCategories() {
        return this.categories;
    }
}
