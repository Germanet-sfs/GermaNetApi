/*
 * Copyright (C) 2012 Department of General and Computational Linguistics,
 * University of Tuebingen
 *
 * This file is part of the Java API to GermaNet.
 *
 * The Java API to GermaNet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Java API to GermaNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this API; if not, see <http://www.gnu.org/licenses/>.
 */
package de.tuebingen.uni.sfs.germanet.api;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;

import static de.tuebingen.uni.sfs.germanet.api.GermaNet.GNROOT_ID;
import static de.tuebingen.uni.sfs.germanet.api.GermaNet.NUMBER_OF_GERMANET_FILES;

/**
 * Stax loader for GermaNet xml files. All Synsets must be loaded before
 * any relations can be loaded.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
class StaxLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaxLoader.class);

    /**
     * Loads all synset streams and then all relations.
     *
     * @throws FileNotFoundException if a file is not found
     * @throws XMLStreamException if there is a problem with a stream
     */
    static LoaderData load(LoaderData loaderData) throws XMLStreamException, IOException {

        List<InputStream> inputStreams = loaderData.getInputStreams();
        List<String> xmlNames = loaderData.getXmlNames();
        InputStream relsInputStream = loaderData.getRelsInputStream();
        String relsXmlName = loaderData.getRelsXmlName();

        int loadedFiles = 0;
        if (inputStreams == null || inputStreams.isEmpty()) {
            throw new FileNotFoundException("Unable to load GermaNet data.");
        }

        // setting capacity for Synset and LexUnit data structures
        // does not seem to make a significant difference in performance
        // using default values
        List<Synset> synsets = new ObjectArrayList<>();
        List<LexUnit> lexUnits = new ObjectArrayList<>();
        Int2ObjectOpenHashMap<Synset> synsetIdMap = new Int2ObjectOpenHashMap<>();
        Int2ObjectOpenHashMap<LexUnit> lexUnitIdMap = new Int2ObjectOpenHashMap<>();
        Object2ObjectOpenHashMap<WordCategory, Set<Synset>> catSynsetMap = new Object2ObjectOpenHashMap<>(WordCategory.values().length);
        Object2ObjectOpenHashMap<WordCategory, Set<LexUnit>> catLexUnitMap = new Object2ObjectOpenHashMap<>(WordCategory.values().length);
        Map<WordCategory, Map<String, Set<LexUnit>>> wordCategoryMapAllOrthForms = new Object2ObjectOpenHashMap<>(WordCategory.values().length);
        Object2ObjectMap<String, ObjectSet<String>> lowerToUpperMap = new Object2ObjectOpenHashMap<>();

        ObjectIterator<Synset> synsetIterator;
        ObjectIterator<LexUnit> lexUnitIterator;
        List<Synset> synsetsFromInputStream;
        Synset synset;
        LexUnit lexUnit;
        Set<Synset> synsetSet;
        Set<LexUnit> lexUnitSet;
        WordCategory cat;
        Map<String, Set<LexUnit>> mapAllOrthForms;
        String orthForm;

        // load all synset input streams first with a SynsetLoader
        for (int i = 0; i < inputStreams.size(); i++) {
            InputStream stream = inputStreams.get(i);
            String name = xmlNames.get(i);

            LOGGER.info("Loading {}...", name);
            synsetsFromInputStream = SynsetLoader.loadSynsets(stream);
            synsets.addAll(synsetsFromInputStream);
            synsetIterator = ObjectIterators.asObjectIterator(synsetsFromInputStream.iterator());

            while (synsetIterator.hasNext()) {
                synset = synsetIterator.next();
                cat = synset.getWordCategory();
                synsetIdMap.put(synset.getId(), synset);

                // Don't add Root or its LexUnit to any of the
                // WordCategory or orthForm maps
                if (synset.getId() == GNROOT_ID) {
                    continue;
                }

                synsetSet = catSynsetMap.getOrDefault(cat, new ObjectOpenHashSet<>());
                synsetSet.add(synset);
                catSynsetMap.put(cat, synsetSet);
                mapAllOrthForms = wordCategoryMapAllOrthForms.getOrDefault(synset.getWordCategory(), new Object2ObjectOpenHashMap<>());

                lexUnitIterator = ObjectIterators.asObjectIterator(synset.getLexUnits().iterator());
                while (lexUnitIterator.hasNext()) {
                    lexUnit = lexUnitIterator.next();
                    lexUnitIdMap.put(lexUnit.getId(), lexUnit);
                    lexUnitSet = catLexUnitMap.getOrDefault(cat, new ObjectOpenHashSet<>());
                    lexUnitSet.add(lexUnit);
                    catLexUnitMap.put(cat, lexUnitSet);
                    lexUnits.add(lexUnit);

                    // add orthForm and lowercase orthForm to lowerToUpperMap
                    orthForm = lexUnit.getOrthForm();
                    StaxLoader.processOrthForm(orthForm, lexUnit, mapAllOrthForms, lowerToUpperMap);

                    // get orthVar
                    // add orthVar and lowercase orthVar to lowerToUpperMap
                    orthForm = lexUnit.getOrthVar();
                    StaxLoader.processOrthForm(orthForm, lexUnit, mapAllOrthForms, lowerToUpperMap);

                    // get oldOrthForm
                    // add oldOrthForm and lowercase oldOrthForm to lowerToUpperMap
                    orthForm = lexUnit.getOldOrthForm();
                    StaxLoader.processOrthForm(orthForm, lexUnit, mapAllOrthForms, lowerToUpperMap);

                    // get oldOrthVar
                    // add oldOrthVar and lowercase oldOrthVar to lowerToUpperMap
                    orthForm = lexUnit.getOldOrthVar();
                    StaxLoader.processOrthForm(orthForm, lexUnit, mapAllOrthForms, lowerToUpperMap);
                }
                wordCategoryMapAllOrthForms.put(cat, mapAllOrthForms);
            }

            stream.close();
            loadedFiles++;
        }

        // load relations with a RelationLoader
        LOGGER.info("Loading {}...", relsXmlName);
        RelationLoader.loadRelations(relsInputStream, synsetIdMap, lexUnitIdMap);
        loadedFiles++;

        if (loadedFiles >= NUMBER_OF_GERMANET_FILES) {
            LOGGER.info("Done loading {} GermaNet files.", loadedFiles);
        } else {
            throw new FileNotFoundException("GermaNet data not found or files are missing.");
        }

        loaderData.setSynsets(synsets);
        loaderData.setLexUnits(lexUnits);
        loaderData.setSynsetIdMap(synsetIdMap);
        loaderData.setLexUnitIdMap(lexUnitIdMap);
        loaderData.setCatSynsetMap(catSynsetMap);
        loaderData.setCatLexUnitMap(catLexUnitMap);
        loaderData.setWordCategoryMapAllOrthForms(wordCategoryMapAllOrthForms);
        loaderData.setLowerToUpperMap(lowerToUpperMap);

        return loaderData;
    }

    private static void processOrthForm(String orthForm, LexUnit lexUnit,
                                        Map<String, Set<LexUnit>> mapAllOrthForms,
                                        Object2ObjectMap<String, ObjectSet<String>> lowerToUpperMap) {
        String orthFormLower;
        ObjectSet<String> orthFormSet;
        Set<LexUnit> lexUnitSet;

        if (orthForm != null) {
            orthFormLower = orthForm.toLowerCase();
            orthFormSet = lowerToUpperMap.getOrDefault(orthFormLower, new ObjectOpenHashSet<>());
            orthFormSet.add(orthForm);
            orthFormSet.add(orthFormLower);
            lowerToUpperMap.put(orthFormLower, orthFormSet);
        }
        lexUnitSet = mapAllOrthForms.getOrDefault(orthForm, new ObjectOpenHashSet<>());
        lexUnitSet.add(lexUnit);
        mapAllOrthForms.put(orthForm, lexUnitSet);
    }
    /**
     * Filters out synset files by name.
     */
    private class SynsetFilter implements FilenameFilter {
        @Override
        public boolean accept(File directory, String name) {
            return (name.endsWith("xml") &&
                    (name.startsWith("nomen") ||
                            name.startsWith("verben") ||
                            name.startsWith("adj")));
        }
    }

    /**
     * Filters out relation files by name.
     */
    private class RelationFilter implements FilenameFilter {
        @Override
        public boolean accept(File directory, String name) {
            return (name.equals("gn_relations.xml"));
        }
    }
}
