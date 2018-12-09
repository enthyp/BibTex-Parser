package bibtex_search.bib_index;

import bibtex_search.bib_parser.record.IRecord;

import java.util.LinkedHashSet;
import java.util.Set;

public class SearchResults implements ISearchResults {

    private Set<String> resultKeys;

    public SearchResults(Set<String> resultKeys) {
        this.resultKeys = resultKeys;
    }

    public void addResult(IRecord record) {
        if (resultKeys != null) {
            resultKeys.add(record.getKey());
        } else {
            resultKeys = new LinkedHashSet<String>() {{
                add(record.getKey());
            }};
        }
    }

    public Set<String> getResultKeys() { return this.resultKeys; }
}
