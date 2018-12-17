package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.IRecord;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

// TODO: use assertions.
// TODO: test case independence for categories.
// TODO: test general correctness - parsing entire file without additional criteria.

public class BibParserTest {

    @Test(expected = FileNotFoundException.class)
    public void fileNotExistsTest() throws IOException {
        BibParser bibParser = new BibParser();
        bibParser.parse("nonexistentfile");
    }

    @Test
    public void parseFileTest() throws IOException {
        String fileName = "/xampl_simplified.bib";
        String outputFileName = "/xampl_simplified_out.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());

        File outFile = new File(this.getClass().getResource(outputFileName).getFile());
        String expectedOutput = Files.lines(outFile.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        BibParser bibParser = new BibParser();
        bibParser.parse(file.getAbsolutePath());

        Set<IRecord> records = bibParser.getRecords();
        StringBuilder builder = new StringBuilder();
        for (IRecord record : records)
            builder.append(record.toString());

        //System.out.println(builder.toString());
        assertEquals(builder.toString(), expectedOutput);
    }

    @Test
    public void crossRefTest() throws IOException {
        String fileName = "/bib_test/cross_reference.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String outputFileName = "/bib_test/cross_reference_out.bib";

        File outFile = new File(this.getClass().getResource(outputFileName).getFile());
        String expectedOutput = Files.lines(outFile.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        BibParser bibParser = new BibParser();
        bibParser.parse(file.getAbsolutePath());

        Set<IRecord> records = bibParser.getRecords();
        StringBuilder builder = new StringBuilder();
        for (IRecord record : records)
            builder.append(record.toString());

        assertEquals(expectedOutput, builder.toString());
    }


    @Test
    public void crossRef2Test() throws IOException {
        String fileName = "/bib_test/cross_reference2.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String outputFileName = "/bib_test/cross_reference2_out.bib";

        File outFile = new File(this.getClass().getResource(outputFileName).getFile());
        String expectedOutput = Files.lines(outFile.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        BibParser bibParser = new BibParser();
        bibParser.parse(file.getAbsolutePath());

        Set<IRecord> records = bibParser.getRecords();
        StringBuilder builder = new StringBuilder();
        for (IRecord record : records)
            builder.append(record.toString());

        //System.out.println(builder.toString());
        assertEquals(expectedOutput, builder.toString());
    }

    @Test
    public void alternativesTest() throws IOException {
        String fileName = "/bib_test/alternatives.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String outputFileName = "/bib_test/alternatives_out.bib";

        File outFile = new File(this.getClass().getResource(outputFileName).getFile());
        String expectedOutput = Files.lines(outFile.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        BibParser bibParser = new BibParser();
        bibParser.parse(file.getAbsolutePath());

        Set<IRecord> records = bibParser.getRecords();
        StringBuilder builder = new StringBuilder();
        for (IRecord record : records)
            builder.append(record.toString());

        assertEquals(expectedOutput, builder.toString());
    }


    @Test
    public void emptyMandatoryTest() throws IOException {
        String fileName = "/bib_test/empty_mandatory.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String outputFileName = "/bib_test/empty_mandatory_out.bib";

        File outFile = new File(this.getClass().getResource(outputFileName).getFile());
        String expectedOutput = Files.lines(outFile.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        BibParser bibParser = new BibParser();
        bibParser.parse(file.getAbsolutePath());

        Set<IRecord> records = bibParser.getRecords();
        StringBuilder builder = new StringBuilder();
        for (IRecord record : records)
            builder.append(record.toString());

        assertEquals(expectedOutput, builder.toString());
    }

}