package bibtex_search.bib_parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Parser {

    private int lineOffset;
    private ArrayList<Integer> lineBeginnings = new ArrayList<Integer>() {{ add(0); }};

    /**
     *
     * @param index index of character in a string
     * @return which line given character is in
     */
    protected int getLineNumber(int index) {
        int foundIndex = Collections.binarySearch(lineBeginnings, index);

        if (foundIndex >= 0) {
            return lineOffset + foundIndex;
        } else {
            return lineOffset - foundIndex - 2;
        }
    }

    /**
     *
     * @param content string parsed by the Parser object
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
