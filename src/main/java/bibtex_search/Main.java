package bibtex_search;

import bibtex_search.bib_index.IIndex;
import bibtex_search.bib_index.Index;
import bibtex_search.bib_parser.BibParser;
import bibtex_search.bib_parser.IBibParser;
import bibtex_search.bib_parser.record.IRecord;
import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        try {
            /* Parse arguments. */
            ICLIArgParser argParser = new CLIArgParser();
            argParser.parseArgs(args);

            IBibParser bibParser = new BibParser();
            try {
                /* Parse the input file. */
                bibParser.parse(argParser.getBibFilePath());

                IIndex index = new Index();
                try {
                    Set<IRecord> foundRecords = bibParser.getRecords();
                    index.build(foundRecords);

                    /* Search the file given the criteria. */
                    index.search();
                } catch (Exception exc) {
                    System.out.println(exc.getMessage());
                }
            } catch (FileNotFoundException exc) {
                System.out.println("File not found: " + exc.getMessage());
            } catch(IOException exc) {
                System.out.println("IO exception occured: " + exc.getMessage());
            }
        } catch (ParseException exc) {
            System.out.println(exc.getMessage());
        }
    }
}
