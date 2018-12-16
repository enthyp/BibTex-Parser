package bibtex_search;

import org.junit.Test;

import java.io.File;

// TODO: use assertions (add expected results to appropriate files).
// TODO: check different cases: interplay of options, what if some (or all of them) are wrong etc.

public class MainTest {
    @Test
    public void mainTestCategories() {
        String fileName = "/bib_test/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();
        String categories = "unpublished book";
        Main.main(new String[] {"-f", filePath, "-s", "book"});
    }

    @Test
    public void mainTestAuthors() {
        String fileName = "/bib_test/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();
        String authors = "Manmaker";

        Main.main(new String[] {"-f", filePath, "-a", authors, "Ullman"});
    }

    @Test
    public void mainTestYears() {
        String fileName = "/bib_test/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();

        Main.main(new String[] {"-f", filePath, "-y", "1988", "1977", "2022"});
    }

    @Test
    public void mainTestAll() {
        String fileName = "/bib_test/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();

        Main.main(new String[] {"-f", filePath, "-a", "Manmaker", "Knuth", "-c", "manual", "inboOk",
        "-y", "1986", "1977"});
    }
}