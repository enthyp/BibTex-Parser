package bibtex_search.bib_parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a way to print warnings to std output along with boundaries of the
 * block of code that caused a problem.
 *
 * As a general side-note: the processing of warnings is conducted as follows.
 *   Every object responsible for parsing a block of text (full record, individual field, string
 * variable definition) has one method that does the parsing.
 *   The call to that method is always enclosed in a try-catch block in a method of an object
 * responsible for initialization of the parser (a parser of the super-block of text usually).
 *   Inner parser always tries to handle it's own warnings (do the printing, not propagate anymore
 * and return a useful result).
 *   However, when a result cannot be returned due to a major malfunction (record lacking mandatory
 * fields for example), a ParseException is thrown for the super-parser to handle.
 *   Every time, when catching a ParseException in a WarningHandler, it's `handle` method is called,
 * as it contains appropriate information about the issue location.
 */
public abstract class WarningHandler {
    // TODO: add interfaces to parser classes! SOLID mnemonic! Factory class. Dependency Injection.
    /* The number the first line of given string had in the original input .bib file. */
    private int lineOffset;

    /* Indices of starting characters of subsequent lines of given string. */
    private ArrayList<Integer> lineBeginnings = new ArrayList<Integer>() {{ add(0); }};

    private void handle(ParseException exc) {

    }

    /**
     *
     * @param index index of character in a string
     * @return which line given character is in
     */
    protected int getLineNumber(int index) {
        int foundIndex = Collections.binarySearch(lineBeginnings, index);

        if (foundIndex >= 0) {
            /* Searched character is actually the first one in some line. */
            return lineOffset + foundIndex;
        } else {
            /* Because `binary_search` returns (-(index) - 1), where index is of the first element greater
            than what we're looking for or the end of array if none such are in the array. */
            return lineOffset - foundIndex - 2;
        }
    }

    /**
     *
     * @param content string parsed by the WarningHandler object
     * @param lineOffset number of the first (in given string) line in the original file
     */
    protected void setLineBeginnings(String content, int lineOffset) {
        Pattern newLines = Pattern.compile("\n");
        Matcher matcher = newLines.matcher(content);

        this.lineOffset = lineOffset;
        while (matcher.find()) {
            lineBeginnings.add(matcher.end());
        }
    }
}
