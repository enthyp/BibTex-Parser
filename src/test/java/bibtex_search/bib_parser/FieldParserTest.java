package bibtex_search.bib_parser;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.util.HashMap;

public class FieldParserTest {

    @Test
    public void singleKnownVarTest() throws ParseException {
        String fieldString = "blob_2 = \nbzzt";
        FieldParser fieldParser = new FieldParser(new HashMap<String, String>() {{
            put("bzzt", "Well done");
        }});
        fieldParser.parse(fieldString);
    }

    @Test(expected = ParseException.class)
    public void singleUnknownVarTest() throws ParseException {
        String fieldString = "blob_2 = bzzt";
        FieldParser fieldParser = new FieldParser(new HashMap<>());
        fieldParser.parse(fieldString);
    }

    @Test
    public void multipleVarTest() throws ParseException {
        String fieldString = "blob_2 = bzzt # \nbob";
        FieldParser fieldParser = new FieldParser(new HashMap<String, String>() {{
            put("bzzt", "Well done");
            put("bob", " mate");
        }});
        fieldParser.parse(fieldString);
    }

    @Test
    public void multipleVarPlusTextTest() throws ParseException {
        FieldParser fieldParser = new FieldParser(new HashMap<String, String>() {{
            put("bzzt", "Well done");
            put("bob", " mate");
            put("zob", " gr8 b8");
        }});
        fieldParser.parse("bob = bob # \"\n bait \"#bzzt");
        fieldParser.parse("blob_2 = \"don \" #bzzt # bob#\"  ms#dmms\"");
        fieldParser.parse("mob = \" Loads \"# bzzt # \"\n\" # bob # zob");
    }

    @Test(expected = ParseException.class)
    public void multipleVarPlusTextIncorrectTest() throws ParseException {
        FieldParser fieldParser = new FieldParser(new HashMap<String, String>() {{
            put("bzzt", "Well done");
            put("bob", " mate");
            put("zob", " gr8 b8");
        }});
        fieldParser.parse("bob = bob 1 # \" bait \"#bzzt");
    }

    @Test(expected = ParseException.class)
    public void multipleVarPlusTextIncorrect2Test() throws ParseException {
        FieldParser fieldParser = new FieldParser(new HashMap<String, String>() {{
            put("bzzt", "Well done");
            put("bob", " mate");
            put("zob", " gr8 b8");
        }});
        fieldParser.parse("bob = bob * # \" bait \"# bzzt");
    }


    @Test
    public void parseCorrectFieldTest() throws ParseException {
        String fieldString = "blob_2 = \" kjsdn21**\n\t [[[] {} xDDD!1 a\" # \"gooo\"";
        FieldParser fieldParser = new FieldParser(new HashMap<>());
        fieldParser.parse(fieldString);
    }

    @Test(expected = ParseException.class)
    public void emptyValueTest() throws ParseException {
        String fieldString = "blob = ";
        FieldParser fieldParser = new FieldParser(new HashMap<>());
        fieldParser.parse(fieldString);
    }

    // TODO: empty value must be parsed, but should not be allowed for mandatory fields (Validator).
    @Test
    public void emptyValueQuotesTest() throws ParseException {
        String fieldString = "blob = \"\"";
        FieldParser fieldParser = new FieldParser(new HashMap<>());
        fieldParser.parse(fieldString);
    }

    @Test(expected = ParseException.class)
    public void nonWordCharacterInNameTest() throws ParseException {
        String fieldString = "s+tockey = \"OX[\\singleletter[stoc]]\"";
        FieldParser fieldParser = new FieldParser(new HashMap<>());
        fieldParser.parse(fieldString);
    }

    @Test(expected = ParseException.class)
    public void whitespaceInNameTest() throws ParseException {
        String fieldString = "s tockey = \"OX[\\singleletter[stoc]]\"";
        FieldParser fieldParser = new FieldParser(new HashMap<>());
        fieldParser.parse(fieldString);
    }

    @Test(expected = ParseException.class)
    public void noQuotationMarkInValueTest() throws ParseException {
        String fieldString = "stockey12 = \"OX[\\singleletter[stoc]]";
        FieldParser fieldParser = new FieldParser(new HashMap<>());
        fieldParser.parse(fieldString);
    }

    @Test(expected = ParseException.class)
    public void nonWordCharacterInValueTest() throws ParseException {
        String fieldString = "stockey12 = bzz*t";
        FieldParser fieldParser = new FieldParser(new HashMap<>());
        fieldParser.parse(fieldString);
    }

    @Test(expected = ParseException.class)
    public void soWrongTest() throws ParseException {
        String fieldString = "stockey12 = \"sdf\" \"f\"";
        FieldParser fieldParser = new FieldParser(new HashMap<>());
        fieldParser.parse(fieldString);
    }
}