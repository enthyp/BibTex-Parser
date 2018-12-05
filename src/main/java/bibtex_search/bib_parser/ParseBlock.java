package bibtex_search.bib_parser;

public class ParseBlock {
    private int lineStart;
    private int lineEnd;
    private String content;

    public ParseBlock(int lineStart, int lineEnd, String content) {
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.content = content;
    }

    public int getLineStart() {
        return lineStart;
    }

    public int getLineEnd() {
        return lineEnd;
    }

    public String getContent() {
        return content;
    }
}

