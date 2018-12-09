package bibtex_search.bib_index.bib_filter;

/**
 * Generic criterion used to search for particular IRecord instances. It describes
 * a specific property of IRecord instances, e.g. the type of IRecord instance.
 */
public interface ISearchCriterion {

    /**
     *
     * @return name of the criterion. It must have an accompanying Filter implementation
     * that is to be returned by used IFilterFactory implementation.
     */
     String getName();
}
