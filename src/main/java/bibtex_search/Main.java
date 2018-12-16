package bibtex_search;

import bibtex_search.bib_index.IIndex;
import bibtex_search.bib_index.ISearchResults;
import bibtex_search.bib_index.Index;
import bibtex_search.bib_index.bib_filter.FilterFactory;
import bibtex_search.bib_index.bib_filter.BaseSearchCriterion;
import bibtex_search.bib_parser.BibParser;
import bibtex_search.bib_parser.IBibParser;
import bibtex_search.bib_parser.record.IRecord;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

// TODO: add documentation to every method and field (then you can generate e.g. for public only).

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
                Set<IRecord> foundRecords = bibParser.getRecords();
                index.build(foundRecords);

                /* Get the criteria and search the file accordingly. */
                List<BaseSearchCriterion> criteria = FilterFactory.getCriteria(argParser.getCriteria());
                ISearchResults results = index.search(criteria);

                /* Print the results to the console. */
                index.show(results);
            } catch(IOException exc) {
                System.out.println(exc.getMessage());
            }
        } catch (ParseException exc) {
            System.out.println(exc.getMessage());
        }
    }
}
