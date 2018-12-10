package bibtex_search.bib_index.bib_filter;

import bibtex_search.bib_parser.record.IRecord;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

// TODO: add tests - filters/categories correct, wrong, single, multiple, none.

/**
 * Factory class used to create Filter objects for given criteria and ISearchCriterion objects
 * for given criteria names.
 */
public class FilterFactory {

    public static Filter getFilter(ISearchCriterion criterion, Set<IRecord> records) {
        if (filterChoice.containsKey(criterion.getName())) {
            try {
                Class<? extends Filter> filterClazz = filterChoice.get(criterion.getName());
                Constructor<? extends Filter> constructor = filterClazz
                        .getConstructor(Set.class);
                return constructor.newInstance((Object)records);
                } catch (NoSuchMethodException | InstantiationException |
                    IllegalAccessException | InvocationTargetException exc) {
                System.out.println("WARNING: problem occurred building the filter!\n" + exc.getMessage());
            }
        }

        return null;
    }


    /**
     *
     * @param criteriaByNames a map from criterion name to a list of values for that criterion.
     * @return a list of found criteria - an empty one if no criteria were passed as arguments,
     * a NULL value if no criteria were recognized correctly.
     */
    public static List<ISearchCriterion> getCriteria(Map<String, String[]> criteriaByNames) {
        List<ISearchCriterion> criteria = new ArrayList<>();

        if (!criteriaByNames.isEmpty()) {
            for (Map.Entry<String, String[]> c : criteriaByNames.entrySet()) {
                String name = c.getKey();
                String[] values = c.getValue();

                if (criterionChoice.containsKey(name)) {
                    try {
                        Class<? extends ISearchCriterion> criterionClazz = criterionChoice.get(name);
                        Constructor<? extends ISearchCriterion> constructor = criterionClazz
                                .getConstructor(String[].class);
                        ISearchCriterion criterion = constructor.newInstance((Object) values);
                        criteria.add(criterion);
                    } catch (NoSuchMethodException | InstantiationException |
                            IllegalAccessException | InvocationTargetException exc) {
                        System.out.println("WARNING: problem occurred building the criterion!\n" + exc.getMessage());
                    }
                }
            }

            if (criteria.isEmpty()) {
                System.out.println("WARNING: no proper criterion was found!!!");
                return null;
            }
        }

        return criteria;
    }

    private static final Map<String, Class<? extends ISearchCriterion>> criterionChoice =
            new HashMap<String, Class<? extends ISearchCriterion>>() {{
                put("authors", AuthorSearchCriterion.class);
                put("categories", CategorySearchCriterion.class);
                put("years", YearSearchCriterion.class);
            }};

    private static final Map<String, Class<? extends Filter>> filterChoice =
            new HashMap<String, Class<? extends Filter>>() {{
        put("authors", AuthorFilter.class);
        put("categories", CategoryFilter.class);
        put("years", YearFilter.class);
    }};
}
