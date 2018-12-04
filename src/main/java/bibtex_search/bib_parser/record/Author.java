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

    @Override
    public String toString() {
        if (!jr.equals("")) {
            if (!von.equals("")) {
                return String.format("%s %s, %s, %s", von, last, jr, first);
            } else {
                return String.format("%s, %s, %s", last, jr, first);
            }
        } else {
            if (!von.equals("")) {
                return String.format("%s %s %s", first, von, last);
            } else {
                return String.format("%s %s",first, last);
            }
        }
    }

    public String contentString() {
        return String.format("First: %s\nvon: %s\nLast: %s\njr: %s", first, von, last, jr);
    }
}
