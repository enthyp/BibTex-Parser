package bibtex_search.bib_index;

import bibtex_search.bib_parser.record.IRecord;

import java.util.LinkedHashSet;
import java.util.Set;

public class SearchResults implements ISearchResults {

    private Set<IRecord> results;

    public SearchResults() {
    }

    public SearchResults(Set<IRecord> results) {
        this.results = results;
    }

    public void addResult(IRecord record) {
        if (results != null) {
            results.add(record);
        } else {
            results = new LinkedHashSet<IRecord>() {{
                add(record);
            }};
        }
    }

    @Override
    public void show() {
        for (IRecord record : results)
            System.out.println(record);
    }
}
