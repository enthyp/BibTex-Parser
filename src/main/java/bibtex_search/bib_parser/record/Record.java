package bibtex_search.bib_parser.record;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class Record {
    private RecordType type;
    private String key;
    private HashSet<Author> authors;
    private Map<String, String> fields;

    public Record(RecordType type, String key, HashSet<Author> authors, Map<String, String> fields) {
        this.type = type;
        this.key = key;
        this.authors = authors;
        this.fields = fields;
    }

    public Record(RecordType type, String key, Author author, Map<String, String> fields) {
        this.type = type;
        this.key = key;
        this.authors = new HashSet<>();
        this.authors.add(author);
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

        /* Author rows. */
        if (authors != null) {
            Iterator<Author> it = authors.iterator();
            output.append(String.format("* %1$-" + width_left + "s* ", "author"));
            output.append(String.format("%1$-" + width_right + "s*\n", it.next()));

            while (it.hasNext()) {
                Author nextAuthor = it.next();
                output.append(String.format("* %1$-" + width_left + "s* ", ""));
                output.append(String.format("%1$-" + width_right + "s*\n", nextAuthor));
            }

            for (int i = 0; i < width_total; i++)
                output.append("*");
            output.append("\n");
        }

        /* Next rows. */
        for (Map.Entry<String, String> entry: fields.entrySet()) {
            output.append(String.format("* %1$-" + width_left + "s* ", entry.getKey()));
            output.append(String.format("%1$-" + width_right + "s*\n", entry.getValue()));

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

    public void setType(RecordType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HashSet<Author> getAuthor() {
        return authors;
    }

    public void setAuthor(HashSet<Author> authors) {
        this.authors = authors;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public void addField(String name, String value) {
        fields.put(name, value);
    }
}
