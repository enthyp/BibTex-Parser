package bibtex_search.bib_parser;

import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a way to print warnings to std output along with boundaries of the
 * block of code that caused a problem.
 *
 */
public abstract class WarningHandler {
    /* The number the first line of given string had in the original input .bib file. */
    private int lineOffset;

    /* Indices of starting characters of subsequent lines of given string. */
    private ArrayList<Integer> lineBeginnings = new ArrayList<Integer>() {{ add(0); }};


    /**
     * This is the default way of handling exceptions from lower level parsers.
     * They indicate that lower level object was unable to return it's result.
     * In most cases that's not a problem (e.g. a record possibly can lack some fields).
     * @param exc a localised exception from a lower level parser
     */
    protected void handle(ParseException exc) {
        System.out.println(exc.getMessage() + "\n");
    }

    protected String getLocation() {
        int startLine = lineOffset;
        int endLine = lineOffset + lineBeginnings.size() - 1;
        String errMsg;

        if (startLine < endLine) {
            errMsg = String.format("WARNING: Lines %d-%d\n", startLine, endLine);
        } else {
            errMsg = String.format("WARNING: Line %d\n", startLine);
        }

        return errMsg;
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
