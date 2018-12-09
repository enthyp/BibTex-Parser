package bibtex_search;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class MainTest {
    @Test
    public void mainTestCategories() {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();
        String categories = "unpublished book";

        // TODO: if criterion is not recognized - a warning must be printed.
        Main.main(new String[] {"-f", filePath, "-c", categories, "book"});
    }

    @Test
    public void mainTestAuthors() {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();
        String authors = "Manmaker";

        // TODO: if author is not found - a warning should be printed?
        Main.main(new String[] {"-f", filePath, "-a", authors, "Knuth"});
    }

    @Test
    public void mainTestAll() {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();

        // TODO: if author is not found - a warning should be printed?
        Main.main(new String[] {"-f", filePath, "-a", "Manmaker", "Knuth", "-c", "manual", "inboOk"});
    }
}