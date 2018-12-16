package bibtex_search.bib_index.bib_filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuthorSearchCriterion extends BaseSearchCriterion {
    /**
     * Authors' last names
     */
    private List<String> authors;

    public AuthorSearchCriterion(String[] authors) {
        this.authors = new ArrayList<>(Arrays.asList(authors));
    }

    @Override
    public String getName() {
        return "authors";
    }

    public List<String> getAuthors() {
        return authors;
    }
}
