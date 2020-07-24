package de.tuebingen.uni.sfs.germanet.api;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple class to transfer data from the loaders to the GermaNet object.
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */
class LoaderData {
    // input data streams and file names
    private List<InputStream> inputStreams;
    private List<String> xmlNames;
    private InputStream relsInputStream;
    private String relsXmlName;
    private InputStream iliInputStream;
    private String iliXmlName;
    private List<InputStream> wiktInputStreams;
    private List<String> wiktXmlNames;

    private List<IliRecord> iliRecords;
    private List<WiktionaryParaphrase> wiktionaryParaphrases;
    Object2IntMap<WordCategory> catMaxHypernymDistanceMap;

    private List<Synset> synsets;
    private List<LexUnit> lexUnits;
    private Int2ObjectOpenHashMap<Synset> synsetIdMap;
    private Int2ObjectOpenHashMap<LexUnit> lexUnitIdMap;
    private Object2ObjectMap<WordCategory, Set<Synset>> catSynsetMap;
    private Object2ObjectMap<WordCategory, Set<LexUnit>> catLexUnitMap;
    private Map<WordCategory, Map<String, Set<LexUnit>>> wordCategoryMapAllOrthForms;
    private Object2ObjectMap<String, ObjectSet<String>> lowerToUpperMap;

    LoaderData() {
        inputStreams = new ArrayList<>();
        xmlNames = new ArrayList<>();
        relsInputStream = null;
        relsXmlName = null;
        iliInputStream = null;
        iliXmlName = null;
        wiktInputStreams = new ArrayList<>();
        wiktXmlNames = new ArrayList<>();

    }

    /**
     * Add the given stream to the correct stream list for loading,
     * based on its name.
     *
     * @param fileName name of the file, for logging
     * @param stream input stream to add
     */
    void addStreamToLists(String fileName, InputStream stream) {
        if (fileName.startsWith("wiktionary") && fileName.endsWith(".xml")) {
            wiktInputStreams.add(stream);
            wiktXmlNames.add(fileName);
        } else if (fileName.startsWith("interLingualIndex") && fileName.endsWith(".xml")) {
            iliInputStream = stream;
            iliXmlName = fileName;
        } else if (fileName.equals("gn_relations.xml")) {
            relsInputStream = stream;
            relsXmlName = fileName;
        } else if (fileName.endsWith(".xml") &&
                (fileName.startsWith("nomen.")
                        || fileName.startsWith("adj.")
                        || fileName.startsWith("verben."))) {
            inputStreams.add(stream);
            xmlNames.add(fileName);
        }
    }

    void trimAll() {
        ObjectIterator<Synset> iterator = ObjectIterators.asObjectIterator(synsets.iterator());
        Synset synset;
        while (iterator.hasNext()) {
            synset = iterator.next();
            synset.trimAll();
        }

        ((ObjectArrayList<Synset>) synsets).trim();
        ((ObjectArrayList<LexUnit>)lexUnits).trim();
        synsetIdMap.trim();
        lexUnitIdMap.trim();
    }

    List<InputStream> getInputStreams() {
        return inputStreams;
    }

    List<String> getXmlNames() {
        return xmlNames;
    }

    List<InputStream> getWiktInputStreams() {
        return wiktInputStreams;
    }

    List<String> getWiktXmlNames() {
        return wiktXmlNames;
    }

    InputStream getIliInputStream() {
        return iliInputStream;
    }

    String getIliXmlName() {
        return iliXmlName;
    }

    InputStream getRelsInputStream() {
        return relsInputStream;
    }

    String getRelsXmlName() {
        return relsXmlName;
    }

    void setSynsets(List<Synset> synsets) {
        this.synsets = synsets;
    }

    void setLexUnits(List<LexUnit> lexUnits) {
        this.lexUnits = lexUnits;
    }

    void setSynsetIdMap(Int2ObjectOpenHashMap<Synset> synsetIdMap) {
        this.synsetIdMap = synsetIdMap;
    }

    void setLexUnitIdMap(Int2ObjectOpenHashMap<LexUnit> lexUnitIdMap) {
        this.lexUnitIdMap = lexUnitIdMap;
    }

    void setCatSynsetMap(Object2ObjectMap<WordCategory, Set<Synset>> catSynsetMap) {
        this.catSynsetMap = catSynsetMap;
    }

    void setCatLexUnitMap(Object2ObjectMap<WordCategory, Set<LexUnit>> catLexUnitMap) {
        this.catLexUnitMap = catLexUnitMap;
    }

    void setWordCategoryMapAllOrthForms(Map<WordCategory, Map<String, Set<LexUnit>>> wordCategoryMapAllOrthForms) {
        this.wordCategoryMapAllOrthForms = wordCategoryMapAllOrthForms;
    }

    void setLowerToUpperMap(Object2ObjectMap<String, ObjectSet<String>> lowerToUpperMap) {
        this.lowerToUpperMap = lowerToUpperMap;
    }

    void setIliRecords(List<IliRecord> iliRecords) {
        this.iliRecords = iliRecords;
    }

    void setWiktionaryParaphrases(List<WiktionaryParaphrase> wiktionaryParaphrases) {
        this.wiktionaryParaphrases = wiktionaryParaphrases;
    }

    void setCatMaxHypernymDistanceMap(Object2IntMap<WordCategory> catMaxHypernymDistanceMap) {
        this.catMaxHypernymDistanceMap = catMaxHypernymDistanceMap;
    }

    List<Synset> getSynsets() {
        return synsets;
    }

    List<LexUnit> getLexUnits() {
        return lexUnits;
    }

    Int2ObjectMap<Synset> getSynsetIdMap() {
        return synsetIdMap;
    }

    Int2ObjectMap<LexUnit> getLexUnitIdMap() {
        return lexUnitIdMap;
    }

    Object2ObjectMap<WordCategory, Set<Synset>> getCatSynsetMap() {
        return catSynsetMap;
    }

    Object2ObjectMap<WordCategory, Set<LexUnit>> getCatLexUnitMap() {
        return catLexUnitMap;
    }

    Map<WordCategory, Map<String, Set<LexUnit>>> getWordCategoryMapAllOrthForms() {
        return wordCategoryMapAllOrthForms;
    }

    Object2ObjectMap<String, ObjectSet<String>> getLowerToUpperMap() {
        return lowerToUpperMap;
    }

    List<IliRecord> getIliRecords() {
        return iliRecords;
    }

    List<WiktionaryParaphrase> getWiktionaryParaphrases() {
        return wiktionaryParaphrases;
    }

    Object2IntMap<WordCategory> getCatMaxHypernymDistanceMap() {
        return catMaxHypernymDistanceMap;
    }
}
