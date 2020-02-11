package de.tuebingen.uni.sfs.germanet.api;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that synset and lexunit searches return the expected results
 * regardless of which constructor was used (ignoreCase true | false),
 * which method is used (overloaded search methods or with FilterConfig).</br></br>
 * <p>
 * The GermaNet XML data is expected to be located at Data/GN-XML-ForApiUnitTesting
 * under your home directory.
 * <p>
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GermaNetTest {
    static GermaNet gnetCaseSensitive;
    static GermaNet gnetIgnoreCase;
    static String dataPath;
    private static final Logger LOGGER = LoggerFactory.getLogger(GermaNetTest.class);


    @BeforeAll
    static void setUp() {
        try {
            String userHome = System.getProperty("user.home");
            String sep = System.getProperty("file.separator");
            dataPath = userHome + sep + "Data" + sep;
            String goodDataPath = dataPath + "GN-XML-ForApiUnitTesting/";

            gnetCaseSensitive = new GermaNet(goodDataPath, false);
            gnetIgnoreCase = new GermaNet(goodDataPath, true);

        } catch (IOException ex) {
            LOGGER.error("\nGermaNet data not found at <homeDirectory>/Data/GN-XML-ForApiUnitTesting/\nAborting...", ex);
            System.exit(0);
        } catch (XMLStreamException ex) {
            LOGGER.error("\nUnable to load GermaNet data at <homeDirectory>/Data/GN-XML-ForApiUnitTesting/\nAborting...", ex);
            System.exit(0);
        }
    }

    @Test
    void badPathTest() {
        assertThrows(FileNotFoundException.class, () -> {
            GermaNet gnet = new GermaNet(dataPath + "blah/blah/");
        });
    }

    @Test
    void iliCountTest() {
        // release 14
        assertEquals(28565, gnetCaseSensitive.getIliRecords().size());
        assertEquals(28565, gnetIgnoreCase.getIliRecords().size());
    }

    @Test
    void wiktCountTest() {
        // release 14
        assertEquals(29549, gnetCaseSensitive.getWiktionaryParaphrases().size());
        assertEquals(29549, gnetIgnoreCase.getWiktionaryParaphrases().size());
    }

    /**
     * Search gnet for Synsets with filterConfig and a GermaNet instance constructed with ignoreCase true | false.
     * Test that the expected synsets , identified by ID, are returned
     *
     * @param filterConfig
     * @param useGnetIgnoreCase
     * @param expectedIds
     */
    @ParameterizedTest(name = "{index} {1} {2} {0}")
    @MethodSource({"emptyFilterProvider",
            "getSynsetsAllOrthFilterProvider",
            "getSynsetsMainOnlyFilterProvider",
            "getSynsetsAllOrthCatFilterProvider",
            "getSynsetsMainOrthCatFilterProvider",
            "getSynsetsAllOrthCatClassFilterProvider",
            "getSynsetsMainOrthCatClassFilterProvider",
            "getSynsetsRegexFilterProvider",
            "getSynsetsRegexEditDistanceFilterProvider",
            "getSynsetsVariedOrthCatClassFilterProvider"})
    void getSynsetsFilter(FilterConfig filterConfig, boolean useGnetIgnoreCase, List<Integer> expectedIds) {
        GermaNet gnet;
        List<Synset> actualList;

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        actualList = gnet.getSynsets(filterConfig);

        List<Integer> actualIds = new ArrayList<>(actualList.size());
        for (Synset synset : actualList) {
            actualIds.add(synset.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    /**
     * Search gnet for LexUnits with filterConfig and a GermaNet instance constructed with ignoreCase true | false.
     * Test that the expected lexunits , identified by ID, are returned
     *
     * @param filterConfig
     * @param useGnetIgnoreCase
     * @param expectedIds
     */
    @ParameterizedTest(name = "{index} {1} {2} {0}")
    @MethodSource({"emptyFilterProvider",
            "getLexUnitsAllOrthFilterProvider",
            "getLexUnitsMainOnlyFilterProvider",
            "getLexUnitsAllOrthCatFilterProvider",
            "getLexUnitsMainOrthCatFilterProvider",
            "getLexUnitsAllOrthCatClassFilterProvider",
            "getLexUnitsMainOrthCatClassFilterProvider",
            "getLexUnitsVariedOrthCatClassFilterProvider",
            "getLexUnitsRegexFilterProvider",
            "getLexUnitsRegexEditDistanceFilterProvider"})
    void getLexUnitsFilter(FilterConfig filterConfig, boolean useGnetIgnoreCase, List<Integer> expectedIds) {
        GermaNet gnet;
        List<LexUnit> actualList;

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        actualList = gnet.getLexUnits(filterConfig);

        List<Integer> actualIds = new ArrayList<>(actualList.size());
        for (LexUnit lexUnit : actualList) {
            actualIds.add(lexUnit.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    /**
     * synset/lexunit searches
     * empty search string or sets
     * Using Filters
     */
    private static Stream<Arguments> emptyFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("");
        FilterConfig filterConfig2 = new FilterConfig(null);
        FilterConfig filterConfig3 = new FilterConfig("blahblahblah");

        FilterConfig filterConfig4 = new FilterConfig("Haus");
        filterConfig4.clearWordClasses();

        FilterConfig filterConfig5 = new FilterConfig("Haus");
        filterConfig5.clearWordClasses();

        FilterConfig filterConfig6 = new FilterConfig("Haus");
        filterConfig6.clearWordClasses();

        // Filter overrides constructor
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig5, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig5, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig6, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig6, caseSensitive, new ArrayList<>(0))
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * All orthForm variants
     * Using Filters
     */
    private static Stream<Arguments> getSynsetsAllOrthFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("Spitz");
        filterConfig1.setIgnoreCase(true);

        FilterConfig filterConfig2 = new FilterConfig("spitz");
        filterConfig2.setIgnoreCase(true);

        FilterConfig filterConfig3 = new FilterConfig("Spitz");
        filterConfig3.setIgnoreCase(false);

        FilterConfig filterConfig4 = new FilterConfig("spitz");
        filterConfig4.setIgnoreCase(false);

        // Filter overrides constructor
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(Arrays.asList(50936, 2433, 4983))),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(Arrays.asList(50936, 2433, 4983))),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(50936, 2433, 4983))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(50936, 2433, 4983))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(Arrays.asList(50936))),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(Arrays.asList(50936))),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(Arrays.asList(2433, 4983))),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(Arrays.asList(2433, 4983)))
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * All orthForm variants
     * Using Filters
     */
    private static Stream<Arguments> getLexUnitsAllOrthFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("Spitz");
        filterConfig1.setIgnoreCase(true);

        FilterConfig filterConfig2 = new FilterConfig("Spitz");
        filterConfig2.setIgnoreCase(false);

        FilterConfig filterConfig3 = new FilterConfig("spitz");
        filterConfig3.setIgnoreCase(true);

        FilterConfig filterConfig4 = new FilterConfig("spitz");
        filterConfig4.setIgnoreCase(false);

        // Filter overrides constructor
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(Arrays.asList(72040, 3733, 7447))),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(Arrays.asList(72040, 3733, 7447))),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(72040))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(72040))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(Arrays.asList(72040, 3733, 7447))),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(Arrays.asList(72040, 3733, 7447))),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(Arrays.asList(3733, 7447))),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(Arrays.asList(3733, 7447)))
        );
    }


    /**
     * synset searches
     * ignoreCase true | false in constructor
     * Main orthForm only
     * Using Filters
     */
    private static Stream<Arguments> getSynsetsMainOnlyFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("Schiffahrt");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig2 = new FilterConfig("schiffahrt");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig3 = new FilterConfig("Schiffahrt");
        filterConfig3.setIgnoreCase(false);
        filterConfig3.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig4 = new FilterConfig("schiffahrt");
        filterConfig4.setIgnoreCase(false);
        filterConfig4.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig5 = new FilterConfig("Fett");
        filterConfig5.setIgnoreCase(false);
        filterConfig5.setOrthFormVariants(OrthFormVariant.orthForm);

        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig5, ignoreCase, new ArrayList<>(Arrays.asList(25879, 40222, 48503))),
                Arguments.of(filterConfig5, caseSensitive, new ArrayList<>(Arrays.asList(25879, 40222, 48503)))
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * Main orthForm only
     * Using Filters
     */
    private static Stream<Arguments> getLexUnitsMainOnlyFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("Schiffahrt");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig2 = new FilterConfig("schiffahrt");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig3 = new FilterConfig("Schiffahrt");
        filterConfig3.setIgnoreCase(false);
        filterConfig3.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig4 = new FilterConfig("schiffahrt");
        filterConfig4.setIgnoreCase(false);
        filterConfig4.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig5 = new FilterConfig("Fett");
        filterConfig5.setIgnoreCase(false);
        filterConfig5.setOrthFormVariants(OrthFormVariant.orthForm);

        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig5, ignoreCase, new ArrayList<>(Arrays.asList(100314, 58933, 69249))),
                Arguments.of(filterConfig5, caseSensitive, new ArrayList<>(Arrays.asList(100314, 58933, 69249)))
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * all orth forms
     * by WordCategory
     * Using Filters
     */
    private static Stream<Arguments> getSynsetsAllOrthCatFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("Recht");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setWordCategories(WordCategory.nomen);

        FilterConfig filterConfig2 = new FilterConfig("recht");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setWordCategories(WordCategory.nomen);

        FilterConfig filterConfig3 = new FilterConfig("Recht");
        filterConfig3.setIgnoreCase(false);
        filterConfig3.setWordCategories(WordCategory.nomen);

        FilterConfig filterConfig4 = new FilterConfig("recht");
        filterConfig4.setIgnoreCase(false);
        filterConfig4.setWordCategories(WordCategory.nomen);

        FilterConfig filterConfig5 = new FilterConfig("Recht");
        filterConfig5.setIgnoreCase(true);
        filterConfig5.setWordCategories(WordCategory.adj);

        FilterConfig filterConfig6 = new FilterConfig("recht");
        filterConfig6.setIgnoreCase(true);
        filterConfig6.setWordCategories(WordCategory.nomen, WordCategory.adj);

        FilterConfig filterConfig7 = new FilterConfig("Recht");
        filterConfig7.setIgnoreCase(true);
        filterConfig7.removeWordCategories(WordCategory.nomen, WordCategory.adj);

        FilterConfig filterConfig8 = new FilterConfig("schiffahrt");
        filterConfig8.setIgnoreCase(true);
        filterConfig8.removeWordCategories(WordCategory.verben, WordCategory.adj);

        FilterConfig filterConfig9 = new FilterConfig("schiffahrt");
        filterConfig9.setIgnoreCase(true);
        filterConfig9.clearWordCategories();
        filterConfig9.addWordCategories(WordCategory.nomen, WordCategory.adj);

        FilterConfig filterConfig10 = new FilterConfig("schiffahrt");
        filterConfig10.setIgnoreCase(true);
        filterConfig10.setWordCategories(WordCategory.adj);

        FilterConfig filterConfig11 = new FilterConfig("schloß");
        filterConfig11.setIgnoreCase(true);
        filterConfig11.setWordCategories(WordCategory.nomen);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213))),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213))),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213))),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213))),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig5, ignoreCase, new ArrayList<>(Arrays.asList(273, 1182, 2311, 99446))),
                Arguments.of(filterConfig5, caseSensitive, new ArrayList<>(Arrays.asList(273, 1182, 2311, 99446))),

                Arguments.of(filterConfig6, ignoreCase, new ArrayList<>(Arrays.asList(273, 1182, 2311, 99446, 32782, 13474, 13475, 28213))),
                Arguments.of(filterConfig6, caseSensitive, new ArrayList<>(Arrays.asList(273, 1182, 2311, 99446, 32782, 13474, 13475, 28213))),

                Arguments.of(filterConfig7, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig7, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig8, ignoreCase, new ArrayList<>(Arrays.asList(19109))),
                Arguments.of(filterConfig8, caseSensitive, new ArrayList<>(Arrays.asList(19109))),

                Arguments.of(filterConfig9, ignoreCase, new ArrayList<>(Arrays.asList(19109))),
                Arguments.of(filterConfig9, caseSensitive, new ArrayList<>(Arrays.asList(19109))),

                Arguments.of(filterConfig10, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig10, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig11, ignoreCase, new ArrayList<>(Arrays.asList(6011, 42555))),
                Arguments.of(filterConfig11, caseSensitive, new ArrayList<>(Arrays.asList(6011, 42555)))
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * all orth forms
     * by WordCategory
     * Using Filters
     */
    private static Stream<Arguments> getLexUnitsAllOrthCatFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("Recht");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setWordCategories(WordCategory.nomen);

        FilterConfig filterConfig2 = new FilterConfig("recht");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setWordCategories(WordCategory.nomen);

        FilterConfig filterConfig3 = new FilterConfig("Recht");
        filterConfig3.setIgnoreCase(false);
        filterConfig3.setWordCategories(WordCategory.nomen);

        FilterConfig filterConfig4 = new FilterConfig("recht");
        filterConfig4.setIgnoreCase(false);
        filterConfig4.setWordCategories(WordCategory.nomen);

        FilterConfig filterConfig5 = new FilterConfig("Recht");
        filterConfig5.setIgnoreCase(true);
        filterConfig5.setWordCategories(WordCategory.adj);

        FilterConfig filterConfig6 = new FilterConfig("recht");
        filterConfig6.setIgnoreCase(true);
        filterConfig6.setWordCategories(WordCategory.nomen, WordCategory.adj);

        FilterConfig filterConfig7 = new FilterConfig("Recht");
        filterConfig7.setIgnoreCase(true);
        filterConfig7.removeWordCategories(WordCategory.nomen, WordCategory.adj);

        FilterConfig filterConfig8 = new FilterConfig("schiffahrt");
        filterConfig8.setIgnoreCase(true);
        filterConfig8.removeWordCategories(WordCategory.verben, WordCategory.adj);

        FilterConfig filterConfig9 = new FilterConfig("schiffahrt");
        filterConfig9.setIgnoreCase(true);
        filterConfig9.clearWordCategories();
        filterConfig9.addWordCategories(WordCategory.nomen, WordCategory.adj);

        FilterConfig filterConfig10 = new FilterConfig("schiffahrt");
        filterConfig10.setIgnoreCase(true);
        filterConfig10.setWordCategories(WordCategory.adj);

        FilterConfig filterConfig11 = new FilterConfig("schloß");
        filterConfig11.setIgnoreCase(true);
        filterConfig11.setWordCategories(WordCategory.nomen);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344))),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344))),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344))),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344))),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig5, ignoreCase, new ArrayList<>(Arrays.asList(435, 1963, 3535, 132285))),
                Arguments.of(filterConfig5, caseSensitive, new ArrayList<>(Arrays.asList(435, 1963, 3535, 132285))),

                Arguments.of(filterConfig6, ignoreCase, new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344, 435, 1963, 3535, 132285))),
                Arguments.of(filterConfig6, caseSensitive, new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344, 435, 1963, 3535, 132285))),

                Arguments.of(filterConfig7, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig7, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig8, ignoreCase, new ArrayList<>(Arrays.asList(26447))),
                Arguments.of(filterConfig8, caseSensitive, new ArrayList<>(Arrays.asList(26447))),

                Arguments.of(filterConfig9, ignoreCase, new ArrayList<>(Arrays.asList(26447))),
                Arguments.of(filterConfig9, caseSensitive, new ArrayList<>(Arrays.asList(26447))),

                Arguments.of(filterConfig10, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig10, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig11, ignoreCase, new ArrayList<>(Arrays.asList(62042, 8893))),
                Arguments.of(filterConfig11, caseSensitive, new ArrayList<>(Arrays.asList(62042, 8893)))
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * Main orthForms only
     * by WordCategory
     * Using Filters
     */
    private static Stream<Arguments> getSynsetsMainOrthCatFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("schiffahrt");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setWordCategories(WordCategory.nomen);
        filterConfig1.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig2 = new FilterConfig("Schifffahrt");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setWordCategories(WordCategory.nomen);
        filterConfig2.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig3 = new FilterConfig("telefon");
        filterConfig3.setIgnoreCase(true);
        filterConfig3.setWordCategories(WordCategory.nomen);
        filterConfig3.setOrthFormVariants(OrthFormVariant.orthForm);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(19109))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(19109))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(Arrays.asList(7740))),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(Arrays.asList(7740)))
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * Main orthForms only
     * by WordCategory
     * Using Filters
     */
    private static Stream<Arguments> getLexUnitsMainOrthCatFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("schiffahrt");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setWordCategories(WordCategory.nomen);
        filterConfig1.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig2 = new FilterConfig("Schifffahrt");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setWordCategories(WordCategory.nomen);
        filterConfig2.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig3 = new FilterConfig("telefon");
        filterConfig3.setIgnoreCase(true);
        filterConfig3.setWordCategories(WordCategory.nomen);
        filterConfig3.setOrthFormVariants(OrthFormVariant.orthForm);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(26447))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(26447))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(Arrays.asList(11042))),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(Arrays.asList(11042)))
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * All orthForms
     * by WordCategory
     * by WordClass
     * Using Filters
     */
    private static Stream<Arguments> getSynsetsAllOrthCatClassFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("Recht");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setWordCategories(WordCategory.adj, WordCategory.nomen);
        filterConfig1.setWordClasses(WordClass.Attribut, WordClass.Ort);

        FilterConfig filterConfig2 = new FilterConfig("Recht");
        filterConfig2.setIgnoreCase(false);
        filterConfig2.setWordCategories(WordCategory.nomen);
        filterConfig2.removeWordClasses(WordClass.Attribut);

        FilterConfig filterConfig3 = new FilterConfig("Recht");
        filterConfig3.setIgnoreCase(true);
        filterConfig3.setWordClasses(WordClass.Gefuehl, WordClass.Kognition);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(Arrays.asList(2311, 13474, 13475))),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(Arrays.asList(2311, 13474, 13475))),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(32782, 28213))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(32782, 28213))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(Arrays.asList(99446, 28213))),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(Arrays.asList(99446, 28213)))
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * All orthForms
     * by WordCategory
     * by WordClass
     * Using Filters
     */
    private static Stream<Arguments> getLexUnitsAllOrthCatClassFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("Recht");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setWordCategories(WordCategory.adj, WordCategory.nomen);
        filterConfig1.setWordClasses(WordClass.Attribut, WordClass.Ort);

        FilterConfig filterConfig2 = new FilterConfig("Recht");
        filterConfig2.setIgnoreCase(false);
        filterConfig2.setWordCategories(WordCategory.nomen);
        filterConfig2.removeWordClasses(WordClass.Attribut);

        FilterConfig filterConfig3 = new FilterConfig("Recht");
        filterConfig3.setIgnoreCase(true);
        filterConfig3.setWordClasses(WordClass.Gefuehl, WordClass.Kognition);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(Arrays.asList(3535, 18689, 18690))),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(Arrays.asList(3535, 18689, 18690))),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(45139, 145344))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(45139, 145344))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(Arrays.asList(145344, 132285))),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(Arrays.asList(145344, 132285)))
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * Main orthForm
     * by WordCategory
     * by WordClass
     * Using Filters
     */
    private static Stream<Arguments> getSynsetsMainOrthCatClassFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("schiffahrt");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setWordCategories(WordCategory.nomen);
        filterConfig1.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig2 = new FilterConfig("Schifffahrt");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setWordCategories(WordCategory.nomen);
        filterConfig2.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig3 = new FilterConfig("Schifffahrt");
        filterConfig3.setIgnoreCase(true);
        filterConfig3.setWordCategories(WordCategory.nomen);
        filterConfig3.setOrthFormVariants(OrthFormVariant.orthForm);
        filterConfig3.removeWordClasses(WordClass.Geschehen);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(19109))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(19109))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(0))
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * Main orthForm
     * by WordCategory
     * by WordClass
     * Using Filters
     */
    private static Stream<Arguments> getLexUnitsMainOrthCatClassFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("schiffahrt");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setWordCategories(WordCategory.nomen);
        filterConfig1.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig2 = new FilterConfig("Schifffahrt");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setWordCategories(WordCategory.nomen);
        filterConfig2.setOrthFormVariants(OrthFormVariant.orthForm);

        FilterConfig filterConfig3 = new FilterConfig("Schifffahrt");
        filterConfig3.setIgnoreCase(true);
        filterConfig3.setWordCategories(WordCategory.nomen);
        filterConfig3.setOrthFormVariants(OrthFormVariant.orthForm);
        filterConfig3.removeWordClasses(WordClass.Geschehen);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(26447))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(26447))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(0))
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * Various orthForms
     * by WordCategory
     * by WordClass
     * Using Filters
     */
    private static Stream<Arguments> getSynsetsVariedOrthCatClassFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        // none, orthForm is Schloss
        FilterConfig filterConfig1 = new FilterConfig("Schloß");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setWordCategories(WordCategory.nomen);
        filterConfig1.setOrthFormVariants(OrthFormVariant.orthForm);

        // 2 with oldOrthForm as Schloß
        FilterConfig filterConfig2 = new FilterConfig("Schloß");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setWordCategories(WordCategory.nomen);
        filterConfig2.setOrthFormVariants(OrthFormVariant.oldOrthForm);

        // none, Panther (orthForm) - Panter (orthVar)
        FilterConfig filterConfig3 = new FilterConfig("panter");
        filterConfig3.setIgnoreCase(true);
        filterConfig3.setWordCategories(WordCategory.nomen);
        filterConfig3.setOrthFormVariants(OrthFormVariant.orthForm);

        // 1, Panther (orthForm) - Panter (orthVar)
        FilterConfig filterConfig4 = new FilterConfig("panter");
        filterConfig4.setIgnoreCase(true);
        filterConfig4.setWordCategories(WordCategory.nomen);
        filterConfig4.setOrthFormVariants(OrthFormVariant.orthVar);

        // none, Panther (orthForm) - Panter (orthVar), remove orthVar variant
        FilterConfig filterConfig5 = new FilterConfig("panter");
        filterConfig5.setIgnoreCase(true);
        filterConfig5.setWordCategories(WordCategory.nomen);
        filterConfig5.removeOrthFormVariants(OrthFormVariant.orthVar);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(6011, 42555))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(6011, 42555))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(Arrays.asList(50699))),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(Arrays.asList(50699))),

                Arguments.of(filterConfig5, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig5, caseSensitive, new ArrayList<>(0))
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * Various orthForms
     * by WordCategory
     * by WordClass
     * Using Filters
     */
    private static Stream<Arguments> getLexUnitsVariedOrthCatClassFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        // none, orthForm is Schloss
        FilterConfig filterConfig1 = new FilterConfig("Schloß");
        filterConfig1.setIgnoreCase(true);
        filterConfig1.setWordCategories(WordCategory.nomen);
        filterConfig1.setOrthFormVariants(OrthFormVariant.orthForm);

        // 2 with oldOrthForm as Schloß
        FilterConfig filterConfig2 = new FilterConfig("Schloß");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setWordCategories(WordCategory.nomen);
        filterConfig2.setOrthFormVariants(OrthFormVariant.oldOrthForm);

        // none, Panther (orthForm) - Panter (orthVar)
        FilterConfig filterConfig3 = new FilterConfig("panter");
        filterConfig3.setIgnoreCase(true);
        filterConfig3.setWordCategories(WordCategory.nomen);
        filterConfig3.setOrthFormVariants(OrthFormVariant.orthForm);

        // 1, Panther (orthForm) - Panter (orthVar)
        FilterConfig filterConfig4 = new FilterConfig("panter");
        filterConfig4.setIgnoreCase(true);
        filterConfig4.setWordCategories(WordCategory.nomen);
        filterConfig4.setOrthFormVariants(OrthFormVariant.orthVar);

        // none, Panther (orthForm) - Panter (orthVar), remove orthVar variant
        FilterConfig filterConfig5 = new FilterConfig("panter");
        filterConfig5.setIgnoreCase(true);
        filterConfig5.setWordCategories(WordCategory.nomen);
        filterConfig5.removeOrthFormVariants(OrthFormVariant.orthVar);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(8893, 62042))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(8893, 62042))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(0)),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(Arrays.asList(71762))),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(Arrays.asList(71762))),

                Arguments.of(filterConfig5, ignoreCase, new ArrayList<>(0)),
                Arguments.of(filterConfig5, caseSensitive, new ArrayList<>(0))
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * All orthForms
     * RegEx
     * Using Filters
     */
    private static Stream<Arguments> getSynsetsRegexFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("Unver.*barkeit");
        filterConfig1.setIgnoreCase(false);
        filterConfig1.setRegEx(true);

        FilterConfig filterConfig2 = new FilterConfig("unver.*barkeit");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setRegEx(true);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(Arrays.asList(13953, 13966, 13969, 14004, 101032, 108169, 122501, 125332))),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(Arrays.asList(13953, 13966, 13969, 14004, 101032, 108169, 122501, 125332))),
                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(13953, 13966, 13969, 14004, 101032, 108169, 122501, 125332))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(13953, 13966, 13969, 14004, 101032, 108169, 122501, 125332)))
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * All orthForms
     * RegEx
     * Using Filters
     */
    private static Stream<Arguments> getLexUnitsRegexFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        FilterConfig filterConfig1 = new FilterConfig("Unver.*barkeit");
        filterConfig1.setIgnoreCase(false);
        filterConfig1.setRegEx(true);

        FilterConfig filterConfig2 = new FilterConfig("unver.*barkeit");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setRegEx(true);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(Arrays.asList(19331, 19344, 19347, 19387, 134272, 143622, 161172, 164538))),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(Arrays.asList(19331, 19344, 19347, 19387, 134272, 143622, 161172, 164538))),
                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(19331, 19344, 19347, 19387, 134272, 143622, 161172, 164538))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(19331, 19344, 19347, 19387, 134272, 143622, 161172, 164538)))
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * All orthForms
     * RegEx
     * EditDistance
     * Using Filters
     */
    private static Stream<Arguments> getSynsetsRegexEditDistanceFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        // editDistance should be ignored if regEx
        FilterConfig filterConfig1 = new FilterConfig("Unver.*barkeit");
        filterConfig1.setIgnoreCase(false);
        filterConfig1.setRegEx(true);
        filterConfig1.setEditDistance(3);

        // editDistance should be ignored if regEx
        FilterConfig filterConfig2 = new FilterConfig("unver.*barkeit");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setRegEx(true);
        filterConfig2.setEditDistance(5);

        // editDistance should be used if NOT regEx
        FilterConfig filterConfig3 = new FilterConfig("Unvertretbarkeit");
        filterConfig3.setIgnoreCase(false);
        filterConfig3.setEditDistance(4);

        // editDistance should be used if NOT regEx
        FilterConfig filterConfig4 = new FilterConfig("Schloß");
        filterConfig4.setIgnoreCase(true);
        filterConfig4.setOrthFormVariants(OrthFormVariant.oldOrthForm);
        filterConfig4.setEditDistance(2);

        // editDistance should be used if NOT regEx
        FilterConfig filterConfig5 = new FilterConfig("schloß");
        filterConfig5.setIgnoreCase(true);
        filterConfig5.setOrthFormVariants(OrthFormVariant.oldOrthForm);
        filterConfig5.setEditDistance(2);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(
                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(Arrays.asList(13953, 13966, 13969, 14004, 101032, 108169, 122501, 125332))),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(Arrays.asList(13953, 13966, 13969, 14004, 101032, 108169, 122501, 125332))),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(13953, 13966, 13969, 14004, 101032, 108169, 122501, 125332))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(13953, 13966, 13969, 14004, 101032, 108169, 122501, 125332))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(Arrays.asList(13943, 13952, 13953, 13966, 13969, 14004, 108169, 122501))),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(Arrays.asList(13943, 13952, 13953, 13966, 13969, 14004, 108169, 122501))),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(Arrays.asList(6011, 17941, 21263, 21276, 28692, 42555, 51189, 80636, 112175))),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(Arrays.asList(6011, 17941, 21263, 21276, 28692, 42555, 51189, 80636, 112175))),

                Arguments.of(filterConfig5, ignoreCase, new ArrayList<>(Arrays.asList(6011, 17941, 21263, 21276, 28692, 42555, 51189, 80636, 112175))),
                Arguments.of(filterConfig5, caseSensitive, new ArrayList<>(Arrays.asList(6011, 17941, 21263, 21276, 28692, 42555, 51189, 80636, 112175)))
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * All orthForms
     * RegEx
     * EditDistance
     * Using Filters
     */
    private static Stream<Arguments> getLexUnitsRegexEditDistanceFilterProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        // editDistance should be ignored if regEx
        FilterConfig filterConfig1 = new FilterConfig("Unver.*barkeit");
        filterConfig1.setIgnoreCase(false);
        filterConfig1.setRegEx(true);
        filterConfig1.setEditDistance(3);

        // editDistance should be ignored if regEx
        FilterConfig filterConfig2 = new FilterConfig("unver.*barkeit");
        filterConfig2.setIgnoreCase(true);
        filterConfig2.setRegEx(true);
        filterConfig2.setEditDistance(5);

        // editDistance should be used if NOT regEx
        FilterConfig filterConfig3 = new FilterConfig("Unvertretbarkeit");
        filterConfig3.setIgnoreCase(false);
        filterConfig3.setEditDistance(4);

        // editDistance should be used if NOT regEx
        FilterConfig filterConfig4 = new FilterConfig("Schloß");
        filterConfig4.setIgnoreCase(true);
        filterConfig4.setOrthFormVariants(OrthFormVariant.oldOrthForm);
        filterConfig4.setEditDistance(2);

        // editDistance should be used if NOT regEx
        FilterConfig filterConfig5 = new FilterConfig("schloß");
        filterConfig5.setIgnoreCase(true);
        filterConfig5.setOrthFormVariants(OrthFormVariant.oldOrthForm);
        filterConfig5.setEditDistance(2);

        // filterConfig overrides constructor for ignoreCase
        return Stream.of(

                Arguments.of(filterConfig1, ignoreCase, new ArrayList<>(Arrays.asList(19331, 19344, 19347, 19387, 134272, 143622, 161172, 164538))),
                Arguments.of(filterConfig1, caseSensitive, new ArrayList<>(Arrays.asList(19331, 19344, 19347, 19387, 134272, 143622, 161172, 164538))),

                Arguments.of(filterConfig2, ignoreCase, new ArrayList<>(Arrays.asList(19331, 19344, 19347, 19387, 134272, 143622, 161172, 164538))),
                Arguments.of(filterConfig2, caseSensitive, new ArrayList<>(Arrays.asList(19331, 19344, 19347, 19387, 134272, 143622, 161172, 164538))),

                Arguments.of(filterConfig3, ignoreCase, new ArrayList<>(Arrays.asList(19319, 19330, 19331, 19344, 19347, 19387, 143622, 161172))),
                Arguments.of(filterConfig3, caseSensitive, new ArrayList<>(Arrays.asList(19319, 19330, 19331, 19344, 19347, 19387, 143622, 161172))),

                Arguments.of(filterConfig4, ignoreCase, new ArrayList<>(Arrays.asList(8893, 24911, 29280, 29305, 62042, 72381, 103425, 109078, 148719))),
                Arguments.of(filterConfig4, caseSensitive, new ArrayList<>(Arrays.asList(8893, 24911, 29280, 29305, 62042, 72381, 103425, 109078, 148719))),

                Arguments.of(filterConfig5, ignoreCase, new ArrayList<>(Arrays.asList(8893, 24911, 29280, 29305, 62042, 72381, 103425, 109078, 148719))),
                Arguments.of(filterConfig5, caseSensitive, new ArrayList<>(Arrays.asList(8893, 24911, 29280, 29305, 62042, 72381, 103425, 109078, 148719)))
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * no Filters
     */
    @ParameterizedTest(name = "{index} {0} {1} {2}")
    @MethodSource({"getSynsetsAllOrthProvider"})
    void getSynsetsAllOrth(String orthForm, List<Integer> expectedIds, boolean useGnetIgnoreCase) {
        GermaNet gnet;
        List<Synset> actualList;

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        actualList = gnet.getSynsets(orthForm);
        List<Integer> actualIds = new ArrayList<>(actualList.size());

        for (Synset synset : actualList) {
            actualIds.add(synset.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * all orth forms
     * no Filters
     */
    private static Stream<Arguments> getSynsetsAllOrthProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        return Stream.of(
                Arguments.of("Spitz", new ArrayList<>(Arrays.asList(50936, 2433, 4983)), ignoreCase),
                Arguments.of("spitz", new ArrayList<>(Arrays.asList(50936, 2433, 4983)), ignoreCase),
                Arguments.of("Spitz", new ArrayList<>(Arrays.asList(50936)), caseSensitive),
                Arguments.of("spitz", new ArrayList<>(Arrays.asList(2433, 4983)), caseSensitive),

                Arguments.of("Schiffahrt", new ArrayList<>(Arrays.asList(19109)), ignoreCase),
                Arguments.of("schiffahrt", new ArrayList<>(Arrays.asList(19109)), ignoreCase),
                Arguments.of("Schiffahrt", new ArrayList<>(Arrays.asList(19109)), caseSensitive),
                Arguments.of("schiffahrt", new ArrayList<>(0), caseSensitive)

        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * no Filters
     */
    @ParameterizedTest(name = "{index} {0} {1} {2}")
    @MethodSource({"getLexUnitsAllOrthProvider"})
    void getLexUnitsAllOrth(String orthForm, List<Integer> expectedIds, boolean useGnetIgnoreCase) {
        GermaNet gnet;
        List<LexUnit> actualList;

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        actualList = gnet.getLexUnits(orthForm);
        List<Integer> actualIds = new ArrayList<>(actualList.size());

        for (LexUnit lexUnit : actualList) {
            actualIds.add(lexUnit.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * all orth forms
     * no Filters
     */
    private static Stream<Arguments> getLexUnitsAllOrthProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        return Stream.of(
                Arguments.of("Spitz", new ArrayList<>(Arrays.asList(72040, 3733, 7447)), ignoreCase),
                Arguments.of("spitz", new ArrayList<>(Arrays.asList(72040, 3733, 7447)), ignoreCase),

                Arguments.of("Spitz", new ArrayList<>(Arrays.asList(72040)), caseSensitive),
                Arguments.of("spitz", new ArrayList<>(Arrays.asList(3733, 7447)), caseSensitive),

                Arguments.of("Schiffahrt", new ArrayList<>(Arrays.asList(26447)), ignoreCase),
                Arguments.of("schiffahrt", new ArrayList<>(Arrays.asList(26447)), ignoreCase),

                Arguments.of("Schiffahrt", new ArrayList<>(Arrays.asList(26447)), caseSensitive),
                Arguments.of("schiffahrt", new ArrayList<>(0), caseSensitive)

        );
    }


    /**
     * synset searches
     * ignoreCase true | false in constructor
     * Main orthForm only
     * no Filters
     */
    @ParameterizedTest(name = "{index} {0} {1} {2}")
    @MethodSource({"getSynsetsMainOnlyProvider"})
    void getSynsetsMainOnly(String orthForm, List<Integer> expectedIds, boolean useGnetIgnoreCase, TestReporter testReporter) {
        GermaNet gnet;
        List<Synset> actualList;

        testReporter.publishEntry("params", orthForm + " " + expectedIds + " "
                + (useGnetIgnoreCase ? "ignoreCase" : "caseSensitive"));


        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        actualList = gnet.getSynsets(orthForm, true);
        List<Integer> actualIds = new ArrayList<>(actualList.size());

        for (Synset synset : actualList) {
            actualIds.add(synset.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    private static Stream<Arguments> getSynsetsMainOnlyProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        return Stream.of(
                Arguments.of("Schiffahrt", new ArrayList<>(0), ignoreCase),
                Arguments.of("schiffahrt", new ArrayList<>(0), ignoreCase),
                Arguments.of("Schiffahrt", new ArrayList<>(0), caseSensitive),
                Arguments.of("schiffahrt", new ArrayList<>(0), caseSensitive)
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * Main orthForm only
     * no Filters
     */
    @ParameterizedTest(name = "{index} {0} {1} {2}")
    @MethodSource({"getLexUnitsMainOnlyProvider"})
    void getLexUnitsMainOnly(String orthForm, List<Integer> expectedIds, boolean useGnetIgnoreCase) {
        GermaNet gnet;
        List<LexUnit> actualList;

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        actualList = gnet.getLexUnits(orthForm, true);
        List<Integer> actualIds = new ArrayList<>(actualList.size());

        for (LexUnit lexUnit : actualList) {
            actualIds.add(lexUnit.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    private static Stream<Arguments> getLexUnitsMainOnlyProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        return Stream.of(
                Arguments.of("Schiffahrt", new ArrayList<>(0), ignoreCase),
                Arguments.of("schiffahrt", new ArrayList<>(0), ignoreCase),

                Arguments.of("Schiffahrt", new ArrayList<>(0), caseSensitive),
                Arguments.of("schiffahrt", new ArrayList<>(0), caseSensitive)
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * all orth forms
     * by WordCategory
     * no Filters
     */
    @ParameterizedTest(name = "{index} {0} {1} {2} {3}")
    @MethodSource({"getSynsetsAllOrthCatProvider"})
    void getSynsetsAllOrthCat(String orthForm, List<Integer> expectedIds, boolean useGnetIgnoreCase, WordCategory cat) {
        GermaNet gnet;
        List<Synset> actualList;

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        actualList = gnet.getSynsets(orthForm, cat);
        List<Integer> actualIds = new ArrayList<>(actualList.size());

        for (Synset synset : actualList) {
            actualIds.add(synset.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    private static Stream<Arguments> getSynsetsAllOrthCatProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        return Stream.of(
                Arguments.of("Recht", new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213)), ignoreCase, WordCategory.nomen),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213)), ignoreCase, WordCategory.nomen),

                Arguments.of("Recht", new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213)), caseSensitive, WordCategory.nomen),
                Arguments.of("recht", new ArrayList<>(0), caseSensitive, WordCategory.nomen),

                Arguments.of("Recht", new ArrayList<>(Arrays.asList(273, 1182, 2311, 99446)), ignoreCase, WordCategory.adj),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(273, 1182, 2311, 99446)), ignoreCase, WordCategory.adj),

                Arguments.of("Recht", new ArrayList<>(0), caseSensitive, WordCategory.adj),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(273, 1182, 2311, 99446)), caseSensitive, WordCategory.adj),

                Arguments.of("Schiffahrt", new ArrayList<>(Arrays.asList(19109)), ignoreCase, WordCategory.nomen),
                Arguments.of("schiffahrt", new ArrayList<>(Arrays.asList(19109)), ignoreCase, WordCategory.nomen),
                Arguments.of("schiffahrt", new ArrayList<>(0), ignoreCase, WordCategory.adj)
        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * all orth forms
     * by WordCategory
     * no Filters
     */
    @ParameterizedTest(name = "{index} {0} {1} {2} {3}")
    @MethodSource({"getLexUnitsAllOrthCatProvider"})
    void getLexUnitsAllOrthCat(String orthForm, List<Integer> expectedIds, boolean useGnetIgnoreCase, WordCategory cat) {
        GermaNet gnet;
        List<LexUnit> actualList;

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        actualList = gnet.getLexUnits(orthForm, cat);
        List<Integer> actualIds = new ArrayList<>(actualList.size());

        for (LexUnit lexUnit : actualList) {
            actualIds.add(lexUnit.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    private static Stream<Arguments> getLexUnitsAllOrthCatProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        return Stream.of(
                Arguments.of("Recht", new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344)), ignoreCase, WordCategory.nomen),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344)), ignoreCase, WordCategory.nomen),

                Arguments.of("Recht", new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344)), caseSensitive, WordCategory.nomen),
                Arguments.of("recht", new ArrayList<>(0), caseSensitive, WordCategory.nomen),

                Arguments.of("Recht", new ArrayList<>(Arrays.asList(435, 1963, 3535, 132285)), ignoreCase, WordCategory.adj),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(435, 1963, 3535, 132285)), ignoreCase, WordCategory.adj),

                Arguments.of("Recht", new ArrayList<>(0), caseSensitive, WordCategory.adj),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(435, 1963, 3535, 132285)), caseSensitive, WordCategory.adj),

                Arguments.of("Schiffahrt", new ArrayList<>(Arrays.asList(26447)), ignoreCase, WordCategory.nomen),
                Arguments.of("schiffahrt", new ArrayList<>(Arrays.asList(26447)), ignoreCase, WordCategory.nomen),
                Arguments.of("schiffahrt", new ArrayList<>(0), ignoreCase, WordCategory.adj)
        );
    }

    /**
     * synset searches
     * ignoreCase true | false in constructor
     * Main orthForm only
     * by WordCategory
     * no Filters
     */
    @ParameterizedTest(name = "{index} {0} {1} {2} {3}")
    @MethodSource({"getSynsetsMainOrthCatProvider"})
    void getSynsetsMainOrthCat(String orthForm, List<Integer> expectedIds, boolean useGnetIgnoreCase, WordCategory cat) {
        GermaNet gnet;
        List<Synset> actualList;

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        actualList = gnet.getSynsets(orthForm, cat, true);
        List<Integer> actualIds = new ArrayList<>(actualList.size());

        for (Synset synset : actualList) {
            actualIds.add(synset.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    private static Stream<Arguments> getSynsetsMainOrthCatProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        return Stream.of(
                Arguments.of("Recht", new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213)), ignoreCase, WordCategory.nomen),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213)), ignoreCase, WordCategory.nomen),

                Arguments.of("Recht", new ArrayList<>(Arrays.asList(32782, 13474, 13475, 28213)), caseSensitive, WordCategory.nomen),
                Arguments.of("recht", new ArrayList<>(0), caseSensitive, WordCategory.nomen),

                Arguments.of("Recht", new ArrayList<>(Arrays.asList(273, 1182, 2311, 99446)), ignoreCase, WordCategory.adj),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(273, 1182, 2311, 99446)), ignoreCase, WordCategory.adj),

                Arguments.of("Recht", new ArrayList<>(0), caseSensitive, WordCategory.adj),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(273, 1182, 2311, 99446)), caseSensitive, WordCategory.adj),

                Arguments.of("Schiffahrt", new ArrayList<>(0), ignoreCase, WordCategory.nomen),
                Arguments.of("schiffahrt", new ArrayList<>(0), ignoreCase, WordCategory.nomen),
                Arguments.of("schiffahrt", new ArrayList<>(0), ignoreCase, WordCategory.adj),

                Arguments.of("Schiffahrt", new ArrayList<>(0), ignoreCase, WordCategory.nomen)

        );
    }

    /**
     * lexunit searches
     * ignoreCase true | false in constructor
     * Main orthForm only
     * by WordCategory
     * no Filters
     */
    @ParameterizedTest(name = "{index} {0} {1} {2} {3}")
    @MethodSource({"getLexUnitsMainOrthCatProvider"})
    void getLexUnitsMainOrthCat(String orthForm, List<Integer> expectedIds, boolean useGnetIgnoreCase, WordCategory cat) {
        GermaNet gnet;
        List<LexUnit> actualList;

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        actualList = gnet.getLexUnits(orthForm, cat, true);
        List<Integer> actualIds = new ArrayList<>(actualList.size());

        for (LexUnit lexUnit : actualList) {
            actualIds.add(lexUnit.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    private static Stream<Arguments> getLexUnitsMainOrthCatProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;

        return Stream.of(
                Arguments.of("Recht", new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344)), ignoreCase, WordCategory.nomen),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344)), ignoreCase, WordCategory.nomen),

                Arguments.of("Recht", new ArrayList<>(Arrays.asList(45139, 18689, 18690, 145344)), caseSensitive, WordCategory.nomen),
                Arguments.of("recht", new ArrayList<>(0), caseSensitive, WordCategory.nomen),

                Arguments.of("Recht", new ArrayList<>(Arrays.asList(435, 1963, 3535, 132285)), ignoreCase, WordCategory.adj),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(435, 1963, 3535, 132285)), ignoreCase, WordCategory.adj),

                Arguments.of("Recht", new ArrayList<>(0), caseSensitive, WordCategory.adj),
                Arguments.of("recht", new ArrayList<>(Arrays.asList(435, 1963, 3535, 132285)), caseSensitive, WordCategory.adj),

                Arguments.of("Schiffahrt", new ArrayList<>(0), ignoreCase, WordCategory.nomen),
                Arguments.of("schiffahrt", new ArrayList<>(0), ignoreCase, WordCategory.nomen),
                Arguments.of("schiffahrt", new ArrayList<>(0), ignoreCase, WordCategory.adj),

                Arguments.of("Schiffahrt", new ArrayList<>(0), ignoreCase, WordCategory.nomen)

        );
    }

    /**
     * synset relation searches
     * ignoreCase true | false in constructor
     * by ID
     */
    @ParameterizedTest(name = "{index} {0} {1} {2} {3}")
    @MethodSource({"getConRelationsProvider"})
    void getRelatedSynsets(int synsetID, Set<ConRel> relations, List<Integer> expectedIds, boolean useGnetIgnoreCase) {
        GermaNet gnet;
        List<Synset> actualList = new ArrayList<>();

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        Synset synset = gnet.getSynsetByID(synsetID);
        for (ConRel conRel : relations) {
            actualList.addAll(synset.getRelatedSynsets(conRel));
        }
        List<Integer> actualIds = new ArrayList<>(actualList.size());

        for (Synset syn : actualList) {
            actualIds.add(syn.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    private static Stream<Arguments> getConRelationsProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;
        int klauenID = 52509;
        Set<ConRel> allConRelSet = new HashSet<>(Arrays.asList(ConRel.values()));
        Set<ConRel> hyponymSet = new HashSet<>();
        hyponymSet.add(ConRel.has_hyponym);
        Set<ConRel> hypernymSet = new HashSet<>();
        hypernymSet.add(ConRel.has_hypernym);

        return Stream.of(
                Arguments.of(klauenID, allConRelSet, new ArrayList<>(Arrays.asList(124504, 52545, 52508, 52516)), ignoreCase),
                Arguments.of(klauenID, allConRelSet, new ArrayList<>(Arrays.asList(124504, 52545, 52508, 52516)), caseSensitive),

                Arguments.of(klauenID, hyponymSet, new ArrayList<>(Arrays.asList(124504, 52545, 52516)), ignoreCase),
                Arguments.of(klauenID, hyponymSet, new ArrayList<>(Arrays.asList(124504, 52545, 52516)), caseSensitive),

                Arguments.of(klauenID, hypernymSet, new ArrayList<>(Arrays.asList(52508)), ignoreCase),
                Arguments.of(klauenID, hypernymSet, new ArrayList<>(Arrays.asList(52508)), caseSensitive)
        );
    }

    /**
     * lexunit relation searches
     * ignoreCase true | false in constructor
     * by ID
     */
    @ParameterizedTest(name = "{index} {0} {1} {2} {3}")
    @MethodSource({"getLexRelationsProvider"})
    void getRelatedLexUnits(int lexUnitID, Set<LexRel> relations, List<Integer> expectedIds, boolean useGnetIgnoreCase) {
        GermaNet gnet;
        List<LexUnit> actualList = new ArrayList<>();

        gnet = (useGnetIgnoreCase) ? gnetIgnoreCase : gnetCaseSensitive;
        LexUnit lexUnit = gnet.getLexUnitByID(lexUnitID);
        for (LexRel lexRel : relations) {
            actualList.addAll(lexUnit.getRelatedLexUnits(lexRel));
        }
        List<Integer> actualIds = new ArrayList<>(actualList.size());

        for (LexUnit lu : actualList) {
            actualIds.add(lu.getId());
        }

        Collections.sort(expectedIds);
        Collections.sort(actualIds);
        assertEquals(expectedIds, actualIds);
    }

    private static Stream<Arguments> getLexRelationsProvider() {
        boolean ignoreCase = true;
        boolean caseSensitive = false;
        int laufenID = 81287;
        Set<LexRel> allLexRelSet = new HashSet<>(Arrays.asList(LexRel.values()));
        Set<LexRel> synPartSet = new HashSet<>();
        synPartSet.add(LexRel.has_synonym);
        synPartSet.add(LexRel.has_participle);

        return Stream.of(
                Arguments.of(laufenID, allLexRelSet, new ArrayList<>(Arrays.asList(81288, 676)), ignoreCase),
                Arguments.of(laufenID, allLexRelSet, new ArrayList<>(Arrays.asList(81288, 676)), caseSensitive),

                Arguments.of(laufenID, synPartSet, new ArrayList<>(Arrays.asList(81288, 676)), ignoreCase),
                Arguments.of(laufenID, synPartSet, new ArrayList<>(Arrays.asList(81288, 676)), caseSensitive)
        );
    }

    @Test
    void transRelatedTest() {
        List<List<Synset>> rels = gnetCaseSensitive.getSynsetByID(52509).getTransRelatedSynsets(ConRel.has_hypernym);
        List<Integer> expectedList = new ArrayList<>(Arrays.asList(52509, 52508, 52497, 51001));
        int expected;
        int actual;

        for (int i=0; i < 4; i++) {
            List<Synset> list = rels.get(i);
            if (list.size() != 1) {
                fail("size of each list should be 1");
            }
            actual = list.get(0).getId();
            expected = expectedList.get(i);

            assertEquals(expected, actual);
        }
    }

    @Test
    void immutable1Test() {
        List<Synset> expected = gnetCaseSensitive.getSynsets();
        List<Synset> cleared = gnetCaseSensitive.getSynsets();
        cleared.clear();
        List<Synset> afterClear = gnetCaseSensitive.getSynsets();
        assertEquals(expected, afterClear);
    }

    @Test
    void immutable2Test() {
        int laufenID = 52047;
        List<Synset> expected = gnetCaseSensitive.getSynsetByID(laufenID).getRelatedSynsets(ConRel.has_hypernym);
        List<Synset> cleared = gnetCaseSensitive.getSynsetByID(laufenID).getRelatedSynsets(ConRel.has_hypernym);
        cleared.clear();
        List<Synset> afterClear = gnetCaseSensitive.getSynsetByID(laufenID).getRelatedSynsets(ConRel.has_hypernym);
        assertEquals(expected, afterClear);
    }

    @Test
    void immutable3Test() {
        int laufenID = 52047;
        List<LexUnit> expected = gnetCaseSensitive.getSynsetByID(laufenID).getLexUnits();
        List<LexUnit> cleared = gnetCaseSensitive.getSynsetByID(laufenID).getLexUnits();
        cleared.clear();
        List<LexUnit> afterClear = gnetCaseSensitive.getSynsetByID(laufenID).getLexUnits();
        assertEquals(expected, afterClear);
    }

    @Test
    void immutable4Test() {
        int laufenID = 52047;
        List<IliRecord> expected = gnetCaseSensitive.getSynsetByID(laufenID).getIliRecords();
        List<IliRecord> cleared = gnetCaseSensitive.getSynsetByID(laufenID).getIliRecords();
        cleared.clear();
        List<IliRecord> afterClear = gnetCaseSensitive.getSynsetByID(laufenID).getIliRecords();
        assertEquals(expected, afterClear);
    }
}

