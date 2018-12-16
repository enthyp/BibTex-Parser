package bibtex_search.bib_index.bib_filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class YearSearchCriterion extends BaseSearchCriterion {

    private Set<String> years;

    public YearSearchCriterion(String[] years) {
        this.years = new HashSet<>(Arrays.asList(years));
    }

    @Override
    public String getName() {
        return "years";
    }

    public Set<String> getYears() {
        return years;
    }
}
