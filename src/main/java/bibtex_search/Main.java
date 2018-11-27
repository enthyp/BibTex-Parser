package bibtex_search;

import bibtex_search.bib_parser.BibParser;
import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        /* Parse arguments. */
        CommandLine cmd = parseArguments(args);

        if (cmd != null) {
            /* Arguments parsed no problem. */
            BibParser bibParser = new BibParser();

            try {
                bibParser.parse(cmd.getOptionValue("f"));

                // ...some searching based on command line arguments / print all.
            } catch (FileNotFoundException exc) {
                System.out.println("File not found: " + exc.getMessage());
            } catch(IOException exc) {
                System.out.println("IO exception occured: " + exc.getMessage());
            }
        }
    }


    /**
     *
     * @param args command line arguments
     * @return command line arguments parsing results
     */
    public static CommandLine parseArguments(String[] args) {
        // TODO: add option to suppress warnings!!!
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
        } catch (ParseException exc) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar BibTex-Parser.jar", options, true);
        }

        return cmd;
    }
}
