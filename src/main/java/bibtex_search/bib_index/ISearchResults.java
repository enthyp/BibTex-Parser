package bibtex_search.bib_index;

/**
 * Results of a call to `IIndex.search` method.
 * May consist of entire records, individual fields
 * or whatever else that seems reasonable.
 */
public interface ISearchResults {
    /**
     * Prints it's contents to std output.
     */
    void show();
}
