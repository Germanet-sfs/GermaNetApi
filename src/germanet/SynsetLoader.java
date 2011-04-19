/*
 * Copyright (C) 2009 Verena Henrich, Department of General and Computational
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
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Load <code>Synsets</code> into a specified <code>GermaNet</code> object.
 * 
 * @author Verena Henrich (verena.henrich at uni-tuebingen.de)
 * @version 2.0
 */
class SynsetLoader {
    private GermaNet germaNet;
    private String namespace;

    /**
     * Constructs a <code>SynsetLoader</code> for the specified
     * <code>GermaNet</code> object.
     * @param germaNet the <code>GermaNet</code> object to load the
     * <code>Synsets</code> into
     */
    protected SynsetLoader(GermaNet germaNet) {
        this.germaNet = germaNet;
    }

    /**
     * Loads <code>Synsets</code> from the specified file into this
     * <code>SynsetLoader</code>'s <code>GermaNet</code> object.
     * @param synsetFile the file containing <code>GermaNet Synset<code> data
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    protected void loadSynsets(File synsetFile) throws FileNotFoundException, XMLStreamException {
        InputStream in = new FileInputStream(synsetFile);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(in);
        int event;
        String nodeName;

        /*
         * Parse entire file, looking for synset start elements
         */
        while (parser.hasNext()) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    namespace = parser.getNamespaceURI();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_SYNSET)) {
                        Synset syn = processSynset(parser);
                        germaNet.addSynset(syn);
                    }
                    break;
            }
        }
        parser.close();
    }

    /**
     * Loads <code>Synsets</code> from the specified file into this
     * <code>SynsetLoader</code>'s <code>GermaNet</code> object.
     * @param synsetFile the file containing <code>GermaNet Synset<code> data
     * @throws javax.xml.stream.XMLStreamException
     */
    protected void loadSynsets(InputStream inputStream) throws XMLStreamException {
//        InputStream in = new FileInputStream(synsetFile);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        System.out.println("loadSynsets...");
        XMLStreamReader parser = factory.createXMLStreamReader(inputStream);
        int event;
        String nodeName;

        /*
         * Parse entire file, looking for synset start elements
         */
        while (parser.hasNext()) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    namespace = parser.getNamespaceURI();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_SYNSET)) {
                        Synset syn = processSynset(parser);
                        germaNet.addSynset(syn);
                    }
                    break;
            }
        }
        parser.close();
    }

    /**
     * Returns the <code>Synset</code> for which the start tag was just encountered.
     * @param parser the <code>parser</code> being used on the current file
     * @return a <code>Synset</code> representing the data parsed
     * @throws javax.xml.stream.XMLStreamException
     */
    private Synset processSynset(XMLStreamReader parser) throws XMLStreamException {
        int sID;
        WordCategory wordCategory;
        int event = -1;
        String nodeName;
        boolean done = false;
        LexUnit curLexUnit = null;
        Synset curSynset;
        String aParaphrase;
//        Example anExample;
//        Frame aFrame;

        // get the synset attributes
        sID = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_ID).substring(1));
        wordCategory = WordCategory.valueOf(parser.getAttributeValue(namespace,
                GermaNet.XML_WORD_CATEGORY));
        
        // create a new Synset with those attributes
        curSynset = new Synset(sID, wordCategory);

        // process this synset
        while (parser.hasNext() && !done) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    // process subtrees
                    if (nodeName.equals(GermaNet.XML_LEX_UNIT)) {
                        curLexUnit = processLexUnit(parser, curSynset);
                        curSynset.addLexUnit(curLexUnit);
//                        if (wordCategory.equals(WordCategory.nomen)) {
//                            System.out.println(curLexUnit.getOrthForm());
//                        }
//                    } else if (nodeName.equals(GermaNet.XML_FRAME)) {
//                        aFrame = new Frame(parser.getElementText());
//                        curLexUnit.addFrame(aFrame);
                    } else if (nodeName.equals(GermaNet.XML_PARAPHRASE)) {
                        aParaphrase = parser.getElementText();
                        curSynset.setParaphrase(aParaphrase);
//                    } else if (nodeName.equals(GermaNet.XML_EXAMPLE)) {
//                        anExample = processExample(parser);
//                        curLexUnit.addExample(anExample);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    nodeName = parser.getLocalName();
                    // quit when we reach the end synset tag
                    if (nodeName.equals(GermaNet.XML_SYNSET)) {
                        done = true;
                    }
                    break;
            }
        }
        return curSynset; // the generated synset

    }

    /**
     * Return the <code>LexUnit</code> for which the start tag was just encountered.
     * @param parser the <code>parser</code> being used on the current file
     * @param parentSynset the <code>Synset</code> to which this
     * <code>LexUnit</code> belongs
     * @return a <code>LexUnit</code> representing the data parsed
     * @throws javax.xml.stream.XMLStreamException
     */
    private LexUnit processLexUnit(XMLStreamReader parser, Synset parentSynset)
            throws XMLStreamException {
        boolean styleMarking, artificial, namedEntity;
        int id, sense;
        String source;
        String attVal;
        boolean done = false;
        int event;
        String nodeName;
        LexUnit curLexUnit;
        String orthForm = null;
        String orthVar = null;
        String oldOrthForm = null;
        String oldOrthVar = null;
        List<Example> examples = new ArrayList<Example>();
        List<Frame> frames = new ArrayList<Frame>();
        
        // get all the attributes
        id = Integer.parseInt(parser.getAttributeValue(namespace, GermaNet.XML_ID).substring(1));
//             Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_ID))

        styleMarking = artificial = namedEntity = false;
        attVal = parser.getAttributeValue(namespace, GermaNet.XML_STYLE_MARKING);
        if (attVal != null) {
            styleMarking = attVal.equals(GermaNet.YES);
        }

        attVal = parser.getAttributeValue(namespace, GermaNet.XML_ARTIFICIAL);
        if (attVal != null) {
            artificial = attVal.equals(GermaNet.YES);
        }

        attVal = parser.getAttributeValue(namespace, GermaNet.XML_NAMED_ENTITY);
        if (attVal != null) {
            namedEntity = attVal.equals(GermaNet.YES);
        }

        sense = Integer.parseInt(parser.getAttributeValue(namespace, GermaNet.XML_SENSE));

        source = parser.getAttributeValue(namespace, GermaNet.XML_SOURCE);

        // process this lexUnit
        while (parser.hasNext() && !done) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    // process subtrees
                    if (nodeName.equals(GermaNet.XML_ORTH_FORM)) {
                        orthForm = parser.getElementText();
                    } else if (nodeName.equals(GermaNet.XML_ORTH_VAR)) {
                        orthVar = parser.getElementText();
                    } else if (nodeName.equals(GermaNet.XML_OLD_ORTH_FORM)) {
                        oldOrthForm = parser.getElementText();
                    } else if (nodeName.equals(GermaNet.XML_OLD_ORTH_VAR)) {
                        oldOrthVar = parser.getElementText();
                    } else if (nodeName.equals(GermaNet.XML_FRAME)) {
                        frames.add(new Frame(parser.getElementText()));
                    } else if (nodeName.equals(GermaNet.XML_EXAMPLE)) {
                        examples.add(processExample(parser));
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    nodeName = parser.getLocalName();
                    // quit when we reach the end lexUnit tag
                    if (nodeName.equals(GermaNet.XML_LEX_UNIT)) {
                        done = true;
                    }
                    break;
            }
        }

        // create a new LexUnit with those attributes
        curLexUnit = new LexUnit(id, parentSynset, sense, styleMarking, artificial,
                orthForm, orthVar, oldOrthForm, oldOrthVar, namedEntity, source);

        for (Frame frame : frames) {
            curLexUnit.addFrame(frame);
        }

        for (Example example : examples) {
            curLexUnit.addExample(example);
        }

        return curLexUnit;
    }

    /**
     * Return an <code>Example</code> object representing the example for which
     * the start tag was just encountered.
     * @param parser the <code>parser</code> being used on the current file
     * @return an </code>Example</code> object
     * @throws javax.xml.stream.XMLStreamException
     */
    private Example processExample(XMLStreamReader parser) throws XMLStreamException {
        boolean done = false;
        int event;
        String nodeName;
        Example curExample = new Example();

        while (parser.hasNext() && !done) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    // process text and frame subtrees
                    if (nodeName.equals(GermaNet.XML_TEXT)) {
                        curExample.setText(parser.getElementText());
                    } else if (nodeName.equals(GermaNet.XML_EXFRAME)) {
                        curExample.setFrame(new Frame(parser.getElementText()));
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    nodeName = parser.getLocalName();
                    // quit when we reach the example end tag
                    if (nodeName.equals(GermaNet.XML_EXAMPLE)) {
                        done = true;
                    }
                    break;
            }
        }
        return curExample;
    }
}