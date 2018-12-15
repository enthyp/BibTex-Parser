package bibtex_search;

import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * {@inheritDoc}
 *
 * This implementation parses for the input file path, a list of
 * authors' last names, categories of BibTeX records and years of publication.
 */
public class CLIArgParser implements ICLIArgParser {

    /**
     * Path to input .bib file passed by the user.
     */
    private String bibFilePath;

    /**
     * Map from criteria names to desirable criteria values.
     */
    private Map<String, String[]> criteria;

    public void parseArgs(String[] args) throws ParseException {
        Option filePath = Option.builder("f")
                .longOpt("filepath")
                .hasArg()
                .argName( "file" )
                .required()
                .desc("path to .bib file" )
                .build();

        Option authors = Option.builder("a")
                .longOpt("authors")
                .hasArgs()
                .argName("authors")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .desc("last names of authors")
                .build();

        Option categories = Option.builder("c")
                .longOpt("categories")
                .hasArgs()
                .argName("categories")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .desc("names of categories")
                .build();

        Option years = Option.builder("y")
                .longOpt("years")
                .hasArgs()
                .argName("years")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .build();

        Options options = new Options();

        options.addOption(filePath);
        options.addOption(authors);
        options.addOption(categories);
        options.addOption(years);

        CommandLineParser cmdParser = new DefaultParser();

        try{
            /* File path. */
            CommandLine cmd = cmdParser.parse( options, args);
            this.bibFilePath = cmd.getOptionValue("f");

            /* Authors and categories. */
            String[] a =  cmd.getOptionValues("a");
            String[] c = cmd.getOptionValues("c");
            String[] y = cmd.getOptionValues("y");
            this.criteria = new HashMap<>();

            if (a != null)
                this.criteria.put("authors", a);
            if (c != null)
                this.criteria.put("categories", c);
            if (y != null)
                this.criteria.put("years", y);
        } catch (ParseException exc) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(printWriter, 90, "java -jar BibTex-Parser.jar", "",
                    options, formatter.getLeftPadding(), formatter.getDescPadding(), "", true);
            printWriter.flush();
            throw new ParseException(stringWriter.toString());
        }
    }

    public String getBibFilePath() {
        return bibFilePath;
    }

    /**
     * {@inheritDoc}
     *
     * Admissible values for a criterion name are: "authors", "categories", "years".
     */
    public Map<String, String[]> getCriteria() {
        return this.criteria;
    }
}
