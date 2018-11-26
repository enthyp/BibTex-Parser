package bibtex_search.bib_parser.record;

public class Author {
    private String first;
    private String last;
    private String von;
    private String jr;

    public Author(String first, String last, String von, String jr) {
        this.first = first;
        this.last = last;
        this.von = von;
        this.jr = jr;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String getVon() {
        return von;
    }

    public String getJr() {
        return jr;
    }
}
