package bibtex_search;

import bibtex_search.bib_index.IIndex;
import bibtex_search.bib_index.ISearchResults;
import bibtex_search.bib_index.Index;
import bibtex_search.bib_index.bib_filter.CriteriaFactory;
import bibtex_search.bib_index.bib_filter.ISearchCriterion;
import bibtex_search.bib_parser.BibParser;
import bibtex_search.bib_parser.IBibParser;
import bibtex_search.bib_parser.record.IRecord;
import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Main {
    // Sun right window - approx. 10:40
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
                Set<IRecord> foundRecords = bibParser.getRecords();
                index.build(foundRecords);
                // TODO: test the Index. Heavily.
                /* Get the criteria and search the file accordingly. */
                List<ISearchCriterion> criteria = CriteriaFactory.getCriteria(argParser.getCriteria());
                ISearchResults results = index.search(criteria);

                /* Print the results to the console. */
                index.show(results);
            } catch (FileNotFoundException exc) {
                System.out.println("File not found: " + exc.getMessage());
            } catch(IOException exc) {
                System.out.println("IO exception occured: " + exc.getMessage());
            }
        } catch (ParseException exc) {
            System.out.println("Error parsing command line arguments! " + exc.getMessage());
        }
    }
}
