package bibtex_search.bib_parser.record;

import java.util.*;

public class Record implements IRecord {
    private RecordType type;
    private String key;
    private Map<String, Set<Person>> people;
    private Map<String, String> fields;

    public Record(RecordType type, String key, Map<String, Set<Person>> people, Map<String, String> fields) {
        this.type = type;
        this.key = key;
        this.people = people;
        this.fields = fields;
    }

    public Record(RecordType type, String key, Person person, Map<String, String> fields) {
        this.type = type;
        this.key = key;
        this.people = new LinkedHashMap<String, Set<Person>>() {{
            put("author", new LinkedHashSet<Person>() {{
                add(person);
            }});
        }};
        this.fields = fields;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Record))
            return false;

        if (obj == this)
            return true;

        return key.equals(((Record)obj).key);
    }

    @Override
    public String toString() {
        /* Ugly. */
        int width_left = 40;
        int width_right = 75;
        int width_total = 120;

        StringBuilder output = new StringBuilder();

        output.append("RECORD:\n");
        /* 1st row. */
        for (int i = 0; i < width_total; i++)
            output.append("*");
        output.append("\n* ");

        output.append(String.format("%1$-" + (width_total - 3) + "s*\n", type.name() + " (" + key + ")"));

        for (int i = 0; i < width_total; i++)
            output.append("*");
        output.append("\n");

        /* People rows. */
        if (people != null) {
            for (Map.Entry<String, Set<Person>> peopleGroup: people.entrySet()) {
                Iterator<Person> it = peopleGroup.getValue().iterator();
                Person person = it.next();
                output.append(String.format("* %1$-" + width_left + "s* ", peopleGroup.getKey()));
                output.append(String.format("%1$-" + width_right + "s*\n", person));

                while (it.hasNext()) {
                    Person nextPerson = it.next();
                    output.append(String.format("* %1$-" + width_left + "s* ", ""));
                    output.append(String.format("%1$-" + width_right + "s*\n", nextPerson));
                }

                for (int i = 0; i < width_total; i++)
                    output.append("*");
                output.append("\n");
            }
        }

        /* Next rows. */
        for (Map.Entry<String, String> entry: fields.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            output.append(String.format("* %1$-" + width_left + "s* ", name));

            /* In case of values too long for current printing format. */
            String[] valueWords = value.split("\\s+");
            for (int i = 0; i < valueWords.length;) {
                int j = i;
                StringBuilder lineBuilder = new StringBuilder();

                while (i < valueWords.length && lineBuilder.length() + valueWords[i].length() + 1 < width_right - 5) {
                    lineBuilder.append(valueWords[i]).append(" ");
                    i++;
                }

                /* Just in an unlikely case of at least 70 consequent characters - skip it. */
                if (i > j) {
                    if (j == 0)
                        output.append(String.format("%1$-" + width_right + "s*\n", lineBuilder.toString()));
                    else {
                        output.append(String.format("* %1$-" + width_left + "s* ", ""));
                        output.append(String.format("%1$-" + width_right + "s*\n", lineBuilder.toString()));
                    }
                } else {
                    i++;
                }
            }

            for (int i = 0; i < width_total; i++)
                output.append("*");
            output.append("\n");
        }

        output.append("\n");

        return output.toString();
    }

    public RecordType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public Map<String, Set<Person>> getPeople() {
        return people;
    }

    @Override
    public Map<String, String> getFields() {
        return this.fields;
    }
}
