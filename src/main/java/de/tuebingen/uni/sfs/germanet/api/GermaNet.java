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

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.stream.XMLStreamException;

/**
 * Provides high-level look-up access to GermaNet data. Intended as a read-only
 * resource - no public methods are provided for changing or adding data.<br><br>
 * <p>
 * GermaNet is a collection of German lexical units (<code>LexUnits</code>)
 * organized into sets of synonyms (<code>Synsets</code>).<br>
 * A <code>Synset</code> has a
 * <code>WordCategory</code> (adj, nomen, verben) and consists of a paraphrase
 * and Lists of <code>LexUnit</code>s. The List of <code>LexUnit</code>s is
 * never empty.<br>
 * A <code>LexUnit</code> consists of an orthForm (represented as a Strings),
 * an orthVar (can be empty), an oldOrthForm (can be empty), and an oldOrthVar
 * (can be empty). <code>Examples</code>, <code>Frames</code>, <code>IliRecords</code>,
 * and <code>WiktionaryParaphrases</code> can belong to a
 * <code>LexUnit</code> as well as the following
 * attributes: styleMarking (boolean), sense (int), styleMarking (boolean),
 * artificial (boolean), namedEntity (boolean), and source (String).<br>
 * A <code>Frame</code> is simply a container for frame data (String).<br>
 * An <code>Example</code> consists of text (String) and zero or one
 * <code>Frame</code>(s).<br><br>
 * <p>
 * To construct a <code>GermaNet</code> object, provide the location of the
 * GermaNet data and (optionally) a flag indicating whether searches should be
 * done ignoring case. This data location can be set with a <code>String</code>
 * representing the path to the directory containing the data, or with a
 * <code>File</code> object. If no flag is used, then case-sensitive
 * searching will be performed:<br><br>
 * <code>
 * // Use case-sensitive searching<br>
 * GermaNet gnet = new GermaNet("/home/myName/germanet/GN_V130");<br>
 * </code>
 * or<br>
 * <code>
 * // Ignore case when searching<br>
 * File gnetDir = new File("/home/myName/germanet/GN_V130");<br>
 * GermaNet gnet = new GermaNet(gnetDir, true);<br><br>
 * </code>
 * The <code>GermaNet</code> class has methods that return <code>Lists</code> of
 * <code>Synsets</code> or <code>LexUnits</code>, given
 * an orthForm or a WordCategory.  For example,<br><br><code>
 * List&lt;LexUnit&gt; lexList = gnet.getLexUnits("Bank");<br>
 * List&lt;LexUnit&gt;> verbenLU = gnet.getLexUnits(WordCategory.verben);<br>
 * List&lt;Synset&gt; synList = gnet.getSynsets("gehen");<br>
 * List&lt;Synset&gt; adjSynsets = gnet.getSynsets(WordCategory.adj);<br><br>
 * </code>
 * <p>
 * Unless otherwise stated, methods will return an empty List rather than null
 * to indicate that no objects exist for the given request. <br><br>
 *
 * <b>Important Note:</b><br>
 * Loading GermaNet requires more memory than the JVM allocates by default. Any
 * application that loads GermaNet will most likely need to be run with JVM
 * options that increase the memory allocated, like this:<br><br>
 *
 * <code>java -Xms1g -Xmx1g MyApplication</code><br><br>
 * <p>
 * Depending on the memory needs of the application itself, the 1g's may
 * need to be changed to something higher.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
public class GermaNet {

    private static final Logger LOGGER = LoggerFactory.getLogger(GermaNet.class);
    public static final String XML_SYNSETS = "synsets";
    public static final String XML_SYNSET = "synset";
    public static final String XML_ID = "id";
    public static final String XML_PARAPHRASE = "paraphrase";
    public static final String XML_WORD_CATEGORY = "category";
    public static final String XML_WORD_CLASS = "class";
    public static final String XML_LEX_UNIT = "lexUnit";
    public static final String XML_ORTH_FORM = "orthForm";
    public static final String XML_ORTH_VAR = "orthVar";
    public static final String XML_OLD_ORTH_FORM = "oldOrthForm";
    public static final String XML_OLD_ORTH_VAR = "oldOrthVar";
    public static final String XML_SOURCE = "source";
    public static final String XML_SENSE = "sense";
    public static final String XML_STYLE_MARKING = "styleMarking";
    public static final String XML_NAMED_ENTITY = "namedEntity";
    public static final String XML_ARTIFICIAL = "artificial";
    public static final String XML_EXAMPLE = "example";
    public static final String XML_TEXT = "text";
    public static final String XML_EXFRAME = "exframe";
    public static final String XML_FRAME = "frame";
    public static final String XML_RELATIONS = "relations";
    public static final String XML_RELATION = "relation";
    public static final String XML_CON_REL = "con_rel";
    public static final String XML_LEX_REL = "lex_rel";
    public static final String XML_RELATION_NAME = "name";
    public static final String XML_RELATION_DIR = "dir";
    public static final String XML_RELATION_INV = "inv";
    public static final String XML_RELATION_TO = "to";
    public static final String XML_RELATION_FROM = "from";

    //for ILI
    public static final String XML_ILI_RECORD = "iliRecord";
    public static final String XML_LEX_UNIT_ID = "lexUnitId";
    public static final String XML_EWN_RELATION = "ewnRelation";
    public static final String XML_PWN_WORD = "pwnWord";
    public static final String XML_PWN20_SENSE = "pwn20Sense";
    public static final String XML_PWN20_ID = "pwn20Id";
    public static final String XML_PWN30_ID = "pwn30Id";
    public static final String XML_PWN20_PARAPHRASE = "pwn20paraphrase";
    public static final String XML_PWN20_SYNONYMS = "pwn20Synonyms";
    public static final String XML_PWN20_SYNONYM = "pwn20Synonym";
    public static final String YES = "yes";
    public static final String NO = "no";

    //for Wiktionary
    public static final String XML_WIKTIONARY_PARAPHRASE = "wiktionaryParaphrase";
    public static final String XML_WIKTIONARY_ID = "wiktionaryId";
    public static final String XML_WIKTIONARY_SENSE_ID = "wiktionarySenseId";
    public static final String XML_WIKTIONARY_SENSE = "wiktionarySense";
    public static final String XML_WIKTIONARY_EDITED = "edited";
    public static final String XML_WIKTIONARY_POS = "pos";

    //for Compounds
    public static final String XML_COMPOUND = "compound";
    public static final String XML_PROPERTY = "property";
    public static final String XML_CATEGORY = "category";
    public static final String XML_COMPOUND_MODIFIER = "modifier";
    public static final String XML_COMPOUND_HEAD = "head";

    // number of GermaNet files
    public static final int NUMBER_OF_GERMANET_FILES = 55;

    private EnumMap<WordCategory, HashMap<String, ArrayList<LexUnit>>> wordCategoryMap;
    private EnumMap<WordCategory, HashMap<String, ArrayList<LexUnit>>> wordCategoryMapAllOrthForms;
    private HashMap<String, Set<String>> lowerToUpperMap;
    private TreeSet<Synset> synsets;
    private ArrayList<IliRecord> iliRecords;
    private ArrayList<WiktionaryParaphrase> wiktionaryParaphrases;
    private HashMap<Integer, LexUnit> lexUnitID;
    private HashMap<Integer, Synset> synsetID;
    private File dir = null;
    List<InputStream> inputStreams;
    List<String> xmlNames;
    List<InputStream> wiktInputStreams;
    List<String> wiktXmlNames;
    InputStream iliInputStream;
    String iliXmlName;
    InputStream relsInputStream;
    String relsXmlName;
    private boolean ignoreCase;
    private static final int MAX_LEVENSHTEIN_DIST = 30;
    private LevenshteinDistance levenshteinDistance;

    /**
     * Constructs a new <code>GermaNet</code> object by loading the the data
     * files in the specified directory/archive path name - searches are case sensitive.
     *
     * @param dirName the directory where the GermaNet data files are located
     * @throws javax.xml.stream.XMLStreamException
     * @throws java.io.IOException
     */
    public GermaNet(String dirName) throws IOException, XMLStreamException {
        this(new File(dirName), false);
    }

    /**
     * Constructs a new <code>GermaNet</code> object by loading the the data
     * files in the specified directory/archive path name.
     *
     * @param dirName    the directory where the GermaNet data files are located
     * @param ignoreCase if true ignore case on lookups, otherwise do case
     *                   sensitive searches
     * @throws javax.xml.stream.XMLStreamException
     * @throws java.io.IOException
     */
    public GermaNet(String dirName, boolean ignoreCase) throws XMLStreamException, IOException {
        this(new File(dirName), ignoreCase);
    }

    /**
     * Constructs a new <code>GermaNet</code> object by loading the the data
     * files in the specified directory/archive File - searches are case sensitive.
     *
     * @param dir location of the GermaNet data files
     * @throws javax.xml.stream.XMLStreamException
     * @throws java.io.IOException
     */
    public GermaNet(File dir) throws XMLStreamException, IOException {
        this(dir, false);
    }

    /**
     * Constructs a new <code>GermaNet</code> object by loading the the data
     * files in the specified directory/archive File.
     *
     * @param dir        location of the GermaNet data files
     * @param ignoreCase if true ignore case on lookups, otherwise do case
     *                   sensitive searches
     * @throws javax.xml.stream.XMLStreamException
     * @throws java.io.IOException
     */
    public GermaNet(File dir, boolean ignoreCase) throws XMLStreamException, IOException {
        checkMemory();
        this.ignoreCase = ignoreCase;
        this.inputStreams = new ArrayList<>();
        this.xmlNames = new ArrayList<>();
        this.wiktInputStreams = new ArrayList<>();
        this.wiktXmlNames = new ArrayList<>();
        this.iliInputStream = null;
        this.iliXmlName = null;
        this.synsets = new TreeSet<>();
        this.iliRecords = new ArrayList<>();
        this.wiktionaryParaphrases = new ArrayList<>();
        this.synsetID = new HashMap<>();
        this.lexUnitID = new HashMap<>();
        this.wordCategoryMap = new EnumMap<>(WordCategory.class);
        this.wordCategoryMapAllOrthForms = new EnumMap<>(WordCategory.class);
        this.lowerToUpperMap = new HashMap<>();
        this.levenshteinDistance = new LevenshteinDistance(MAX_LEVENSHTEIN_DIST);

        if (!dir.isDirectory() && isZipFile(dir)) {
            ZipFile zipFile = new ZipFile(dir);
            Enumeration entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String name = entry.getName();
                if (name.split(File.separator).length > 1) {
                    name = name.split(File.separator)[name.split(File.separator).length - 1];
                }
                InputStream stream = zipFile.getInputStream(entry);
                addStreamToLists(name, stream);
            }
        } else {
            this.dir = dir;
            File[] allFiles = dir.listFiles();
            for (int i = 0; i < allFiles.length; i++) {
                InputStream stream = new FileInputStream(allFiles[i]);
                String name = allFiles[i].getName();
                addStreamToLists(name, stream);
            }
        }
        load();
    }

    /**
     * Add the given stream to the correct stream list for loading,
     * based on its name.
     *
     * @param fileName
     * @param stream
     */
    private void addStreamToLists(String fileName, InputStream stream) {
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

    /**
     * Prints warning if available memory is low.
     */
    private void checkMemory() {
        long freeMemory = Runtime.getRuntime().freeMemory() / 1000000;
        if (freeMemory < 120) {
            LOGGER.warn("You may not have enough memory to load GermaNet.\n"
                    + "Try using \"-Xms1g -Xmx1g\" JVM options:");
        }
    }

    /**
     * Loads the data files into this <code>GermaNet</code> object.
     *
     * @throws javax.xml.stream.XMLStreamException
     */
    void load() throws FileNotFoundException, XMLStreamException {
        StaxLoader loader;
        String oldVal = null;

        // use xerces xml parser
        oldVal = System.getProperty("javax.xml.stream.XMLInputFactory");
        System.setProperty("javax.xml.stream.XMLInputFactory",
                "com.sun.xml.internal.stream.XMLInputFactoryImpl");

        // load data
        loader = new StaxLoader(this);
        loader.load();
        loadIli();
        loadWiktionaryParaphrases();
        trimAll();

        // set parser back to whatever it was before
        if (oldVal != null) {
            System.setProperty("javax.xml.stream.XMLInputFactory", oldVal);
        }
    }

    /**
     * Gets the absolute path name of the directory where the GermaNet data files
     * are stored.
     *
     * @return the absolute pathname of the location of the GermaNet data files
     */
    public String getDir() {
        if (this.dir != null) {
            return this.dir.getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * Adds a <code>Synset</code> to this <code>GermaNet</code>'s
     * <code>Synset</code> list.
     *
     * @param synset the <code>Synset</code> to add
     */
    protected void addSynset(Synset synset) {
        ArrayList<LexUnit> luList;
        HashMap<String, ArrayList<LexUnit>> map;
        HashMap<String, ArrayList<LexUnit>> mapAllOrthForms;

        // add synset to synset list and synsetID map
        synsets.add(synset);
        synsetID.put(synset.getId(), synset);

        // add synset to its wordCategory map
        map = wordCategoryMap.get(synset.getWordCategory());
        mapAllOrthForms = wordCategoryMapAllOrthForms.get(synset.getWordCategory());
        if (map == null) {
            map = new HashMap<String, ArrayList<LexUnit>>();
        }
        if (mapAllOrthForms == null) {
            mapAllOrthForms = new HashMap<String, ArrayList<LexUnit>>();
        }

        // add LexUnits of synset to lexUnitID map and add mapping
        // from orthForm to corresponding LexUnits
        for (LexUnit lu : synset.getLexUnits()) {

            lexUnitID.put(lu.getId(), lu);

            // add orthForm and lowercase orthForm to lowerToUpperMap
            String orthForm = lu.getOrthForm();
            String orthFormLower = orthForm.toLowerCase();
            Set<String> orthFormSet = lowerToUpperMap.get(orthFormLower);
            if (orthFormSet == null) {
                orthFormSet = new HashSet<String>();
            }
            orthFormSet.add(orthForm);
            orthFormSet.add(orthFormLower);
            lowerToUpperMap.put(orthFormLower, orthFormSet);

            luList = map.get(orthForm);
            if (luList == null) {
                luList = new ArrayList<LexUnit>();
            }
            luList.add(lu);
            map.put(orthForm, luList);

            luList = mapAllOrthForms.get(orthForm);
            if (luList == null) {
                luList = new ArrayList<LexUnit>();
            }
            luList.add(lu);
            mapAllOrthForms.put(orthForm, luList);

            // get orthVar
            // add orthVar and lowercase orthVar to lowerToUpperMap
            String orthVar = lu.getOrthVar();
            if (orthVar != null) {
                String orthVarLower = orthVar.toLowerCase();
                Set<String> orthVarSet = lowerToUpperMap.get(orthVarLower);
                if (orthVarSet == null) {
                    orthVarSet = new HashSet<String>();
                }
                orthVarSet.add(orthVar);
                orthVarSet.add(orthVarLower);
                lowerToUpperMap.put(orthVarLower, orthVarSet);
            }
            luList = mapAllOrthForms.get(orthVar);
            if (luList == null) {
                luList = new ArrayList<LexUnit>();
            }
            luList.add(lu);
            mapAllOrthForms.put(orthVar, luList);

            // get oldOrthForm
            // add oldOrthForm and lowercase oldOrthForm to lowerToUpperMap
            String oldOrthForm = lu.getOldOrthForm();
            if (oldOrthForm != null) {
                String oldOrthFormLower = oldOrthForm.toLowerCase();
                Set<String> oldOrthFormSet = lowerToUpperMap.get(oldOrthFormLower);
                if (oldOrthFormSet == null) {
                    oldOrthFormSet = new HashSet<String>();
                }
                oldOrthFormSet.add(oldOrthForm);
                oldOrthFormSet.add(oldOrthFormLower);
                lowerToUpperMap.put(oldOrthFormLower, oldOrthFormSet);
            }
            luList = mapAllOrthForms.get(oldOrthForm);
            if (luList == null) {
                luList = new ArrayList<LexUnit>();
            }
            if (!luList.contains(lu)) {
                luList.add(lu);
                mapAllOrthForms.put(oldOrthForm, luList);
            }

            // get oldOrthVar
            // add oldOrthVar and lowercase oldOrthVar to lowerToUpperMap
            String oldOrthVar = lu.getOldOrthVar();
            if (oldOrthVar != null) {
                String oldOrthVarLower = oldOrthVar.toLowerCase();
                Set<String> oldOrthVarSet = lowerToUpperMap.get(oldOrthVarLower);
                if (oldOrthVarSet == null) {
                    oldOrthVarSet = new HashSet<String>();
                }
                oldOrthVarSet.add(oldOrthVar);
                oldOrthVarSet.add(oldOrthVarLower);
                lowerToUpperMap.put(oldOrthVarLower, oldOrthVarSet);
            }
            luList = mapAllOrthForms.get(oldOrthVar);
            if (luList == null) {
                luList = new ArrayList<LexUnit>();
            }
            if (!luList.contains(lu)) {
                luList.add(lu);
                mapAllOrthForms.put(oldOrthVar, luList);
            }
        }
        wordCategoryMap.put(synset.getWordCategory(), map);
        wordCategoryMapAllOrthForms.put(synset.getWordCategory(), mapAllOrthForms);
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> using the specified
     * <code>FilterConfig</code>.
     *
     * @param filter a <code>FilterConfig</code> to use for the search
     * @return a <code>List</code> of all <code>Synsets</code> using the specified
     * <code>FilterConfig</code>. If no <code>Synsets</code> were found, this
     * is an empty <code>List</code>.
     */
    public List<Synset> getSynsets(FilterConfig filter) {
        return getSynsets(filter, getSynsets());
    }

    /**
     * Returns a <code>List</code> of <code>Synsets</code> in the given <code>Collection</code> of <code>Synset</code>
     * that satisfy the specified <code>FilterConfig</code>.
     *
     * @param filter  a <code>FilterConfig</code> to use for the search
     * @param synsets a <code>Collection</code> of <code>Synset</code> to search
     * @return a <code>List</code> of <code>Synset</code> in the given <code>Collection</code> of <code>Synset</code>
     * that satisfy the specified <code>FilterConfig</code>. If no <code>Synsets</code> were found, this
     * is an empty <code>List</code>.
     */
    public List<Synset> getSynsets(FilterConfig filter, Collection<Synset> synsets) {
        List<Synset> rval = new ArrayList<>();

        Pattern pattern;
        String searchString = filter.getSearchString();
        Set<WordCategory> wordCategories = filter.getWordCategories();
        Set<WordClass> wordClasses = filter.getWordClasses();
        Set<OrthFormVariant> orthFormVariants = filter.getOrthFormVariants();

        // can't do anything with a null or empty searchString
        // or if any of the sets are null or empty
        if (searchString == null || searchString.isEmpty()
                || wordCategories == null || wordCategories.isEmpty()
                || wordClasses == null || wordClasses.isEmpty()
                || orthFormVariants == null || orthFormVariants.isEmpty()) {
            return rval;
        }

        pattern = compilePattern(filter);

        for (Synset synset : synsets) {
            if (wordCategories.contains(synset.getWordCategory())
                    && wordClasses.contains(synset.getWordClass())) {
                List<LexUnit> lexUnitList = matchLexUnits(synset.getLexUnits(), filter, pattern);
                if (!lexUnitList.isEmpty()) {
                    rval.add(synset);
                }
            }
        }

        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code>.
     *
     * @return a <code>list</code> of all <code>Synsets</code>
     */
    public List<Synset> getSynsets() {
        List<Synset> rval = new ArrayList<>(synsets.size());
        Iterator iter = synsets.iterator();
        while (iter.hasNext())
            rval.add((Synset) iter.next());
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> in which
     * <code>orthForm</code> occurs as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant in one of its <code>LexUnits</code>, using the
     * <code>ignoreCase</code> flag as set in the constructor. Same than calling
     * <code>getSynsets(orthForm, false)</code> with
     * <code>considerMainOrthFormOnly=false</code>.
     *
     * @param orthForm the <code>orthForm</code> to search for
     * @return a <code>List</code> of all <code>Synsets</code> containing
     * orthForm. If no <code>Synsets</code> were found, this is a
     * <code>List</code> containing no <code>Synsets</code>
     */
    public List<Synset> getSynsets(String orthForm) {
        return getSynsets(orthForm, false);
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> in which
     * <code>orthForm</code> occurs as main orthographical form in one of its
     * <code>LexUnits</code> -- in case <code>considerAllOrthForms</code> is
     * true. Else returns a <code>List</code> of all <code>Synsets</code> in
     * which <code>orthForm</code> occurs as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant in one of its <code>LexUnits</code> -- in case
     * <code>considerAllOrthForms</code> is false. It uses the
     * <code>ignoreCase</code> flag as set in the constructor.
     *
     * @param orthForm                 the <code>orthForm</code> to search for
     * @param considerMainOrthFormOnly considering main orthographical form only
     *                                 (<code>true</code>) or all variants (<code>false</code>)
     * @return a <code>List</code> of all <code>Synsets</code> containing
     * orthForm. If no <code>Synsets</code> were found, this is a
     * <code>List</code> containing no <code>Synsets</code>
     */
    public List<Synset> getSynsets(String orthForm, boolean considerMainOrthFormOnly) {
        ArrayList<Synset> rval = new ArrayList<Synset>();
        HashMap<String, ArrayList<LexUnit>> map;
        List<LexUnit> tmpList;
        Set<String> mapForms;

        if (ignoreCase) {
            mapForms = lowerToUpperMap.get(orthForm.toLowerCase());
            mapForms = (mapForms == null) ? new HashSet<>(0) : mapForms;
        } else {
            mapForms = new HashSet<String>(1);
            mapForms.add(orthForm);
        }

        for (String form : mapForms) {
            for (WordCategory wc : WordCategory.values()) {
                if (considerMainOrthFormOnly) {
                    map = wordCategoryMap.get(wc);
                } else {
                    map = wordCategoryMapAllOrthForms.get(wc);
                }
                tmpList = map.get(form);
                if (tmpList != null) {
                    for (LexUnit lu : tmpList) {
                        if (!rval.contains(lu.getSynset())) {
                            rval.add(lu.getSynset());
                        }
                    }
                }
            }
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> with the
     * specified <code>WordCategory</code> in which <code>orthForm</code> occurs
     * as main orthographical form, as orthographical variant, as old
     * orthographical form, or as old orthographic variant in one of its
     * <code>LexUnits</code>. It uses the <code>ignoreCase</code> flag as set in
     * the constructor. Same than calling
     * <code>getSynsets(orthForm, wordCategory, false)</code> with
     * <code>considerMainOrthFormOnly=false</code>.
     *
     * @param orthForm     the <code>orthForm</code> to be found
     * @param wordCategory the <code>WordCategory</code> of the
     *                     <code>Synsets</code> to be found (e.g. <code>WordCategory.adj</code>)
     * @return a <code>List</code> of <code>Synsets</code> with the specified
     * <code>orthForm</code> and <code>wordCategory</code>.
     */
    public List<Synset> getSynsets(String orthForm, WordCategory wordCategory) {
        return getSynsets(orthForm, wordCategory, false);
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> with the
     * specified <code>WordCategory</code> in which <code>orthForm</code> occurs
     * as main orthographical form in one of its <code>LexUnits</code> -- in
     * case <code>considerAllOrthForms</code> is true. Else returns a
     * <code>List</code> of all <code>Synsets</code> in which
     * <code>orthForm</code> occurs as as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant in one of its <code>LexUnits</code> -- in case
     * <code>considerAllOrthForms</code> is false. It uses the
     * <code>ignoreCase</code> flag as set in the constructor.
     *
     * @param orthForm                 the <code>orthForm</code> to be found
     * @param wordCategory             the <code>WordCategory</code> of the
     *                                 <code>Synsets</code> to be found (e.g. <code>WordCategory.adj</code>)
     * @param considerMainOrthFormOnly considering main orthographical form only
     *                                 (<code>true</code>) or all variants (<code>false</code>)
     * @return a <code>List</code> of <code>Synsets</code> with the specified
     * <code>orthForm</code> and <code>wordCategory</code>.
     */
    public List<Synset> getSynsets(String orthForm, WordCategory wordCategory, boolean considerMainOrthFormOnly) {
        ArrayList<Synset> rval = new ArrayList<>();
        HashMap<String, ArrayList<LexUnit>> map;
        List<LexUnit> tmpList;
        Set<String> mapForms;

        if (ignoreCase) {
            mapForms = lowerToUpperMap.get(orthForm.toLowerCase());
            mapForms = (mapForms == null) ? new HashSet<>(0) : mapForms;
        } else {
            mapForms = new HashSet<>(1);
            mapForms.add(orthForm);
        }

        for (String form : mapForms) {
            if (considerMainOrthFormOnly) {
                map = wordCategoryMap.get(wordCategory);
            } else {
                map = wordCategoryMapAllOrthForms.get(wordCategory);
            }
            if (map != null) {
                tmpList = map.get(form);
                if (tmpList != null) {
                    for (LexUnit lu : tmpList) {
                        if (!rval.contains(lu.getSynset())) {
                            rval.add(lu.getSynset());
                        }
                    }
                }
            }
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> in the specified
     * <code>wordCategory</code>.
     *
     * @param wordCategory the <code>WordCategory</code>, for example
     *                     <code>WordCategory.nomen</code>
     * @return a <code>List</code> of all <code>Synsets</code> in the specified
     * <code>wordCategory</code>. If no <code>Synsets</code> were found, this is
     * a <code>List</code> containing no <code>Synsets</code>.
     */
    public List<Synset> getSynsets(WordCategory wordCategory) {
        ArrayList<Synset> rval = new ArrayList<>();

        for (Synset syn : synsets) {
            if (syn.getWordCategory() == wordCategory) {
                rval.add(syn);
            }
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> in the specified
     * <code>wordClass</code>.
     *
     * @param wordClass the <code>WordClass</code>, for example
     *                  <code>WordCategory.Menge</code>
     * @return a <code>List</code> of all <code>Synsets</code> in the specified
     * <code>wordClass</code>. If no <code>Synsets</code> were found, this is
     * a <code>List</code> containing no <code>Synsets</code>.
     */
    public List<Synset> getSynsets(WordClass wordClass) {

        ArrayList<Synset> rval = new ArrayList<>();

        for (Synset syn : synsets) {
            if (syn.getWordClass() == wordClass) {
                rval.add(syn);
            }
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns the <code>Synset</code> with <code>id</code>, or
     * <code>null</code> if it is not found.
     *
     * @param id the ID of the <code>Synset</code> to be found.
     * @return the <code>Synset</code> with <code>id</code>, or <code>null</code>
     * if it is not found..
     */
    public Synset getSynsetByID(int id) {
        return synsetID.get(id);
    }

    /**
     * Returns the <code>LexUnit</code> with <code>id</code>, or
     * <code>null</code> if it is not found.
     *
     * @param id the ID of the <code>LexUnit</code> to be found
     * @return the <code>LexUnit</code> with <code>id</code>, or
     * <code>null</code> if it is not found.
     */
    public LexUnit getLexUnitByID(int id) {
        return lexUnitID.get(id);
    }

    /**
     * Returns the number of <code>Synsets</code> contained in <code>GermaNet</code>.
     *
     * @return the number of <code>Synsets</code> contained in <code>GermaNet</code>
     */
    public int numSynsets() {
        return synsetID.size(); //synsets.size();

    }

    /**
     * Returns the number of <code>LexUnits</code> contained in
     * <code>GermaNet</code>.
     *
     * @return the number of <code>LexUnits</code> contained in
     * <code>GermaNet</code>
     */
    public int numLexUnits() {
        return lexUnitID.size();
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code>.
     *
     * @return a <code>List</code> of all <code>LexUnits</code>
     */
    public List<LexUnit> getLexUnits() {
        ArrayList<LexUnit> rval = new ArrayList<LexUnit>();

        for (WordCategory wc : WordCategory.values()) {
            rval.addAll(getLexUnits(wc));
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> in which
     * <code>orthForm</code> occurs as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant. It uses the
     * <code>ignoreCase</code> flag as set in the constructor. Same than
     * calling <code>getSynsets(orthForm, false)</code> with
     * <code>considerMainOrthFormOnly=false</code>.
     *
     * @param orthForm the <code>orthForm</code> to search for
     * @return a <code>List</code> of all <code>LexUnits</code> containing
     * <code>orthForm</code>. If no <code>LexUnits</code> were found, this is a
     * <code>List</code> containing no <code>LexUnits</code>.
     */
    public List<LexUnit> getLexUnits(String orthForm) {
        return getLexUnits(orthForm, false);
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> in which
     * <code>orthForm</code> occurs as main orthographical form -- in case
     * <code>considerAllOrthForms</code> is true. Else returns a
     * <code>List</code> of all <code>LexUnits</code> in which
     * <code>orthForm</code> occurs as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant -- in case
     * <code>considerAllOrthForms</code> is false. It uses the
     * <code>ignoreCase</code> flag as set in the constructor.
     *
     * @param orthForm                 the <code>orthForm</code> to search for
     * @param considerMainOrthFormOnly considering main orthographical form only
     *                                 (<code>true</code>) or all variants (<code>false</code>)
     * @return a <code>List</code> of all <code>LexUnits</code> containing
     * <code>orthForm</code>. If no <code>LexUnits</code> were found, this is a
     * <code>List</code> containing no <code>LexUnits</code>.
     */
    public List<LexUnit> getLexUnits(String orthForm, boolean considerMainOrthFormOnly) {
        ArrayList<LexUnit> rval = new ArrayList<LexUnit>();

        // get LexUnits from each word class
        for (WordCategory wc : WordCategory.values()) {
            rval.addAll(getLexUnits(orthForm, wc, considerMainOrthFormOnly));
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> with the
     * specified <code>WordCategory</code> in which <code>orthForm</code>
     * occurs as as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant. It uses the <code>ignoreCase</code> flag as set in
     * the constructor. Same than calling
     * <code>getSynsets(orthForm, wordCategory, false)</code> with
     * <code>considerMainOrthFormOnly=false</code>.
     *
     * @param orthForm     the <code>orthForm</code> to be found
     * @param wordCategory the <code>WordCategory</code> of the
     *                     <code>LexUnits</code> to be found (eg <code>WordCategory.nomen</code>)
     * @return a <code>List</code> of <code>LexUnits</code> with the specified
     * <code>orthForm</code> and <code>wordCategory</code>.
     */
    public List<LexUnit> getLexUnits(String orthForm, WordCategory wordCategory) {
        return getLexUnits(orthForm, wordCategory, false);
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> with the
     * specified <code>WordCategory</code> in which <code>orthForm</code> occurs
     * as main orthographical form -- in case <code>considerAllOrthForms</code>
     * is true. Else returns a <code>List</code> of all <code>LexUnits</code> in
     * which <code>orthForm</code> occurs as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant -- in case
     * <code>considerAllOrthForms</code> is false. It uses the
     * <code>ignoreCase</code> flag as set in the constructor.
     *
     * @param orthForm                 the <code>orthForm</code> to be found
     * @param wordCategory             the <code>WordCategory</code> of the
     *                                 <code>LexUnits</code> to be found (eg <code>WordCategory.nomen</code>)
     * @param considerMainOrthFormOnly considering main orthographical form only
     *                                 (<code>true</code>) or all variants (<code>false</code>)
     * @return a <code>List</code> of <code>LexUnits</code> with the specified
     * <code>orthForm</code> and <code>wordCategory</code>.
     */
    public List<LexUnit> getLexUnits(String orthForm, WordCategory wordCategory,
                                     boolean considerMainOrthFormOnly) {
        List<LexUnit> rval = new ArrayList<LexUnit>();
        ArrayList<LexUnit> tmpList;
        HashMap<String, ArrayList<LexUnit>> map;
        String mapForm = orthForm;
        Set<String> mapForms;

        if (ignoreCase) {
            mapForms = lowerToUpperMap.get(orthForm.toLowerCase());
            mapForms = (mapForms == null) ? new HashSet<>(0) : mapForms;
        } else {
            mapForms = new HashSet<String>(1);
            mapForms.add(orthForm);
        }

        for (String form : mapForms) {
            if (considerMainOrthFormOnly) {
                map = wordCategoryMap.get(wordCategory);
            } else {
                map = wordCategoryMapAllOrthForms.get(wordCategory);
            }

            if (map != null) {
                tmpList = map.get(form);
                if (tmpList != null) {
                    rval.addAll((List<LexUnit>) tmpList.clone());
                }
            }
        }
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> in the specified
     * <code>wordCategory</code>.
     *
     * @param wordCategory the <code>WordCategory</code>, (e.g.
     *                     <code>WordCategory.verben</code>)
     * @return a <code>List</code> of all <code>LexUnits</code> in the specified
     * <code>wordCategory</code>. If no <code>LexUnits</code> were found, this
     * is a <code>List</code> containing no <code>LexUnits</code>.
     */
    public List<LexUnit> getLexUnits(WordCategory wordCategory) {
        ArrayList<LexUnit> rval = new ArrayList<LexUnit>();
        HashMap<String, ArrayList<LexUnit>> map;
        map = wordCategoryMap.get(wordCategory);

        for (ArrayList<LexUnit> luList : map.values()) {
            rval.addAll((ArrayList<LexUnit>) luList.clone());
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> using the specified
     * <code>FilterConfig</code>.
     *
     * @param filter a <code>FilterConfig</code> to use for the search
     * @return a <code>List</code> of all <code>LexUnits</code> using the specified
     * <code>FilterConfig</code>. If no <code>LexUnits</code> were found, this
     * is an empty <code>List</code>.
     */
    public List<LexUnit> getLexUnits(FilterConfig filter) {
        return getLexUnits(filter, getLexUnits());
    }

    /**
     * Returns a <code>List</code> of <code>LexUnits</code> in the given <code>Collection</code> of <code>LexUnit</code>
     * that satisfy the specified <code>FilterConfig</code>.
     *
     * @param filter   a <code>FilterConfig</code> to use for the search
     * @param lexUnits a <code>Collection</code> of <code>LexUnit</code> to search
     * @return a <code>List</code> of <code>LexUnits</code> in the given <code>Collection</code> of <code>LexUnit</code>
     * that satisfy the specified <code>FilterConfig</code>. If no <code>LexUnits</code> were found, this
     * is an empty <code>List</code>.
     */
    public List<LexUnit> getLexUnits(FilterConfig filter, Collection<LexUnit> lexUnits) {

        Pattern pattern;
        List<LexUnit> rval = new ArrayList<>();
        String searchString = filter.getSearchString();
        Set<WordCategory> wordCategories = filter.getWordCategories();
        Set<WordClass> wordClasses = filter.getWordClasses();
        Set<OrthFormVariant> orthFormVariants = filter.getOrthFormVariants();

        // can't do anything with a null or empty searchString
        // or if any of the sets are null or empty
        if (searchString == null || searchString.isEmpty()
                || wordCategories == null || wordCategories.isEmpty()
                || wordClasses == null || wordClasses.isEmpty()
                || orthFormVariants == null || orthFormVariants.isEmpty()) {
            return rval;
        }

        // check WordCategory and WordClass:
        // matchLexUnits() does not check Cat or Class
        List<LexUnit> catClassFilteredLexUnits = new ArrayList<>();
        for (LexUnit lexUnit : lexUnits) {
            if (filter.getWordCategories().contains(lexUnit.getWordCategory())
                    && filter.getWordClasses().contains(lexUnit.getWordClass())) {
                catClassFilteredLexUnits.add(lexUnit);
            }
        }

        pattern = compilePattern(filter);
        rval = matchLexUnits(catClassFilteredLexUnits, filter, pattern);

        if (rval == null) {
            rval = new ArrayList<>();
        }
        return rval;
    }

    /**
     * Return a <code>List</code> of <code>LexUnit</code>s within the given <code>Collection</code>
     * of <code>LexUnit</code>s that match the given precompiled <code>Pattern</code>, taking the filter's
     * edit distance into account if the search term is not a regular expression.
     *
     * @param lexUnits the <code>Collection</code> of <code>LexUnit</code>s to match
     * @param filter   the <code>FilterConfig</code> to use
     * @param pattern  a precompiled <code>Pattern</code>
     * @return a <code>List</code> of <code>LexUnit</code>s within the given <code>Collection</code>
     * of <code>LexUnit</code>s that match the given precompiled <code>Pattern</code>, taking the filter's
     * edit distance into account if the search term is not a regular expression. If no matches are found, an
     * empty <code>List</code> is returned.
     */
    private List<LexUnit> matchLexUnits(Collection<LexUnit> lexUnits, FilterConfig filter, Pattern pattern) {
        Set<OrthFormVariant> orthFormVariants = filter.getOrthFormVariants();
        List<LexUnit> rval = new ArrayList<>();

        for (LexUnit lu : lexUnits) {
            boolean hit = false;
            for (OrthFormVariant variant : orthFormVariants) {
                String toMatch = null;
                switch (variant) {
                    case orthForm:
                        toMatch = lu.getOrthForm();
                        break;
                    case orthVar:
                        toMatch = lu.getOrthVar();
                        break;
                    case oldOrthForm:
                        toMatch = lu.getOldOrthForm();
                        break;
                    case oldOrthVar:
                        toMatch = lu.getOldOrthVar();
                        break;
                }
                if (toMatch != null) {
                    int editDist = filter.getEditDistance();
                    if (!filter.isRegEx() && editDist > 0) {
                        String searchString = filter.getSearchString();
                        if (filter.isIgnoreCase()) {
                            searchString = searchString.toLowerCase();
                        }
                        int actualDist = levenshteinDistance.apply(searchString, toMatch);
                        if ((actualDist >= 0) && (actualDist <= editDist)) {
                            hit = true;
                            break; // found a match in this LexUnit, stop looking
                        }
                    } else if (pattern.matcher(toMatch).matches()) {
                        hit = true;
                        break; // found a match in this LexUnit, stop looking
                    }
                }
            }
            if (hit) {
                rval.add(lu);
            }
        }
        return rval;
    }

    /**
     * Returns a compiled pattern based on the given <code>FilterConfig</code>'s
     * searchString, ignoreCase and regEx values.
     *
     * @param filter the <code>FilterConfig</code> to use for this <code>Pattern</code>
     * @return a compiled pattern based on the given <code>FilterConfig</code>'s
     * searchString, ignoreCase and regEx values.
     */
    private Pattern compilePattern(FilterConfig filter) {
        int patternFlags = 0;
        String searchString = filter.getSearchString();

        // set the pattern flags if case insensitive or not a regEx
        if (filter.isIgnoreCase()) {
            patternFlags = patternFlags | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        }
        if (!filter.isRegEx()) {
            patternFlags = patternFlags | Pattern.LITERAL;
        }

        return Pattern.compile(searchString, patternFlags);
    }

    /**
     * Trims all <code>Lists</code> (takes ~0.3 seconds and frees up 2mb).
     */
    protected void trimAll() {
        // trim Synsets, which trim LexUnits
        for (Synset sset : synsets) {
            sset.trimAll();
        }

        // trim lists in wordCategoryMap
        HashMap<String, ArrayList<LexUnit>> map;
        for (WordCategory wc : WordCategory.values()) {
            map = wordCategoryMap.get(wc);
            for (ArrayList<LexUnit> luList : map.values()) {
                luList.trimToSize();
            }
        }
    }

    /**
     * Loads the ILI data files into this <code>GermaNet</code> object
     * from the specified directory File
     *
     * @throws javax.xml.stream.XMLStreamException
     */
    private void loadIli() throws FileNotFoundException, XMLStreamException {
        IliLoader loader;
        String oldVal;

        // use xerces xml parser
        oldVal = System.getProperty("javax.xml.stream.XMLInputFactory");
        System.setProperty("javax.xml.stream.XMLInputFactory",
                "com.sun.xml.internal.stream.XMLInputFactoryImpl");

        // load data
        loader = new IliLoader(this);

        if (iliInputStream != null) {
            loader.loadILI(iliInputStream);
            //add the information about corresponding IliRecords to LexUnits
            updateLexUnitsWithIli();
        }

        // set parser back to whatever it was before
        if (oldVal != null) {
            System.setProperty("javax.xml.stream.XMLInputFactory", oldVal);
        }
    }

    /**
     * Adds <code>IliRecords</code> to this <code>GermaNet</code>
     * object when IliLoader is called
     *
     * @param ili the <code>IliRecord</code> to be added
     */
    protected void addIliRecord(IliRecord ili) {
        iliRecords.add(ili);
    }

    /**
     * Returns a <code>List</code> of all <code>IliRecords</code>.
     *
     * @return a <code>List</code> of all <code>IliRecords</code>
     */
    public List<IliRecord> getIliRecords() {
        return (List<IliRecord>) iliRecords.clone();
    }

    /**
     * Adds the information about corresponding <code>IliRecords</code>
     * to <code>LexUnits</code>
     */
    protected void updateLexUnitsWithIli() {
        for (IliRecord ili : iliRecords) {
            int id = ili.getLexUnitId();
            if (getLexUnitByID(id) != null) {
                LexUnit lu = getLexUnitByID(id);
                lu.addIliRecord(ili);
                lexUnitID.put(id, lu);
            }
        }
    }


    /**
     * Loads the Wiktionary data files into this <code>GermaNet</code> object
     * from the specified directory File
     *
     * @throws javax.xml.stream.XMLStreamException
     */
    private void loadWiktionaryParaphrases() throws XMLStreamException, FileNotFoundException {
        WiktionaryLoader loader;
        String oldVal;

        // use xerces xml parser
        oldVal = System.getProperty("javax.xml.stream.XMLInputFactory");
        System.setProperty("javax.xml.stream.XMLInputFactory",
                "com.sun.xml.internal.stream.XMLInputFactoryImpl");

        if (wiktInputStreams.size() > 0) {
            // load data
            loader = new WiktionaryLoader(this);
            loader.loadWiktionary(wiktInputStreams, wiktXmlNames);
            //add the information about corresponding WiktionaryParaphrases to LexUnits
            updateLexUnitsWithWiktionary();
        }

        // set parser back to whatever it was before
        if (oldVal != null) {
            System.setProperty("javax.xml.stream.XMLInputFactory", oldVal);
        }
    }

    /**
     * Adds <code>WiktionaryParaphrases</code> to this <code>GermaNet</code>
     * object when WiktionaryLoader is called
     *
     * @param wiki the <code>WiktionaryParaphrase</code> to be added
     */
    protected void addWiktionaryParaphrase(WiktionaryParaphrase wiki) {
        wiktionaryParaphrases.add(wiki);
    }

    /**
     * Returns a <code>List</code> of all <code>WiktionaryParaphrases</code>.
     *
     * @return a <code>List</code> of all <code>WiktionaryParaphrases</code>
     */
    public List<WiktionaryParaphrase> getWiktionaryParaphrases() {
        return (List<WiktionaryParaphrase>) wiktionaryParaphrases.clone();
    }

    public HashMap<LexUnit, CompoundInfo> getLexUnitsWithCompoundInfo() {
        HashMap<LexUnit, CompoundInfo> lexUnitsWithCimpounds = new HashMap<LexUnit, CompoundInfo>();
        for (LexUnit lu : getLexUnits()) {
            if (lu.getCompoundInfo() != null) {
                lexUnitsWithCimpounds.put(lu, lu.getCompoundInfo());
            }
        }
        return lexUnitsWithCimpounds;
    }

    /**
     * Adds the information about corresponding <code>WiktionaryParaphrases</code>
     * to <code>LexUnits</code>
     */
    protected void updateLexUnitsWithWiktionary() {
        for (WiktionaryParaphrase wiki : wiktionaryParaphrases) {
            int id = wiki.getLexUnitId();
            LexUnit lu = getLexUnitByID(id);
            if (lu != null) {
                lu.addWiktionaryParaphrase(wiki);
            }
            lexUnitID.put(id, lu);
        }
    }

    /**
     * Checks whether the <code>File</code> is a <code>ZipFile</code>.
     *
     * @param file the <code>File</code> to check
     * @return true if this <code>File</code> is a <code>ZipFile</code>
     * @throws java.io.IOException
     */
    protected static boolean isZipFile(File file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long n = raf.readInt();
        raf.close();
        if (n == 0x504B0304) {
            return true;
        } else {
            return false;
        }
    }
}
