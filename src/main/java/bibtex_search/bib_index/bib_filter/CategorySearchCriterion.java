package bibtex_search.bib_index.bib_filter;

import bibtex_search.bib_parser.record.RecordType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CategorySearchCriterion implements ISearchCriterion {
    private List<RecordType> categories;

    private RecordType convert(String type) {
        try {
            return RecordType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException exc) {
            System.out.println("WARNING: an unknown category: " + type);
        }

        return null;
    }

    public CategorySearchCriterion(String[] categories) {
        this.categories = Arrays.stream(categories)
                .map(this::convert)
                .filter(Objects::nonNull)
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
