package bibtex_search.bib_index;

import java.util.Set;

/**
 * Results of a call to `IIndex.search` method.
 * May consist of entire records, individual fields
 * or whatever else that seems reasonable.
 */
public interface ISearchResults {
    Set<String> getResultKeys();
}
