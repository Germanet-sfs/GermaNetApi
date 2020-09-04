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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Load <code>WiktionaryParaphrases</code> for <code>GermaNet</code>.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
class WiktionaryLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(WiktionaryLoader.class);

    /**
     * Loads <code>WiktionaryParaphrases</code> from the given streams and
     * adds them to their corresponding LexUnit.
     *
     * @param loaderData the loading data that contains the InputStream to read
     *                   and the LexUnits to update
     * @return a List of the loaded WiktionaryParaphrases
     * @throws XMLStreamException if there is a problem with the stream
     */
    static LoaderData loadWiktionary(LoaderData loaderData) throws XMLStreamException {

        List<InputStream> wiktStreams = loaderData.getWiktInputStreams();

        // nothing to do if there is no data to read
        if (wiktStreams.isEmpty()) {
            return loaderData;
        }

        List<String> wiktNames = loaderData.getWiktXmlNames();
        Int2ObjectMap<LexUnit> lexUnitIDMap = loaderData.getLexUnitIdMap();

        String namespace = null;
        List<WiktionaryParaphrase> wiktionaryParaphrases = new ObjectArrayList<>();
        int wiktCnt = 0;

        for (int i = 0; i < wiktStreams.size(); i++) {
            LOGGER.info("Loading wiktionary stream " + wiktNames.get(i) + "...");
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(wiktStreams.get(i));
            int event;
            String nodeName;

            //Parse entire stream, looking for Wiktionary paraphrase start elements
            while (parser.hasNext()) {
                event = parser.next();
                switch (event) {
                    case XMLStreamConstants.START_DOCUMENT:
                        namespace = parser.getNamespaceURI();
                        break;
                    case XMLStreamConstants.START_ELEMENT:
                        nodeName = parser.getLocalName();
                        if (nodeName.equals(GermaNet.XML_WIKTIONARY_PARAPHRASE)) {
                            WiktionaryParaphrase wikt = processWiktionaryParaphrase(parser, namespace);
                            wiktionaryParaphrases.add(wikt);
                            int lexUnitId = wikt.getLexUnitId();
                            LexUnit lexUnit = lexUnitIDMap.getOrDefault(lexUnitId, null);
                            if (lexUnit != null) {
                                lexUnit.addWiktionaryParaphrase(wikt);
                                lexUnitIDMap.put(lexUnitId, lexUnit);
                                wiktCnt++;
                            }
                        }
                        break;
                }
            }
            parser.close();
        }

        LOGGER.info("Done loading {} wiktionary records.", wiktCnt);
        loaderData.setWiktionaryParaphrases(wiktionaryParaphrases);
        return loaderData;
    }

    /**
     * Returns the <code>WiktionaryParaphrase</code> for which the start tag was just encountered.
     *
     * @param parser the <code>parser</code> being used on the current file
     * @param namespace the namespace to use
     * @return a <code>WiktionaryParaphrase</code> representing the data parsed
     * @throws XMLStreamException if there is a problem with the stream
     */
    private static WiktionaryParaphrase processWiktionaryParaphrase(XMLStreamReader parser, String namespace) throws XMLStreamException {
        int lexUnitId;
        int wiktionaryId;
        int wiktionarySenseId;
        String wiktionarySense;
        boolean edited = false;
        WiktionaryParaphrase curWikt;

        lexUnitId = Integer.parseInt(parser.getAttributeValue(namespace, GermaNet.XML_LEX_UNIT_ID).substring(1));
        wiktionaryId = Integer.parseInt(parser.getAttributeValue(namespace, GermaNet.XML_WIKTIONARY_ID).substring(1));
        wiktionarySenseId = Integer.parseInt(parser.getAttributeValue(namespace, GermaNet.XML_WIKTIONARY_SENSE_ID));
        wiktionarySense = parser.getAttributeValue(namespace, GermaNet.XML_WIKTIONARY_SENSE);

        String edit = parser.getAttributeValue(namespace, GermaNet.XML_WIKTIONARY_EDITED);
        if (edit.equals(GermaNet.YES)) {
            edited = true;
        }

        curWikt = new WiktionaryParaphrase(lexUnitId, wiktionaryId, wiktionarySenseId,
                wiktionarySense, edited);

        return curWikt;
    }
}
