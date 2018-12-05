package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.Person;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PersonParser extends Parser {

    private enum Case {
        UPPER, LOWER, UNDETERMINED;
    }

    public Person parse(ParseBlock personData) throws ParseException {
        int commaCount = personData.getContent().split(",", -1).length - 1;
        String authorString = personData.getContent();
        Person person;

        try {
            switch (commaCount) {
                case 0:
                    person = parse1st(authorString);
                    break;

                case 1:
                    person = parse2nd(authorString);
                    break;

                case 2:
                    person = parse3rd(authorString);
                    break;

                default:
                    throw new ParseException("\nUnknown person signature!", -1);
            }
        } catch (ParseException e) {
            throw new ParseException(String.format("Line %d\n", personData.getLineStart()) + e.getMessage(), -1);
        }

        return person;
    }

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
     *
     * @param text sequence of characters we want to split into words in BibTex manner
     * @return array of words
     */
    public String[] splitIntoWords(String text) throws ParseException {
        ArrayList<String> words = new ArrayList<>();
        for (int position = 0; position < text.length();) {
            int wordEnd = passWord(text, position);
            words.add(text.substring(position, wordEnd));
            position = wordEnd;
            while (position < text.length() && Character.isWhitespace(text.charAt(position)))
                position++;
        }

        return words.toArray(new String[0]);
    }

    /**
     *
     * @param text sequence of characters we want to divide into words
     * @param start starting position of a word in the text (non-whitespace character)
     * @return last position of a word in the text + 1
     */
    private int passWord(String text, int start) throws ParseException {
        int i = start;

        while (i < text.length() && !Character.isWhitespace(text.charAt(i))) {
            // Eat balanced brackets block.
            if (text.charAt(i) == '[') {
                int count = 1;
                while (count > 0 && i < text.length() - 1) {
                    i++;
                    Character c = text.charAt(i);
                    if (c == '[')
                        count++;
                    else if (c == ']')
                        count--;
                }

                if (count > 0) {
                    throw new ParseException("Brackets in field 'author' don't match!", -1);
                }
            }
            i++;
        }

        return i;
    }

    /**
     *
     * @param word String of consecutive non-white characters
     * @return
     * @throws ParseException
     */
    private Case startsWith(String word) throws ParseException {
        for (int i = 0; i < word.length(); i++) {
            Character c = word.charAt(i);

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
                    throw new ParseException("Brackets in field 'author' don't match!", -1);
                }
            } else if (Character.isLetter(c)) {
                return Character.isUpperCase(c) ? Case.UPPER : Case.LOWER;
            }
        }

        return Case.UNDETERMINED;
    }
}
