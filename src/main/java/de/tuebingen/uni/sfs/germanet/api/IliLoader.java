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

import java.util.*;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Load <code>IliRecords</code> for <code>GermaNet</code>.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
class IliLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(IliLoader.class);

    /**
     * Loads <code>IliRecords</code> from the specified stream and
     * adds them to their corresponding LexUnit.
     *
     * @param loaderData the loading data that contains the InputStream to read
     *                   and the LexUnits to update
     * @return a List of the loaded IliRecords
     * @throws XMLStreamException if there is a problem with the stream
     */
    static LoaderData loadILI(LoaderData loaderData) throws XMLStreamException {

        // nothing to do if the stream is null
        InputStream inputStream = loaderData.getIliInputStream();
        if (inputStream == null) {
            return loaderData;
        }

        Int2ObjectMap<LexUnit> lexUnitIDMap = loaderData.getLexUnitIdMap();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(inputStream);
        String namespace = null;
        int event;
        String nodeName;
        int iliCnt = 0;
        List<IliRecord> iliRecords = new ObjectArrayList<>();

        LOGGER.info("Loading interLingualIndex_DE-EN.xml...");

        //Parse entire file, looking for ili record start elements
        while (parser.hasNext()) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    namespace = parser.getNamespaceURI();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_ILI_RECORD)) {
                        IliRecord ili = processIliRecord(parser, namespace);
                        iliRecords.add(ili);
                        int lexUnitId = ili.getLexUnitId();
                        LexUnit lexUnit = lexUnitIDMap.getOrDefault(lexUnitId, null);
                        if (lexUnit != null) {
                            lexUnit.addIliRecord(ili);
                            lexUnitIDMap.put(lexUnitId, lexUnit);
                            iliCnt++;
                        }
                    }
                    break;
            }
        }
        parser.close();
        LOGGER.info("Done loading {} ILI records.", iliCnt);

        loaderData.setIliRecords(iliRecords);
        return loaderData;
    }

    /**
     * Returns the <code>IliRecord</code> for which the start tag was just encountered.
     *
     * @param parser the <code>parser</code> being used on the current file
     * @param namespace the namespace to use
     * @return a <code>IliRecord</code> representing the data parsed
     * @throws XMLStreamException if there is a problem with the stream
     */
    private static IliRecord processIliRecord(XMLStreamReader parser, String namespace) throws XMLStreamException {
        int lexUnitId;
        String ewnRelation;
        String pwnWord;
        String pwn20Id;
        String pwn30Id;
        String pwn20paraphrase = "";
        String source;
        IliRecord curIli;
        List<String> englishSynonyms = new ArrayList<String>();
        boolean done = false;
        int event;
        String nodeName;
        lexUnitId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_LEX_UNIT_ID).substring(1));
        ewnRelation = parser.getAttributeValue(namespace, GermaNet.XML_EWN_RELATION);
        pwnWord = parser.getAttributeValue(namespace, GermaNet.XML_PWN_WORD);
        pwn20Id = parser.getAttributeValue(namespace, GermaNet.XML_PWN20_ID);
        pwn30Id = parser.getAttributeValue(namespace, GermaNet.XML_PWN30_ID);
        pwn20paraphrase = parser.getAttributeValue(namespace, GermaNet.XML_PWN20_PARAPHRASE);

        source = parser.getAttributeValue(namespace, GermaNet.XML_SOURCE);

        // process this lexUnit
        while (parser.hasNext() && !done) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_PWN20_SYNONYM)) {
                        englishSynonyms.add(parser.getElementText());
                    }
                case XMLStreamConstants.END_ELEMENT:
                    nodeName = parser.getLocalName();
                    // quit when we reach the end lexUnit tag
                    if (nodeName.equals(GermaNet.XML_ILI_RECORD)) {
                        done = true;
                    }
                    break;
            }
        }

        curIli = new IliRecord(lexUnitId, EwnRel.valueOf(ewnRelation), pwnWord, pwn20Id, pwn30Id, pwn20paraphrase, source);

        for (String synonym : englishSynonyms) {
            curIli.addEnglishSynonym(synonym);
        }

        return curIli;
    }
}
