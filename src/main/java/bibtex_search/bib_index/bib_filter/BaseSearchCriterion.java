package bibtex_search.bib_index.bib_filter;

/**
 * Generic criterion used to search for particular IRecord instances. It describes
 * a specific property of IRecord instances, e.g. the category of an entry.
 */
public abstract class BaseSearchCriterion {



    /**
     * Returns the name of the criterion.
     * @return name of the criterion. It must have an accompanying Filter implementation
     * that is to be returned by used IFilterFactory implementation.
     */
     public abstract String getName();
}
