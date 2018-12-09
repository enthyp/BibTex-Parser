package bibtex_search;

import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * {@inheritDoc}
 *
 * This implementation parses for, aside from the input file path, a list of
 * authors' last names and categories of BibTeX records.
 */
public class CLIArgParser implements ICLIArgParser {

    private String bibFilePath;
    private Map<String, String[]> criteria;

    public void parseArgs(String[] args) throws ParseException {
        // TODO: additional option to suppress warnings?
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
                .argName("author")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .desc("last names of authors")
                .build();

        Option categories = Option.builder("c")
                .longOpt("categories")
                .hasArgs()
                .argName("category")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .desc("names of categories")
                .build();

        Options options = new Options();

        options.addOption(filePath);
        options.addOption(authors);
        options.addOption(categories);

        CommandLineParser cmdParser = new DefaultParser();
        CommandLine cmd = null;

        try{
            cmd = cmdParser.parse( options, args);
            this.bibFilePath = cmd.getOptionValue("f");
            this.criteria = new HashMap<>();
            this.criteria.put("authors", cmd.getOptionValues("a"));
            this.criteria.put("categories", cmd.getOptionValues("c"));
        } catch (ParseException exc) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(printWriter, 80, "java -jar BibTex-Parser.jar", "OPTIONS",
                    options, formatter.getLeftPadding(), formatter.getDescPadding(), "");
            printWriter.flush();
            throw new ParseException(stringWriter.toString());
        }
    }

    public String getBibFilePath() {
        return bibFilePath;
    }

    public Map<String, String[]> getCriteria() {
        return this.criteria;
    }
}
