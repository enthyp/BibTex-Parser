package bibtex_search.bib_index.bib_search_criterion;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaFactory {
    public static List<ISearchCriterion> getCriteria(Map<String, String[]> criteriaByNames) {
        List<ISearchCriterion> criteria = new ArrayList<>();

        for (Map.Entry<String, String[]> crit : criteriaByNames.entrySet()) {
            String name = crit.getKey();
            String[] values = crit.getValue();

            if (criterionChoice.containsKey(name)) {
                try {
                    Class<? extends ISearchCriterion> criterionClazz = criterionChoice.get(name);
                    Constructor<? extends ISearchCriterion> constr = criterionClazz
                            .getConstructor(String[].class);
                    ISearchCriterion criterion = constr.newInstance(new Object[] {values});
                    criteria.add(criterion);
                } catch (NoSuchMethodException | InstantiationException |
                        IllegalAccessException | InvocationTargetException exc) {
                    System.out.println("WARNING: problem occurred building the criterion!\n" + exc.getMessage());
                }
            }
        }

        return criteria;
    }

    private static final Map<String, Class<? extends ISearchCriterion>> criterionChoice =
            new HashMap<String, Class<? extends ISearchCriterion>>() {{
        put("authors", AuthorSearchCriterion.class);
        put("categories", CategorySearchCriterion.class);
    }};
}
