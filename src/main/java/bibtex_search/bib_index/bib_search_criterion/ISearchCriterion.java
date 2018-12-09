package bibtex_search.bib_index.bib_search_criterion;

/**
 * Generic criterion used to search for particular IRecord instances. It generally applies
 * to only one field of a record
 */
public interface ISearchCriterion {
    /**
     *
     * @return name of the criterion. It should have an accompanying IFilter implementation
     * that should be returned by used IFilterFactory implementation.
     */
     String getName();
}
