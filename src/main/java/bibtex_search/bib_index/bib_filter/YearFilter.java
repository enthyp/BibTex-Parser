package bibtex_search.bib_index.bib_filter;

import bibtex_search.bib_index.ISearchResults;
import bibtex_search.bib_index.SearchResults;
import bibtex_search.bib_parser.record.IRecord;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class YearFilter extends Filter {

    private Map<String, Set<String>> yearToKey;

    private Map<String, String> keyToCrossRef;

    public YearFilter(Set<IRecord> records) {
        super(records);
    }

    @Override
    protected void setup() {
        this.yearToKey = new HashMap<>();
        this.keyToCrossRef = new HashMap<>();
    }

    @Override
    protected void addRecord(IRecord record) {
        if (record.getFields().containsKey("year")) {
            String key = record.getKey();
            String year = record.getFields().get("year");

            if (!yearToKey.containsKey(year))
                yearToKey.put(year, new HashSet<>());
            yearToKey.get(year).add(key);
        }

        keyToCrossRef.put(record.getKey(), record.getCrossRef());
    }

    @Override
    public ISearchResults apply(ISearchResults currentResults, BaseSearchCriterion criterion) {
        if (criterion instanceof YearSearchCriterion && currentResults instanceof SearchResults) {
            /* Get keys for all records from specified years. */
            Set<String> yearKeys = new HashSet<>();
            for (String year : ((YearSearchCriterion) criterion).getYears()) {
                if (yearToKey.containsKey(year)) {
                    yearKeys.addAll(yearToKey.get(year));
                }
            }

            /* Also add keys of all records that cross-reference the above. */
            for (String key : currentResults.getResultKeys()) {
                String crossRef = this.keyToCrossRef.get(key);

                if (crossRef != null && yearKeys.contains(crossRef))
                    yearKeys.add(key);
            }

            /* Remove all the records from other years. */
            Set<String> currentKeys = currentResults.getResultKeys();
            currentKeys.retainAll(yearKeys);

            return new SearchResults(currentKeys);
        }

        return null;
    }
}
