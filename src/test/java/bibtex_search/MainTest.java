package bibtex_search;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class MainTest {
    @Test
    public void mainTestCategories() throws IOException {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());

        String filePath = file.getAbsolutePath();
        String categories = "unpublished book";
        Main.main(new String[] {"-f", filePath, "-c", "book"});
    }

    @Test
    public void mainTestAuthors() {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();
        String authors = "Manmaker";

        Main.main(new String[] {"-f", filePath, "-a", "Ullman"});
    }

    @Test
    public void mainTestYears() {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();

        Main.main(new String[] {"-f", filePath, "-y", "1977", "2022"});
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