package bibtex_search.bib_parser.record;

import java.util.*;
import java.util.stream.Collectors;

public enum RecordType {
    ARTICLE,
    BOOK,
    INPROCEEDINGS,
    CONFERENCE,
    BOOKLET,
    INBOOK,
    INCOLLECTION,
    MANUAL,
    MASTERSTHESIS,
    PHDTHESIS,
    TECHREPORT,
    MISC,
    UNPUBLISHED;

    public static Set<String> names = new HashSet<>(Arrays
            .stream(RecordType.values())
            .map(RecordType::name)
            .collect(Collectors.toSet()));
}
