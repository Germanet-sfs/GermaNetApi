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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Load <code>WiktionaryParaphrases</code> into a specified <code>GermaNet</code> object.
 *
 * @author Verena Henrich (verena.henrich at uni-tuebingen.de)
 * @version 7.0
 */
public class WiktionaryLoader {

    private GermaNet germaNet;
    private String namespace;

    /**
     * Constructs an <code>WiktionaryLoader</code> for the specified
     * <code>GermaNet</code> object.
     * @param germaNet the <code>GermaNet</code> object to load the
     * <code>WiktionaryParaphrases</code> into
     */
    protected WiktionaryLoader(GermaNet germaNet) {
        this.germaNet = germaNet;
    }

    /**
     * Loads <code>WiktionaryParaphrases</code> from the specified file into this
     * <code>WiktionaryLoader</code>'s <code>GermaNet</code> object.
     * @param wiktionaryFile the file containing <code>WiktionaryParaphrases</code> data
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    protected void loadWiktionary(File wiktionaryFile) throws FileNotFoundException, XMLStreamException {
        InputStream in = new FileInputStream(wiktionaryFile);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(in);
        int event;
        String nodeName;
        System.out.println("Loading " +
                            wiktionaryFile.getName() + "...");

        //Parse entire file, looking for wictionary paraphrase start elements
        while (parser.hasNext()) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    namespace = parser.getNamespaceURI();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_WIKTONARY_PARAPHRASE)) {
                        WiktionaryParaphrase wiki = processWictionaryParaphrase(parser);
                        germaNet.addWictionaryParaphrase(wiki);
                    }
                    break;
            }
        }
        parser.close();
        System.out.println("Done.");
    }

    /**
     * Returns the <code>WiktionaryParaphrase</code> for which the start tag was just encountered.
     * @param parser the <code>parser</code> being used on the current file
     * @return a <code>WiktionaryParaphrase</code> representing the data parsed
     * @throws javax.xml.stream.XMLStreamException
     */
    private WiktionaryParaphrase processWictionaryParaphrase(XMLStreamReader parser) throws XMLStreamException {
        int lexUnitId;
        int wiktionaryId;
        int wiktionarySenseId;
        String wiktionarySense;
        boolean edited = false;
        WiktionaryParaphrase curWiki;

        lexUnitId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_LEX_UNIT_ID).substring(1));
        wiktionaryId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_WIKTONARY_ID));
        wiktionarySenseId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_WIKTONARY_SENSE_ID));
        wiktionarySense = parser.getAttributeValue(namespace, GermaNet.XML_WIKTONARY_SENSE);

        String edit = parser.getAttributeValue(namespace, GermaNet.XML_WIKTONARY_EDITED);
        if (edit.equals(GermaNet.YES)) {
            edited = true;
        }

        curWiki = new WiktionaryParaphrase(lexUnitId, wiktionaryId, wiktionarySenseId,
                wiktionarySense, edited);

        return curWiki;
    }
}