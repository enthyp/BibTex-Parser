package bibtex_search;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class MainTest {
    @Test
    public void mainTest() {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String filePath = file.getAbsolutePath();

        Main.main(new String[] {"-f", filePath});
    }
}