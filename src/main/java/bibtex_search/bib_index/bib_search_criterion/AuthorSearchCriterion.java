package bibtex_search.bib_index.bib_search_criterion;

import java.util.List;

public class AuthorSearchCriterion implements ISearchCriterion {
    /**
     * Authors' last names
     */
    private List<String> authors;

    public AuthorSearchCriterion(List<String> authors) {
        this.authors = authors;
    }

    @Override
    public String getName() {
        return "authors";
    }

    public List<String> getAuthors() {
        return authors;
    }
}
