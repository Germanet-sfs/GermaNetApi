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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;

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
    private File germaNetDir;
    private List<InputStream> germaNetStreams;
    private SynsetLoader synLoader;  // loader for synsets
    private RelationLoader relLoader; // loader for relations
    private List<String> xmlNames;

    /**
     * Constructs a <code>StaxLoader</code> for data files in directory
     * <code>germanetDirectory</code> and existing <code>GermaNet</code> object
     * <code>germaNet</code>.
     *
     * @param germaNetDir location of GermaNet data files
     * @param germaNet    <code>GermaNet</code> object to load into
     * @throws java.io.FileNotFoundException
     */
    protected StaxLoader(File germaNetDir, GermaNet germaNet) throws
            FileNotFoundException {
        this.germaNetDir = germaNetDir;
        this.germaNetStreams = null;
        this.synLoader = new SynsetLoader(germaNet);
        this.relLoader = new RelationLoader(germaNet);
        this.xmlNames = null;

        if (!germaNetDir.isDirectory()) {
            throw new FileNotFoundException("Unable to load GermaNet from \"" + germaNetDir + "\"");
        }
    }

    /**
     * Constructs a <code>StaxLoader</code> for data streams in
     * <code>germanetStreams</code> and existing <code>GermaNet</code> object
     * <code>germaNet</code>.
     *
     * @param germaNetStreams location of GermaNet data files
     * @param germaNet        <code>GermaNet</code> object to load into
     * @throws java.io.FileNotFoundException
     */
    protected StaxLoader(List<InputStream> germaNetStreams, List<String> xmlNames, GermaNet germaNet) throws
            FileNotFoundException {
        this.germaNetStreams = germaNetStreams;
        this.germaNetDir = null;
        this.synLoader = new SynsetLoader(germaNet);
        this.relLoader = new RelationLoader(germaNet);
        this.xmlNames = xmlNames;

        if (germaNetStreams.isEmpty()) {
            throw new FileNotFoundException("Unable to load GermaNet data");
        }
    }

    /**
     * Loads all synset files or streams (depending on what exists) and then all relation files.
     *
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    protected void load() throws XMLStreamException, FileNotFoundException {

        int loadedFiles = 0;
        if (this.germaNetDir != null) { // load GermaNet from file
            FilenameFilter filter = new SynsetFilter(); //get only synset files

            File[] germaNetFiles = germaNetDir.listFiles(filter);

            if (germaNetFiles == null || germaNetFiles.length == 0) {
                throw new FileNotFoundException("Unable to load GermaNet from \"" + this.germaNetDir.getPath() + "\"");
            }

            // load all synset files first with a SynsetLoader
            for (int i = 0; i < germaNetFiles.length; i++) {
                LOGGER.info("Loading " + germaNetFiles[i].getName() + "...");
                synLoader.loadSynsets(germaNetFiles[i]);
                loadedFiles++;
            }

            filter = new RelationFilter(); //get only relation files
            germaNetFiles = germaNetDir.listFiles(filter);

            // load relations with a RelationLoader
            for (int i = 0; i < germaNetFiles.length; i++) {
                LOGGER.info("Loading " + germaNetFiles[i].getName() + "...");
                relLoader.loadRelations(germaNetFiles[i]);
                loadedFiles++;
            }
        } else { // load GermaNet from InputStream list
            if (germaNetStreams == null || germaNetStreams.isEmpty()) {
                throw new FileNotFoundException("Unable to load GermaNet data.");
            }

            // load all synset input streams first with a SynsetLoader
            for (int i = 0; i < germaNetStreams.size(); i++) {
                if (xmlNames.get(i).endsWith("xml")
                        && (xmlNames.get(i).startsWith("nomen")
                        || xmlNames.get(i).startsWith("verben")
                        || xmlNames.get(i).startsWith("adj"))) {
                    LOGGER.info("Loading input stream " + xmlNames.get(i) + "...");
                    synLoader.loadSynsets(germaNetStreams.get(i));
                    loadedFiles++;
                }
            }

            // load relations with a RelationLoader
            for (int i = 0; i < germaNetStreams.size(); i++) {
                if (xmlNames.get(i).equals("gn_relations.xml")) {
                    LOGGER.info("Loading input stream " + xmlNames.get(i) + "...");
                    relLoader.loadRelations(germaNetStreams.get(i));
                    loadedFiles++;
                }
            }
        }

        if (loadedFiles >= NUMBER_OF_GERMANET_FILES) {
            LOGGER.info("Done loading {} GermaNet files.", loadedFiles);
        } else {
            throw new FileNotFoundException("GermaNet data not found or files are missing.");
        }
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
