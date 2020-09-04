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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Load lex and con relations into a specified GermaNet object.
 * 
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
class RelationLoader {
    static final String DIR_BOTH = "both";
    static final String DIR_REVERT = "revert";

    /**
     * Loads relations from the specified file into this
     * <code>RelationLoader</code>'s <code>GermaNet</code> object.
     * @param relationFile file containing GermaNet relation data
     * @throws java.io.FileNotFoundException if the file is not found
     * @throws javax.xml.stream.XMLStreamException if there is a problem with the stream
     */
    static void loadRelations(File relationFile, Map<Integer, Synset> synsetIdMap, Map<Integer, LexUnit> lexUnitIdMap) throws FileNotFoundException, XMLStreamException {
        RelationLoader.loadRelations(new FileInputStream(relationFile), synsetIdMap, lexUnitIdMap);
    }

    /**
     * Loads relations from the specified file and
     * adds them to the corresponding Synsets and LexUnits.
     * @param inputStream containing GermaNet relation data
     * @param synsetIdMap map of all synset IDs to the Synsets
     * @param lexUnitIdMap map of all lexUnit IDs to the LexUnits
     * @throws XMLStreamException if there is a problem with the steam
     */
    static void loadRelations(InputStream inputStream,
                              Map<Integer, Synset> synsetIdMap,
                              Map<Integer, LexUnit> lexUnitIdMap) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(inputStream);
        String namespace = null;
        int event;
        String nodeName;

        /*
         * Parse entire file, looking for lex- and con- relations
         */
        while (parser.hasNext()) {
            event = parser.getEventType();
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    namespace = parser.getNamespaceURI();
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_LEX_REL)) {
                        processLexRel(parser, namespace, lexUnitIdMap);
                    } else if (nodeName.equals(GermaNet.XML_CON_REL)) {
                        processConRel(parser, namespace, synsetIdMap);
                    }
                    break;
            }
            parser.next();
        }
        parser.close();
    }

    /**
     * Processes the lexical relation for which the start tag was
     * just encountered.
     * @param parser the <code>XMLStreamParser</code> to get the attributes from
     */
    static private void processLexRel(XMLStreamReader parser, String namespace, Map<Integer, LexUnit> lexUnitIdMap) {
        String name, direction;
        int fromLexUnitId, toLexUnitId;
        LexUnit fromLexUnit, toLexUnit;
        LexRel invRel;

        // get all the attributes
        name = parser.getAttributeValue(namespace, GermaNet.XML_RELATION_NAME);
        LexRel rel = LexRel.valueOf(name);
        direction = parser.getAttributeValue(namespace, GermaNet.XML_RELATION_DIR);
        fromLexUnitId = Integer.parseInt(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_FROM).substring(1));
        toLexUnitId = Integer.parseInt(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_TO).substring(1));

        // look up the LexUnits
        fromLexUnit = lexUnitIdMap.get(fromLexUnitId);
        toLexUnit = lexUnitIdMap.get(toLexUnitId);

        // add outgoing relation "from" -> "to"
        fromLexUnit.addRelation(rel, toLexUnit, RelDirection.outgoing);

        // add incoming relation "to" <- "from"
        toLexUnit.addRelation(rel, fromLexUnit, RelDirection.incoming);

        // add the inverse relation, if any
        if (direction.equals(DIR_BOTH)) {
            toLexUnit.addRelation(rel, fromLexUnit, RelDirection.outgoing);
            fromLexUnit.addRelation(rel, toLexUnit, RelDirection.incoming);
        } else if (direction.equals(DIR_REVERT)) {
            invRel = LexRel.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_INV));
            toLexUnit.addRelation(invRel, fromLexUnit, RelDirection.outgoing);
            fromLexUnit.addRelation(invRel, toLexUnit, RelDirection.incoming);
        }
    }

    /**
     * Processes the conceptual relation for which the start tag was
     * just encountered.
     * @param parser the <code>XMLStreamReader</code> to get the attributes from
     */
    static private void processConRel(XMLStreamReader parser, String namespace, Map<Integer, Synset> synsetIdMap) {
        String name, direction;
        int fromSynsetId, toSynsetId;
        Synset fromSynset, toSynset;
        ConRel invRel;

        // get all the attributes
        name = parser.getAttributeValue(namespace, GermaNet.XML_RELATION_NAME);
        ConRel rel = ConRel.valueOf(name);
        direction = parser.getAttributeValue(namespace, GermaNet.XML_RELATION_DIR);
        fromSynsetId = Integer.parseInt(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_FROM).substring(1));
        toSynsetId = Integer.parseInt(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_TO).substring(1));

        // look up the Synsets
        fromSynset = synsetIdMap.get(fromSynsetId);
        toSynset = synsetIdMap.get(toSynsetId);

        // add outgoing relation "from" -> "to"
        fromSynset.addRelation(rel, toSynset, RelDirection.outgoing);

        // add incoming relation "to" <- "from"
        toSynset.addRelation(rel, fromSynset, RelDirection.incoming);

        // add the inverse relation, if any
        if (direction.equals(DIR_BOTH)) {
            toSynset.addRelation(rel, fromSynset, RelDirection.outgoing);
            fromSynset.addRelation(rel, toSynset, RelDirection.incoming);
        } else if (direction.equals(DIR_REVERT)) {
            invRel = ConRel.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_INV));
            toSynset.addRelation(invRel, fromSynset, RelDirection.outgoing);
            fromSynset.addRelation(invRel, toSynset, RelDirection.incoming);
        }
    }
}
