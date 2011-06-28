/*
 * Copyright (C) 2011 Verena Henrich, Department of General and Computational
 * Linguistics, University of Tuebingen
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
package germanet;

import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Load <code>IliRecords</code> into a specified <code>GermaNet</code> object.
 *
 * @author Verena Henrich (verena.henrich at uni-tuebingen.de)
 * @version 7.0
 */
public class IliLoader {

    private GermaNet germaNet;
    private String namespace;

    /**
     * Constructs an <code>IliLoader</code> for the specified
     * <code>GermaNet</code> object.
     * @param germaNet the <code>GermaNet</code> object to load the
     * <code>IliRecords</code> into
     */
    protected IliLoader(GermaNet germaNet) {
        this.germaNet = germaNet;
    }

    /**
     * Loads <code>IliRecords</code> from the specified file into this
     * <code>IliLoader</code>'s <code>GermaNet</code> object.
     * @param iliFile the file containing <code>IliRecords</code> data
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    protected void loadILI(File iliFile) throws FileNotFoundException, XMLStreamException {
        InputStream in = new FileInputStream(iliFile);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(in);
        int event;
        String nodeName;
        System.out.println("Loading " +
                            iliFile.getName() + "...");

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
                        IliRecord ili = processIliRecord(parser);
                        germaNet.addIliRecord(ili);
                    }
                    break;
            }
        }
        parser.close();
        System.out.println("Done.");
    }

    /**
     * Returns the <code>IliRecord</code> for which the start tag was just encountered.
     * @param parser the <code>parser</code> being used on the current file
     * @return a <code>IliRecord</code> representing the data parsed
     * @throws javax.xml.stream.XMLStreamException
     */
    private IliRecord processIliRecord(XMLStreamReader parser) throws XMLStreamException {
        int iliId;
        int lexUnitId;
        String gnWord;
        String ewnRelation;
        String pwnWord;
        int pwn20Sense;
        String pwn20Id;
        String pwn30Id;
        String source;
        IliRecord curIli;
        List<String> englishSynonyms = new ArrayList<String>();
        boolean done = false;
        int event;
        String nodeName;

        iliId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_ID).substring(1));
        lexUnitId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_LEX_UNIT_ID).substring(1));
        gnWord = parser.getAttributeValue(namespace, GermaNet.XML_GN_WORD);
        ewnRelation = parser.getAttributeValue(namespace, GermaNet.XML_EWN_RELATION);
        pwnWord = parser.getAttributeValue(namespace, GermaNet.XML_PWN_WORD);
        pwn20Sense = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_PWN20_SENSE));
        pwn20Id = parser.getAttributeValue(namespace, GermaNet.XML_PWN20_ID);
        pwn30Id = parser.getAttributeValue(namespace, GermaNet.XML_PWN30_ID);
        source = parser.getAttributeValue(namespace, GermaNet.XML_SOURCE);

        // process this lexUnit
        while (parser.hasNext() && !done) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_PWN20_SYNONYM)) {
                        englishSynonyms.add(processEnglishExamples(parser));
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

        curIli = new IliRecord(iliId, lexUnitId, gnWord, ewnRelation, pwnWord, pwn20Sense, pwn20Id, pwn30Id, source);

        for (String synonym : englishSynonyms) {
            curIli.addEnglishSynonym(synonym);
        }

        return curIli;
    }

    private String processEnglishExamples (XMLStreamReader parser) throws XMLStreamException {
        String englishSynonym = parser.getElementText();
        return englishSynonym;
    }
}
