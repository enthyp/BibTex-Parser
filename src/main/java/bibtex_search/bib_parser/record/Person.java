package bibtex_search.bib_parser.record;

/**
 * An object representation of a person mentioned in a record, e.g. of an author or editor.
 */
public class Person {
    /**
     * Type of the person (an author by default).
     */
    private String type = "author";

    /**
     * First name of a person in accordance with BibTeX standard.
     */
    private String first;

    /**
     * Last name of a person in accordance with BibTeX standard.
     */
    private String last;

    /**
     * 'von' part of a person's name in accordance with BibTeX standard.
     */
    private String von;

    /**
     * 'jr' part of person's name in accordance with BibTeX standard.
     */
    private String jr;

    public Person(String first, String last, String von, String jr) {
        this.first = first;
        this.last = last;
        this.von = von;
        this.jr = jr;
    }

    /**
     * Returns person's first name in accordance with BibTeX standard.
     * @return person's first name in accordance with BibTeX standard.
     */
    public String getFirst() {
        return first;
    }

    /**
     * Returns person's last name in accordance with BibTeX standard.
     * @return person's last name in accordance with BibTeX standard.
     */
    public String getLast() {
        return last;
    }

    /**
     * Returns the 'von' part of person's name in accordance with BibTeX standard.
     * @return the 'von' part of person's name in accordance with BibTeX standard.
     */
    public String getVon() {
        return von;
    }

    /**
     * Returns the 'jr' part of person's name in accordance with BibTeX standard.
     * @return the 'jr' part of person's name in accordance with BibTeX standard.
     */
    public String getJr() {
        return jr;
    }

    /**
     * Returns the type of a person.
     * @return the type of a person.
     */
    public String getType() { return type; }

    /**
     * Sets the type of a person.
     * @param type type of the person, e.g. "author".
     */
    public void setType(String type) { this.type = type; }

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

    // TODO: remove, use proper tests.
    public String contentString() {
        return String.format("First: %s\nvon: %s\nLast: %s\njr: %s", first, von, last, jr);
    }
}
