package bibtex_search.bib_index.bib_search_criterion;

import bibtex_search.bib_parser.record.IRecord;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FilterFactory {

    public static IFilter getFilter(ISearchCriterion criterion, Set<IRecord> records) {
        if (filterChoice.containsKey(criterion.getName())) {
            try {
                Class<? extends IFilter> filterClazz = filterChoice.get(criterion.getName());
                Constructor<? extends IFilter> constr = filterClazz
                        .getConstructor(Set.class);
                return constr.newInstance(records);
                } catch (NoSuchMethodException | InstantiationException |
                    IllegalAccessException | InvocationTargetException exc) {
                System.out.println("WARNING: problem occurred building the filter!\n" + exc.getMessage());
            }
        }

        return null;
    }


    // TODO: implement hashCode and equals if using a custom object as key! (not here exactly)
    private static Map<String, Class<? extends IFilter>> filterChoice =
            new HashMap<String, Class<? extends IFilter>>() {{
        put("authors", AuthorFilter.class);
        put("categories", CategoryFilter.class);
    }};
}
