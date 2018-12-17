package bibtex_search.bib_parser;

import bibtex_search.bib_parser.record.IRecord;
import bibtex_search.bib_parser.record.Record;
import bibtex_search.bib_parser.record.RecordType;

import java.util.*;

public class BibValidator {

    private enum RecordStatus {
        /* Record has all mandatory fields without considering cross-references. */
        VALID,
        /* Record lacks mandatory fields and no cross-reference could help it. */
        INVALID,
        /* Record lacks some mandatory fields, but its cross-reference may provide them. */
        UNDETERMINED;
    }

    /**
     * Returns only records that have all mandatory fields, either by themselves or
     * thanks to records they cross-reference.
     * @param records a map between record keys and records themselves.
     * @return initial `records` map but with invalid records removed.
     */
    public Map<String, IRecord> validate(Map<String, IRecord> records) {
        /* Records to return. */
        Map<String, IRecord> finalRecords = new LinkedHashMap<>();

        /* Records to have their cross-references checked. */
        Map<String, IRecord> potentialRecords = new LinkedHashMap<>();

        /* 1st iteration - find all records correct by themselves, without cross-referencing. */
        for (Map.Entry<String, IRecord> entry : records.entrySet()) {
            IRecord record = new Record((Record)entry.getValue());
            RecordStatus status = this.validateNoCrossRef(record);

            if (status.equals(RecordStatus.VALID)) {
                /* Record is correct by itself, without considering cross-references. */
                finalRecords.put(entry.getKey(), record);
            } else if (status.equals(RecordStatus.UNDETERMINED)) {
                /* Cross-references may save this record in subsequent iterations. */
                potentialRecords.put(entry.getKey(), record);
            }
        }

        /* Remove cyclic (sic!) cross-references. */
        potentialRecords = this.dropCycles(potentialRecords);

        /* Subsequent iterations - to validate records that use cross-references. */
        while (!potentialRecords.isEmpty()) {
            /* Deep copy current state of potential records. */
            Map<String, IRecord> newPotentialRecords = deepCopy(potentialRecords);

            /* Walk over all potential records. */
            for (Map.Entry<String, IRecord> entry : potentialRecords.entrySet()) {
                IRecord record = new Record((Record)entry.getValue());
                String crossRef = record.getCrossRef();

                if (crossRef != null
                        && (finalRecords.containsKey(crossRef) || newPotentialRecords.containsKey(crossRef))) {
                    /* Record either is fixed by its cross-reference or is not. */
                    RecordStatus status = this.validateWithCrossRef(record, finalRecords,
                            newPotentialRecords.containsKey(crossRef));

                    if (status.equals(RecordStatus.VALID)) {
                        /* If it's OK - it is saved to results. */
                        finalRecords.put(record.getKey(), record);
                    }

                    if (!status.equals(RecordStatus.UNDETERMINED)) {
                        /* Record has been processed now and is no longer potential. */
                        newPotentialRecords.remove(record.getKey());
                    }
                } else {
                    /* The record has a cross-reference to a discarded record or none - no hope for it. */
                    newPotentialRecords.remove(record.getKey());
                    System.out.println(String.format("WARNING: Record under key '%s' lacks mandatory fields " +
                            "and references an incorrect record.\n", record.getKey()));
                }
            }

            potentialRecords = newPotentialRecords;
        }

        return finalRecords;
    }

    /**
     * Returns status of a record. In case of VALID record, passed instance is modified
     * (non-optional nor mandatory fields are removed). Otherwise no changes are introduced.
     * This method DOES NOT check "crossref" field.
     * @param record entry to be validated.
     * @return status of the record - VALID if all mandatory fields are in place, UNDETERMINED
     * if some are lacking, but there is a "crossref" field or INVALID if mandatory fields are
     * lacking and there is no "crossref" field.
     */
    private RecordStatus validateNoCrossRef(IRecord record) {
        RecordType category = record.getType();
        Set<String> mandatory =  new HashSet<>(mandatoryFields.get(category));
        Map<String, String> alternative = new HashMap<>();
        if (alternatives.containsKey(category))
            alternative.putAll(alternatives.get(category));

        /* Will remain `true` if record has all the mandatory fields without cross-referencing. */
        boolean correctAlone = true;
        Set<String> lacking = new LinkedHashSet<>();
        for (String fieldName : mandatory) {
            /* Check people first. */
            if (fieldName.equals("author") || fieldName.equals("editor")) {
                if (!record.getPeople().containsKey(fieldName)) {
                    String alternativeFieldName = alternative.get(fieldName);
                    if (!record.getPeople().containsKey(alternativeFieldName)) {
                        correctAlone = false;
                        lacking.add(fieldName);
                    }
                }
            } else if (!record.getFields().containsKey(fieldName)) {
                String alternativeFieldName = alternative.get(fieldName);
                if (!record.getFields().containsKey(alternativeFieldName)
                        || record.getFields().get(alternativeFieldName).equals("")) {
                    correctAlone = false;
                    lacking.add(fieldName);
                }
            } else if (record.getFields().get(fieldName).equals("")) {
                correctAlone = false;
                lacking.add(fieldName);
            }
        }

        if (correctAlone) {
            this.cleanRecord(record);
            /* Record is now clean and ready to be returned. */
            return RecordStatus.VALID;
        } else if (record.getCrossRef() != null) {
            return RecordStatus.UNDETERMINED;
        } else {
            System.out.println(String.format("WARNING: Record under key '%s' lacks mandatory fields: %s\n",
                    record.getKey(), lacking));
            return RecordStatus.INVALID;
        }
    }

    /**
     * Returns status of a record. In case of VALID record, passed instance is modified
     * (non-optional nor mandatory fields are removed). Otherwise no changes are introduced.
     * This method DOES check record referenced by "crossref" field (if it exists) and bases
     * the return value upon it.
     * @param record entry to be validated.
     * @param references records that can be used as cross-reference targets.
     * @param isLenient if `true` then UNDETERMINED is returned instead of INVALID.
     * @return status of the record - VALID if all mandatory fields are in place or are provided
     * by a record in `references`. INVALID if some mandatory fields are lacking and are not
     * provided by cross-referenced record and `isLenient` is set to `false`. Otherwise UNDETERMINED.
     */
    private RecordStatus validateWithCrossRef(IRecord record, Map<String, IRecord> references, boolean isLenient) {
        RecordType category = record.getType();
        Set<String> mandatory =  new HashSet<>(mandatoryFields.get(category));
        Map<String, String> alternative = new HashMap<>();
        if (alternatives.containsKey(category))
            alternative.putAll(alternatives.get(category));

        boolean isValid = true;
        Set<String> lacking = new LinkedHashSet<>();

        for (String fieldName : mandatory) {
            /* Check people first. */
            if (fieldName.equals("author") || fieldName.equals("editor")) {
                if (!record.getPeople().containsKey(fieldName)) {
                    String alternativeFieldName = alternative.get(fieldName);
                    if (!record.getPeople().containsKey(alternativeFieldName)) {
                        String crossRef = record.getCrossRef();
                        if (crossRef != null && references.containsKey(crossRef)) {
                            IRecord referenced = references.get(crossRef);

                            if (!referenced.getPeople().containsKey(fieldName)
                                    && !referenced.getPeople().containsKey(alternativeFieldName)) {
                                isValid = false;
                                lacking.add(fieldName);
                            }
                        } else {
                            isValid = false;
                            lacking.add(fieldName);
                        }
                    }
                }
            } else if (!record.getFields().containsKey(fieldName)) {
                String alternativeFieldName = alternative.get(fieldName);
                if (!record.getFields().containsKey(alternativeFieldName)) {
                    String crossRef = record.getCrossRef();
                    if (crossRef != null && references.containsKey(crossRef)) {
                        IRecord referenced = references.get(crossRef);

                        /* Referenced records are assumed to be correct. */
                        if (!referenced.getFields().containsKey(fieldName)
                                && !referenced.getFields().containsKey(alternativeFieldName)) {
                            isValid = false;
                            lacking.add(fieldName);
                        }
                    } else {
                        isValid = false;
                        lacking.add(fieldName);
                    }
                } else if (record.getFields().get(alternativeFieldName).equals("")) {
                    isValid = false;
                    lacking.add(fieldName);
                }
            } else if (record.getFields().get(fieldName).equals("")) {
                isValid = false;
                lacking.add(fieldName);
            }
        }

        if (isValid) {
            this.cleanRecord(record);
            return RecordStatus.VALID;
        } else if (!isLenient) {
            System.out.println(String.format("WARNING: Record under key '%s' lacks mandatory fields: %s\n",
                    record.getKey(), lacking));
            return RecordStatus.INVALID;
        } else {
            return RecordStatus.UNDETERMINED;
        }
    }

    /**
     * Returns a map between record keys and records but without records
     * that form cycles of cross-references.
     * It's not the most efficient implementation but come on, it's just BibTeX.
     * @param records map between record keys and record objects.
     * @return a map between record keys and record objects without cross-reference cycles.
     */
    private Map<String, IRecord> dropCycles(Map<String, IRecord> records) {
        /* Deep copy initial records. */
        Map<String, IRecord> results = deepCopy(records);

        /* Check all potential cycles. */
        for (Map.Entry<String, IRecord> entry : records.entrySet()) {
            /* Starting point of potential cycle. */
            IRecord currentRecord = entry.getValue();
            String startKey = currentRecord.getKey();

            /* Check if given record wasn't removed already. */
            if (results.keySet().contains(startKey)) {
                /* Keys encountered on cross-reference path. */
                Set<String> pathKeys = new HashSet<>();

                /* Add starting point. */
                pathKeys.add(startKey);

                /* Traverse the path. */
                while (currentRecord != null && currentRecord.getCrossRef() != null) {
                    String targetKey = currentRecord.getCrossRef();

                    if (pathKeys.contains(targetKey)) {
                        /* Cycle has closed - remove it from results. */
                        for (String key : pathKeys)
                            results.remove(key);
                        System.out.println(
                                String.format("WARNING: circular cross-reference encountered for key: %s\n",
                                        startKey));
                        break;
                    } else {
                        /* Follow down the path. */
                        pathKeys.add(targetKey);
                        currentRecord = records.get(targetKey);
                    }
                }
            }
        }

        return results;
    }

    /**
     * Removes all non-mandatory nor optional fields (and people types) from a record.
     * @param record entry to be cleaned up.
     */
    public void cleanRecord(IRecord record) {
        /* First construct a set of all admissible field names. */
        Set<String> admissible = getAllAdmissible(record.getType());

        /* Next remove all fields (and people) that do not occur in this set. */
        Set<String> toBeRemoved = new HashSet<>(record.getFields().keySet());
        toBeRemoved.removeAll(admissible);

        for (String fieldName : toBeRemoved)
            record.removeField(fieldName);

        toBeRemoved = new HashSet<>(record.getPeople().keySet());
        toBeRemoved.removeAll(admissible);

        for(String personType : toBeRemoved)
            record.removePersonType(personType);
    }

    /**
     * Returns all (optional, mandatory and their possible alternatives) field names (and people
     * types) for given category.
     * @param category type of the record that admissible field names are needed for.
     * @return a set of all admissible field names for that type of record.
     */
    private Set<String> getAllAdmissible(RecordType category) {
        Set<String> mandatory = new HashSet<>(mandatoryFields.get(category));
        Set<String> admissible =  new HashSet<>(optionalFields.get(category));
        Map<String, String> alternative = new HashMap<>();
        if (alternatives.containsKey(category))
            alternative.putAll(alternatives.get(category));

        admissible.addAll(mandatory);

        Set<String> admissibleAlternatives = new HashSet<>();
        for (String fieldName : admissible) {
            String alterName = alternative.get(fieldName);
            if (alterName != null)
                admissibleAlternatives.add(alterName);
        }

        admissible.addAll(admissibleAlternatives);
        admissible.add("crossref");
        return admissible;
    }


    /**
     * Returns a deep copy of passed map between record keys and records themselves.
     * @param records a map between record keys and records themselves.
     * @return a deep copy of passed argument.
     */
    private Map<String, IRecord> deepCopy(Map<String, IRecord> records) {
        /* Deep copy initial records. */
        Map<String, IRecord> copy = new LinkedHashMap<>();
        for (Map.Entry<String, IRecord> entry : records.entrySet()) {
            copy.put(entry.getKey(), new Record((Record)entry.getValue()));
        }

        return copy;
    }

    /**
     * All mandatory fields with respect to entry category.
     */
    private static Map<RecordType, Set<String>> mandatoryFields = new HashMap<RecordType, Set<String>>() {{
        put(RecordType.ARTICLE, new HashSet<String>(){{
            add("author");
            add("title");
            add("journal");
            add("year");
        }});
        put(RecordType.BOOK, new HashSet<String>(){{
            add("author");
            add("title");
            add("publisher");
            add("year");
        }});
        put(RecordType.INPROCEEDINGS, new HashSet<String>(){{
            add("author");
            add("title");
            add("booktitle");
            add("year");
        }});
        put(RecordType.CONFERENCE, new HashSet<String>(){{
            add("author");
            add("title");
            add("booktitle");
            add("year");
        }});
        put(RecordType.BOOKLET, new HashSet<String>(){{
            add("title");
        }});
        put(RecordType.INBOOK, new HashSet<String>(){{
            add("author");
            add("title");
            add("chapter");
            add("publisher");
            add("year");
        }});
        put(RecordType.INCOLLECTION, new HashSet<String>(){{
            add("author");
            add("title");
            add("booktitle");
            add("publisher");
            add("year");
        }});
        put(RecordType.MANUAL, new HashSet<String>(){{
            add("title");
        }});
        put(RecordType.MASTERSTHESIS, new HashSet<String>(){{
            add("author");
            add("title");
            add("school");
            add("year");
        }});
        put(RecordType.PHDTHESIS, new HashSet<String>(){{
            add("author");
            add("title");
            add("school");
            add("year");
        }});
        put(RecordType.TECHREPORT, new HashSet<String>(){{
            add("author");
            add("title");
            add("institution");
            add("year");
        }});
        put(RecordType.MISC, new HashSet<>());
        put(RecordType.UNPUBLISHED, new HashSet<String>(){{
            add("author");
            add("title");
            add("note");
        }});
    }};

    /**
     * All optional fields with respect to entry category.
     */
    private static Map<RecordType, Set<String>> optionalFields = new HashMap<RecordType, Set<String>>() {{
        put(RecordType.ARTICLE, new HashSet<String>(){{
            add("volume");
            add("number");
            add("pages");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.BOOK, new HashSet<String>(){{
            add("volume");
            add("series");
            add("address");
            add("edition");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.INPROCEEDINGS, new HashSet<String>(){{
            add("editor");
            add("volume");
            add("series");
            add("pages");
            add("address");
            add("month");
            add("organization");
            add("publisher");
            add("note");
            add("key");
        }});
        put(RecordType.CONFERENCE, new HashSet<String>(){{
            add("editor");
            add("volume");
            add("series");
            add("pages");
            add("address");
            add("month");
            add("organization");
            add("publisher");
            add("note");
            add("key");
        }});
        put(RecordType.BOOKLET, new HashSet<String>(){{
            add("author");
            add("howpublished");
            add("address");
            add("month");
            add("year");
            add("note");
            add("key");
        }});
        put(RecordType.INBOOK, new HashSet<String>(){{
            add("volume");
            add("series");
            add("type");
            add("address");
            add("edition");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.INCOLLECTION, new HashSet<String>(){{
            add("editor");
            add("volume");
            add("series");
            add("type");
            add("chapter");
            add("pages");
            add("address");
            add("edition");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.MANUAL, new HashSet<String>(){{
            add("author");
            add("organization");
            add("address");
            add("edition");
            add("month");
            add("year");
            add("note");
            add("key");
        }});
        put(RecordType.MASTERSTHESIS, new HashSet<String>(){{
            add("type");
            add("address");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.PHDTHESIS, new HashSet<String>(){{
            add("type");
            add("address");
            add("month");
            add("note");
            add("key");
        }});
        put(RecordType.TECHREPORT, new HashSet<String>(){{
            add("editor");
            add("volume");
            add("series");
            add("address");
            add("month");
            add("organization");
            add("publisher");
            add("note");
            add("key");
        }});
        put(RecordType.MISC, new HashSet<String>(){{
            add("author");
            add("title");
            add("howpublished");
            add("month");
            add("year");
            add("note");
            add("key");
        }});
        put(RecordType.UNPUBLISHED, new HashSet<String>(){{
            add("month");
            add("year");
            add("key");
        }});
    }};

    /**
     * All alternatives for different field names with respect to entry category.
     */
    private static Map<RecordType, Map<String, String>> alternatives =
        new HashMap<RecordType, Map<String, String>>() {{
            put(RecordType.BOOK, new HashMap<String, String>(){{
                put("author", "editor");
            }});
            put(RecordType.INPROCEEDINGS, new HashMap<String, String>(){{
                put("volume", "number");
            }});
            put(RecordType.INBOOK, new HashMap<String, String>(){{
                put("author", "editor");
                put("chapter", "pages");
                put("volume", "number");
            }});
            put(RecordType.INCOLLECTION, new HashMap<String, String>(){{
                put("volume", "number");
            }});
            put(RecordType.TECHREPORT, new HashMap<String, String>(){{
                put("volume", "number");
            }});
        }};
}