package bibtex_search.bib_parser.record;

import java.util.Map;

public class Record {
    private RecordType type;
    private String key;
    private Author author;
    private Map<String, String> fields;

    public Record(RecordType type, String key, Author author, Map<String, String> fields) {
        this.type = type;
        this.key = key;
        this.author = author;
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

        /* Author row. */
        if (author != null) {
            output.append(String.format("* %1$-" + width_left + "s* ", "author"));
            output.append(String.format("%1$-" + width_right + "s*\n", author));

            for (int i = 0; i < width_total; i++)
                output.append("*");
            output.append("\n");
        }

        /* Next rows. */
        // TODO: authors must be done separately.
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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
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
