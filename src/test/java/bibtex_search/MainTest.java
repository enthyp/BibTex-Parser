package bibtex_search;

import org.junit.Test;

import java.io.File;

public class MainTest {
    @Test
    public void mainTestCategories() {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();
        String categories = "unpublished book";

        Main.main(new String[] {"-f", filePath, "-c", categories, "book"});
    }

    @Test
    public void mainTestAuthors() {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();
        String authors = "Manmaker";

        Main.main(new String[] {"-f", filePath, "-a", authors, "Knuth"});
    }

    @Test
    public void mainTestYears() {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();

        Main.main(new String[] {"-f", filePath, "-y", "1988", "1977", "2022"});
    }

    @Test
    public void mainTestAll() {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();

        Main.main(new String[] {"-f", filePath, "-a", "Manmaker", "Knuth", "-c", "manual", "inboOk",
        "-y", "1986", "1977"});
    }
}