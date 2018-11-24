package bibtex_search.bib_parser;

import java.io.*;
import java.util.ArrayList;

public class BibParser {

    // TODO: must implement it WAY smarter!
    private ArrayList<Record> records = new ArrayList<>();

    public BibParser() {}

    /**
     * Wrapper method provided to add testability of actual parsing.
     * @param filePath absolute path to .bib file
     */
    public void parse(String filePath) throws IOException {
        File file = getFile(filePath);
        parseFile(file);
    }

    public ArrayList<Record> getRecords() {
        return this.records;
    }

    /* -------------------------------------------------------------- */

    public void parseFile(File file) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String line = fileReader.readLine();
        System.out.println(line);
    }

    /**
     *
     * @param filePath path to .bib file
     * @return corresponding File object
     */
    private File getFile(String filePath) {
        return new File(filePath);
    }
}
