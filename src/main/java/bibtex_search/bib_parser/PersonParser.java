package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Person;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A parser of people's names as specified in BibTeX standard.
 */
public class PersonParser extends WarningHandler {

    /**
     * Case of a word as specified in BibTeX standard.
     */
    private enum Case {
        UPPER, LOWER, UNDETERMINED;
    }

    /**
     * Parse a string of characters that occurred as e.g. "author" field's value and return
     * object representation as specified in BibTeX standard.
     *
     * @param personString given person name from .bib file.
     * @return object representation of person's data.
     */
    public Person parse(String personString) throws ParseException {
        int commaCount = personString.split(",", -1).length - 1;
        Person person;

        try {
            switch (commaCount) {
                case 0:
                    person = parse1st(personString);
                    break;

                case 1:
                    person = parse2nd(personString);
                    break;

                case 2:
                    person = parse3rd(personString);
                    break;

                default:
                    throw new ParseException(this.getLocation() + "Unknown person signature!");
            }
        } catch (ParseException exc) {
            throw new ParseException(this.getLocation() + exc.getMessage());
        }

        return person;
    }

    /**
     * Parse the "First von Last" form as specified in BibTeX standard.
     * @param personData given person name from .bib file.
     * @return object representation of person's data.
     */
    private Person parse1st(String personData) throws ParseException {
        String[] words = splitIntoWords(personData);

        /* Determine the first part. */
        int fst = 0; // fst will eventually hold the number of the last word of the first part.
        while (fst < words.length - 1 && startsWith(words[fst]) != Case.LOWER) // we group caseless words with First.
            fst++;

        /* Determine the second part. */
        int i = fst, snd = fst; // snd will eventually hold the number of the last word of the second part (if present).

        while (i < words.length - 1) {
            if (startsWith(words[i]) == Case.LOWER)
                snd = i + 1;
            i++;
        }

        /* Now an Person instance can be built. */
        String first = "", von = "", last = "";
        first = Arrays.stream(Arrays.copyOfRange(words, 0, fst)).collect(Collectors.joining(" "));
        if (snd > fst)
            von = Arrays.stream(Arrays.copyOfRange(words, fst, snd)).collect(Collectors.joining(" "));
        last = Arrays.stream(Arrays.copyOfRange(words, snd, words.length)).collect(Collectors.joining(" "));

        first = first.replaceAll("^\\s+|\\s+$", "");
        von = von.replaceAll("^\\s+|\\s+$", "");
        last = last.replaceAll("^\\s+|\\s+$", "");

        return new Person(first, last, von, "");
    }

    /**
     * Parse the "von Last, First" form as specified in BibTeX standard.
     * @param personData given person name from .bib file.
     * @return object representation of person's data.
     */
    private Person parse2nd(String personData) throws ParseException {
        String[] blocks = personData.split(",", -1);
        String vonLastBlock = blocks[0];
        String first = blocks[1];

        String[] vonLastWords = splitIntoWords(vonLastBlock);

        /* Determine the first part. */
        int i = 0, fst = 0; // fst will eventually hold the number of the last word of the first part.

        while (i < vonLastWords.length - 1) {
            if (startsWith(vonLastWords[i]) != Case.UPPER)
                fst = i + 1;
            i++;
        }

        /* Now an Person instance can be built. */
        String von = "", last = "";
        if (fst > 0)
            von = Arrays.stream(Arrays.copyOfRange(vonLastWords, 0, fst))
                    .collect(Collectors.joining(" "));
        last = Arrays.stream(Arrays.copyOfRange(vonLastWords, fst, vonLastWords.length))
                .collect(Collectors.joining(" "));

        first = first.replaceAll("^\\s+|\\s+$", "");
        von = von.replaceAll("^\\s+|\\s+$", "");
        last = last.replaceAll("^\\s+|\\s+$", "");

        return new Person(first, last, von, "");
    }

    /**
     * Parse the "von Last, Jr, First" form as specified in BibTeX standard.
     * @param personData given person name from .bib file.
     * @return object representation of person's data.
     */
    private Person parse3rd(String personData) throws ParseException {
        String[] blocks = personData.split(",", -1);
        String vonLastBlock = blocks[0];
        String jr = blocks[1];
        String first = blocks[2];

        String[] vonLastWords = splitIntoWords(vonLastBlock);

        /* Determine the first part. */
        int i = 0, fst = 0; // fst will eventually hold the number of the last word of the first part.

        while (i < vonLastWords.length - 1) {
            if (startsWith(vonLastWords[i]) != Case.UPPER)
                fst = i + 1;
            i++;
        }

        /* Now an Person instance can be built. */
        String von = "", last = "";
        if (fst > 0)
            von = Arrays.stream(Arrays.copyOfRange(vonLastWords, 0, fst))
                    .collect(Collectors.joining(" "));
        last = Arrays.stream(Arrays.copyOfRange(vonLastWords, fst, vonLastWords.length))
                .collect(Collectors.joining(" "));

        first = first.replaceAll("^\\s+|\\s+$", "");
        von = von.replaceAll("^\\s+|\\s+$", "");
        last = last.replaceAll("^\\s+|\\s+$", "");
        jr = jr.replaceAll("^\\s+|\\s$", "");

        return new Person(first, last, von, jr);
    }

    /**
     * Split given text into words, treating balanced bracket blocks as single characters.
     * @param text sequence of characters we want to split into words in BibTex manner
     * @return array of words
     */
    public String[] splitIntoWords(String text) throws ParseException {
        ArrayList<String> words = new ArrayList<>();
        for (int position = 0; position < text.length();) {
            int wordEnd;

            try {
                wordEnd = passWord(text, position);
            } catch (ParseException exc) {
                throw new ParseException(this.getLocation() + exc.getMessage());
            }

            words.add(text.substring(position, wordEnd));
            position = wordEnd;
            while (position < text.length() && Character.isWhitespace(text.charAt(position)))
                position++;
        }

        return words.toArray(new String[0]);
    }

    /**
     * Jump over a word, treating balanced bracket blocks as single characters.
     * @param text sequence of characters we want to divide into words
     * @param start starting position of a word in the text (non-whitespace character)
     * @return last position of a word in the text + 1
     */
    private int passWord(String text, int start) throws ParseException {
        int i = start;

        while (i < text.length() && !Character.isWhitespace(text.charAt(i))) {
            // Eat balanced brackets block.
            if (text.charAt(i) == ']') {
                throw new ParseException("Brackets in field 'author' don't match!");
            }

            if (text.charAt(i) == '[') {
                int count = 1;
                while (count > 0 && i < text.length() - 1) {
                    i++;
                    Character c = text.charAt(i);
                    if (c == '[')
                        count++;
                    else if (c == ']')
                        count--;
                    /* Invariant: c = number of unmatched brackets within text[0..i] */
                }

                if (count > 0) {
                    throw new ParseException("Brackets in field 'author' don't match!");
                }
            }
            i++;
        }

        return i;
    }

    /**
     * Returns the case given word starts with as specified in BibTeX standard.
     * @param word String of consecutive non-white characters
     * @return what case the word "starts with" (BibTeX manner)
     */
    private Case startsWith(String word) throws ParseException {
        for (int i = 0; i < word.length(); i++) {
            Character c = word.charAt(i);

            if (c == ']') {
                throw new ParseException("Brackets in field 'author' don't match!");
            }

            if (c == '[') {
                int count = 1;
                while (count > 0 && i < word.length() - 1) {
                    i++;
                    c = word.charAt(i);
                    if (c == ']') {
                        count--;
                    } else if (c == '[')
                        count++;
                }

                if (count > 0) {
                    throw new ParseException("Brackets in field 'author' don't match!");
                }
            } else if (Character.isLetter(c)) {
                return Character.isUpperCase(c) ? Case.UPPER : Case.LOWER;
            }
        }

        return Case.UNDETERMINED;
    }
}
