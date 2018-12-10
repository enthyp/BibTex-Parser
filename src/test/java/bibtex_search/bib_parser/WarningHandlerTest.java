package bibtex_search.bib_parser;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

// TODO: use assertions.
// TODO: test line number correctness for warnings from field, author, record level
// with different reasons for the warning.

public class WarningHandlerTest {
    @Test
    public void getLineNumberTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, IOException {
        String fileName = "/xampl_simplified.bib";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        BibParser bibParser = new BibParser();
        Method method = BibParser.class.getSuperclass()
                .getDeclaredMethod("setLineBeginnings", String.class, int.class);
        method.setAccessible(true);
        method.invoke(bibParser, fileContent, 1);

        Pattern keyPattern = Pattern.compile("author\\s=\\s\"(?<author>[^|]+)\"\\|");
        Matcher keyMatcher = keyPattern.matcher(fileContent);

        while (keyMatcher.find()) {
            method = RecordParser.class.getSuperclass().getDeclaredMethod("getLineNumber", int.class);
            method.setAccessible(true);
            System.out.println(method.invoke(bibParser, keyMatcher.start()));
        }
    }
}