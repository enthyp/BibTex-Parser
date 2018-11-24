package bibtex_search.bib_db;

import bibtex_search.bib_parser.Record;

import java.util.ArrayList;

public class BibDB {

    // TODO: must implement it WAY smarter!
    private ArrayList<Record> records;

    public BibDB(ArrayList<Record> records) {
        this.records = records;
    }
}
