package bibtex_search.bib_index;

import bibtex_search.bib_parser.record.IRecord;

import java.util.Set;

public class SearchResults implements ISearchResults {

    private Set<IRecord> results;

    @Override
    public void show() {
        for (IRecord record : results)
            System.out.println(record);
    }
}
