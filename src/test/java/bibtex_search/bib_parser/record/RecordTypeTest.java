package bibtex_search.bib_parser.record;

import org.junit.Test;

import static org.junit.Assert.*;

public class RecordTypeTest {

    @Test
    public void namesTest() {
        String names = "[MISC, INPROCEEDINGS, ARTICLE, INBOOK, TECHREPORT, BOOKLET, CONFERENCE, INCOLLECTION, MASTERSTHESIS, PHDTHESIS, BOOK, UNPUBLISHED, MANUAL]";
        assertEquals(RecordType.names.toString(), names);
    }
}
