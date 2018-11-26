package bibtex_search.bib_parser.record;

import java.util.Map;

public class Record {
    private RecordType type;
    private String key;
    private Author author;
    private Map<String, String> fields;

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
        return super.toString();
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

    // TODO: implement equals() and hashCode() - necessary to keep these in a Set.
    // TODO: implement toString() - in a way specified by the instructor.

}
