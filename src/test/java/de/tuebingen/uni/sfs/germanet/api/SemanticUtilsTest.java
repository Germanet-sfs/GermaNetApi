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

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static de.tuebingen.uni.sfs.germanet.api.GermaNet.GNROOT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Junit tests for the semantic relatedness functionality of the GermaNet API.
 *
 * @author Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SemanticUtilsTest {
    static GermaNet gnetCaseSensitive;
    static SemanticUtils semanticUtils;
    static String dataPath;

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticUtilsTest.class);

    @BeforeAll
    static void setUp() {
        try {
            String userHome = System.getProperty("user.home");
            String sep = System.getProperty("file.separator");
            dataPath = userHome + sep + "Data" + sep;
            String goodDataPath = dataPath + "GN-XML-ForApiUnitTesting/";

            gnetCaseSensitive = new GermaNet(goodDataPath, false);
            semanticUtils = gnetCaseSensitive.getSemanticUtils();
        } catch (IOException ex) {
            LOGGER.error("\nGermaNet data not found at <homeDirectory>/Data/GN-XML-ForApiUnitTesting/\nAborting...", ex);
            System.exit(0);
        } catch (XMLStreamException ex) {
            LOGGER.error("\nUnable to load GermaNet data at <homeDirectory>/Data/GN-XML-ForApiUnitTesting/\nAborting...", ex);
            System.exit(0);
        }
    }

    @ParameterizedTest(name = "{0} {1} {2} {3}")
    @MethodSource({"lcsNounProvider",
            "lcsVerbProvider",
            "lcsAdjProvider",
            "lcsLongestShortestNounProvider"})
    void lcsTest(String orthforms, int sID1, int sID2, Set<LeastCommonSubsumer> expected) {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(sID1);
        Synset synset2 = gnetCaseSensitive.getSynsetByID(sID2);

        Set<LeastCommonSubsumer> actual = semanticUtils.getLeastCommonSubsumer(synset1, synset2);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> lcsNounProvider() {

        // Apfel - Birne -> Kernobst
        int apfelID = 39494;
        int birneID = 39495;
        int kernobstID = 39491;
        Set<LeastCommonSubsumer> apfelBirneExpected = Sets.newHashSet(
                new LeastCommonSubsumer(kernobstID, Sets.newHashSet(apfelID, birneID), 2)
        );

        // Apfel - Baum -> Objekt
        int baumID = 46042;
        int objektID = 50981;
        Set<LeastCommonSubsumer> apfelBaumExpected = Sets.newHashSet(
                new LeastCommonSubsumer(objektID, Sets.newHashSet(apfelID, baumID), 11)
        );

        // Pferd (animal) - Pferd (Chess) -> Objekt
        int pferdAnimalID = 50869;
        int pferdChessID = 11106;
        Set<LeastCommonSubsumer> pferdExpected = Sets.newHashSet(
                new LeastCommonSubsumer(objektID, Sets.newHashSet(pferdAnimalID, pferdChessID), 20)
        );

        // Chinarindenbaum - Kompresse -> Medizinischer Artikel
        int chinarindenbaumID = 46665;
        int kompresseID = 7922;
        int medizinischerArtikelID = 7917;
        Set<LeastCommonSubsumer> chinarindenbaumKompresseExpected = Sets.newHashSet(
                new LeastCommonSubsumer(medizinischerArtikelID, Sets.newHashSet(chinarindenbaumID, kompresseID), 6)
        );

        // Kraut - Heilpflanze -> Kraut (Kraut is the direct hypernym of Heilpflanze)
        int krautID = 46657;
        int heilpflanzeID = 46659;
        Set<LeastCommonSubsumer> krautHeilpflanzeExpected = Sets.newHashSet(
                new LeastCommonSubsumer(krautID, Sets.newHashSet(krautID, heilpflanzeID), 1)
        );

        return Stream.of(
                Arguments.of("Apfel - Birne -> Kernobst", apfelID, birneID, apfelBirneExpected),
                Arguments.of("Apfel - Baum -> Objekt", apfelID, baumID, apfelBaumExpected),
                Arguments.of("Pferd (animal) - Pferd (Chess) -> Objekt", pferdAnimalID, pferdChessID, pferdExpected),
                Arguments.of("Chinarindenbaum - Kompresse -> Medizinischer Artikel", chinarindenbaumID, kompresseID, chinarindenbaumKompresseExpected),
                Arguments.of("Kraut - Heilpflanze -> Kraut", krautID, heilpflanzeID, krautHeilpflanzeExpected)
        );
    }

    private static Stream<Arguments> lcsVerbProvider() {

        // einkaufen - auftun -> geben
        int einkaufenID = 107484;
        int auftunID = 61151;
        int gebenID = 52270;
        Set<LeastCommonSubsumer> einkaufenAuftunExpected = Sets.newHashSet(
                new LeastCommonSubsumer(gebenID, Sets.newHashSet(einkaufenID, auftunID), 7)
        );

        return Stream.of(
                Arguments.of("einkaufen - auftun -> geben", einkaufenID, auftunID, einkaufenAuftunExpected)
        );

    }

    private static Stream<Arguments> lcsAdjProvider() {

        // regressiv - denunziatorisch -> GNROOT
        int regressivID = 94411;
        int denunziatorischID = 94543;
        Set<LeastCommonSubsumer> regressivDenunziatorischExpected = Sets.newHashSet(
                new LeastCommonSubsumer(0, Sets.newHashSet(regressivID, denunziatorischID), 18),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(regressivID, denunziatorischID), 18)
        );

        // regressiv - blasphemisch -> GNROOT
        int blasphemischID = 94396;
        Set<LeastCommonSubsumer> regressivBlasphemischExpected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(regressivID, blasphemischID), 20)
        );

        return Stream.of(
                Arguments.of("regressiv - denunziatorisch -> GNROOT", regressivID, denunziatorischID, regressivDenunziatorischExpected),
                Arguments.of("regressiv - blasphemisch -> GNROOT", regressivID, blasphemischID, regressivBlasphemischExpected)
        );
    }

    private static Stream<Arguments> lcsLongestShortestNounProvider() {

        // Kleines Johanniswürmchen - Lebertransplantation -> GNROOT
        int kleinesjohanniswuermchenID = 49774;
        int lebertransplantationID = 83979;
        Set<LeastCommonSubsumer> kleinesjohanniswuermchenLebertransplantationExpected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesjohanniswuermchenID, lebertransplantationID), 35)
        );

        // Kleines Johanniswürmchen - Nierentransplantation -> GNROOT
        int nierentransplantationID = 20560;
        Set<LeastCommonSubsumer> kleinesjohanniswuermchenNierentransplantationExpected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesjohanniswuermchenID, nierentransplantationID), 35)
        );

        // Kleines Johanniswürmchen - Herztransplantation -> GNROOT
        int herztransplantationID = 20561;
        Set<LeastCommonSubsumer> kleinesjohanniswuermchenHerztransplantationExpected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesjohanniswuermchenID, herztransplantationID), 35)
        );

        // Kleines Johanniswürmchen - Fußreflexzonenmassage -> GNROOT
        int fussreflexzonenmassageID = 138670;
        Set<LeastCommonSubsumer> kleinesjohanniswuermchenFussreflexzonenmassageExpected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesjohanniswuermchenID, fussreflexzonenmassageID), 35)
        );

        return Stream.of(
                Arguments.of("Kleines Johanniswürmchen - Lebertransplantation -> GNROOT", kleinesjohanniswuermchenID, lebertransplantationID, kleinesjohanniswuermchenLebertransplantationExpected),
                Arguments.of("Kleines Johanniswürmchen - Nierentransplantation -> GNROOT", kleinesjohanniswuermchenID, nierentransplantationID, kleinesjohanniswuermchenNierentransplantationExpected),
                Arguments.of("Kleines Johanniswürmchen - Herztransplantation -> GNROOT", kleinesjohanniswuermchenID, herztransplantationID, kleinesjohanniswuermchenHerztransplantationExpected),
                Arguments.of("Kleines Johanniswürmchen - Fußreflexzonenmassage -> GNROOT", kleinesjohanniswuermchenID, fussreflexzonenmassageID, kleinesjohanniswuermchenFussreflexzonenmassageExpected)
        );
    }

    @Test
    void longestShortestNounTest() {
        // Kleines Johanniswürmchen - Lebertransplantation,
        // Kleines Johanniswürmchen - Nierentransplantation
        // Kleines Johanniswürmchen - Herztransplantation
        // Kleines Johanniswürmchen - Fußreflexzonenmassage
        Set<LeastCommonSubsumer> expected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(49774, 83979), 35),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(49774, 20560), 35),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(49774, 20561), 35),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(49774, 138670), 35)
        );

        Set<LeastCommonSubsumer> actual = semanticUtils.getLongestLeastCommonSubsumer(WordCategory.nomen);

        LOGGER.info("actual longestShortest LCS(s) for noun: {}", actual);

        assertEquals(expected, actual);
    }

    @Test
    void lcsNullTest() {
        int laufenID = 57835;
        int heilpflanzeID = 46659;

        Synset synset1 = gnetCaseSensitive.getSynsetByID(laufenID); // laufen
        Synset synset2 = gnetCaseSensitive.getSynsetByID(heilpflanzeID); // Heilpflanze

        // different word categories - should be null
        Set<LeastCommonSubsumer> actual = semanticUtils.getLeastCommonSubsumer(synset1, synset2);

        assertEquals(null, actual);
    }

    @Test
    void longestShortestVerbTest() {
        // verlauten - hochschlagen
        // verlauten - einstülpen
        // verlauten - bemehlen
        // verlauten - hochrutschen
        // anmustern - einstülpen
        // anmustern - bemehlen
        // anmustern - hochrutschen
        // anmustern - hochschlagen
        Set<LeastCommonSubsumer> expected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(106731, 123246), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(106731, 120154), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(106731, 57534), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(106731, 123240), 28),

                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(119463, 120154), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(119463, 57534), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(119463, 123240), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(119463, 123246), 28)
        );

        Set<LeastCommonSubsumer> actual = semanticUtils.getLongestLeastCommonSubsumer(WordCategory.verben);

        LOGGER.info("actual longestShortest LCS(s) for verb: {}", actual);

        assertEquals(expected, actual);
    }

    @Test
    void longestShortestAdjTest() {

        // rückgebildet - gotteslästerlich
        // rückgebildet - blasphemisch
        // regressiv - gotteslästerlich
        // regressiv - blasphemisch
        Set<LeastCommonSubsumer> expected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(95326, 95987), 20),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(95326, 94396), 20),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(94411, 95987), 20),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(94411, 94396), 20)
        );

        Set<LeastCommonSubsumer> actual = semanticUtils.getLongestLeastCommonSubsumer(WordCategory.adj);

        LOGGER.info("actual longestShortest LCS(s) for adj: {}", actual);

        assertEquals(expected, actual);
    }

    // ToDo: fix maxDepth tests

    @Test
    void maxDepthNounTest() {

        List<Synset> synsets = gnetCaseSensitive.getSynsets(WordCategory.nomen);
        int maxDepth = 0;

        for (Synset synset : synsets) {
            int depth = synset.getDepth();
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }

        // max depth for nouns is 20
        assertEquals(20, maxDepth);
    }

    @Test
    void maxDepthVerbTest() {

        List<Synset> synsets = gnetCaseSensitive.getSynsets(WordCategory.verben);
        int maxDepth = 0;

        for (Synset synset : synsets) {
            int depth = synset.getDepth();
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }

        // max depth for verbs is 15
        assertEquals(15, maxDepth);
    }

    @Test
    void maxDepthAdjTest() {

        List<Synset> synsets = gnetCaseSensitive.getSynsets(WordCategory.adj);
        int maxDepth = 0;

        for (Synset synset : synsets) {
            int depth = synset.getDepth();
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }

        // max depth for adj is 10
        assertEquals(10, maxDepth);
    }

    @ParameterizedTest(name = "{0} {1} {2} PathBetweenSynsets")
    @MethodSource({"pathsBetweenSynsetsProvider"})
    void pathsBetweenSynsetsTest(Synset synset1, Synset synset2, Set<List<Synset>> expected) {
        Set<List<Synset>> actual = semanticUtils.getPathBetweenSynsets(synset1, synset2);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> pathsBetweenSynsetsProvider() {
        Synset chinarindenbaum = gnetCaseSensitive.getSynsetByID(46665);
        Synset alleebaum = gnetCaseSensitive.getSynsetByID(100607);
        Synset baum = gnetCaseSensitive.getSynsetByID(46042);
        Synset obstbaum = gnetCaseSensitive.getSynsetByID(46682);
        Synset apfelbaum = gnetCaseSensitive.getSynsetByID(46683);
        Synset holzpflanze = gnetCaseSensitive.getSynsetByID(46041);
        Synset pflanze = gnetCaseSensitive.getSynsetByID(44960);
        Synset nutzpflanze = gnetCaseSensitive.getSynsetByID(46311);
        Synset kulturpflanze = gnetCaseSensitive.getSynsetByID(44965);
        Synset giftpflanze = gnetCaseSensitive.getSynsetByID(46650);

        Set<List<Synset>> expectedChinaAllee = new HashSet<>();
        List<Synset> pathChinaAllee = new ArrayList<>();
        pathChinaAllee.add(chinarindenbaum);
        pathChinaAllee.add(baum);
        pathChinaAllee.add(alleebaum);
        expectedChinaAllee.add(pathChinaAllee);

        Set<List<Synset>> expectedChinaAlpfel = new HashSet<>();
        List<Synset> pathChinaApfel = new ArrayList<>();
        pathChinaApfel.add(chinarindenbaum);
        pathChinaApfel.add(baum);
        pathChinaApfel.add(obstbaum);
        pathChinaApfel.add(apfelbaum);
        expectedChinaAlpfel.add(pathChinaApfel);

        Set<List<Synset>> expectedAlpfelGift = new HashSet<>();
        List<Synset> pathApfelGift1 = new ArrayList<>();
        pathApfelGift1.add(apfelbaum);
        pathApfelGift1.add(obstbaum);
        pathApfelGift1.add(nutzpflanze);
        pathApfelGift1.add(kulturpflanze);
        pathApfelGift1.add(pflanze);
        pathApfelGift1.add(giftpflanze);
        expectedAlpfelGift.add(pathApfelGift1);
        List<Synset> pathApfelGift2 = new ArrayList<>();
        pathApfelGift2.add(apfelbaum);
        pathApfelGift2.add(obstbaum);
        pathApfelGift2.add(baum);
        pathApfelGift2.add(holzpflanze);
        pathApfelGift2.add(pflanze);
        pathApfelGift2.add(giftpflanze);
        expectedAlpfelGift.add(pathApfelGift2);

        return Stream.of(
                Arguments.of(chinarindenbaum, alleebaum, expectedChinaAllee),
                Arguments.of(chinarindenbaum, apfelbaum, expectedChinaAlpfel),
                Arguments.of(apfelbaum, giftpflanze, expectedAlpfelGift)
        );
    }

    // ToDo: check measures tests - some may be too lenient

    @ParameterizedTest(name = "{0} {1} {2} Path")
    @MethodSource({"simplePathProvider"})
    void simplePathTest(int sID1, int sID2, int normalizedMax, double expected) {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(sID1);
        Synset synset2 = gnetCaseSensitive.getSynsetByID(sID2);

        double epsilon = 0.00001; // tolerance for working with doubles
        double actual = semanticUtils.getSimilarityPath(synset1, synset2, normalizedMax);
        assertEquals(expected, actual, epsilon);
    }

    private static Stream<Arguments> simplePathProvider() {

        // Bambus - Veilchen
        int bambusID = 46047;
        int veilchenID = 45380;
        double bambusVeilchenExpected = 0.885714;

        // Kleines Johanniswürmchen - Lebertransplantation
        int kleinesJohanniswürmchenID = 49774;
        int lebertransplantationID = 83979;
        double unsimilarExpected = 0.0;
        double identityExpected = 1.0;

        // bemehlen - anmustern
        int bemehlenID = 57534;
        int anmusternID = 119463;

        // blasphemisch - regressiv
        int blasphemischID = 94396;
        int regressivID = 94411;

        return Stream.of(
                Arguments.of(bambusID, veilchenID, 0, bambusVeilchenExpected),
                Arguments.of(kleinesJohanniswürmchenID, lebertransplantationID, 0, unsimilarExpected),
                Arguments.of(bemehlenID, anmusternID, 0, unsimilarExpected),
                Arguments.of(bemehlenID, bemehlenID, 0, identityExpected),
                Arguments.of(blasphemischID, regressivID, 0, unsimilarExpected),
                Arguments.of(blasphemischID, regressivID, 10, unsimilarExpected),
                Arguments.of(blasphemischID, blasphemischID, 0, identityExpected),
                Arguments.of(blasphemischID, blasphemischID, 10, 10.0)
        );
    }

    @ParameterizedTest(name = "{0} {1} {2} Leacock and Chodorow")
    @MethodSource({"leacockChodorowProvider"})
    void leacockChodorowTest(int sID1, int sID2, int normalizedMax, double expected) {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(sID1);
        Synset synset2 = gnetCaseSensitive.getSynsetByID(sID2);

        double epsilon = 0.1; // tolerance for working with doubles
        double actual = semanticUtils.getSimilarityLeacockChodorow(synset1, synset2, normalizedMax);
        assertEquals(expected, actual, epsilon);
    }

    private static Stream<Arguments> leacockChodorowProvider() {

        // Bambus - Veilchen
        int bambusID = 46047;
        int veilchenID = 45380;
        double bambusVeilchenRawExpected = 2.1;
        double identityExpected = 1.0; //3.6888;

        // Kleines Johanniswürmchen - Lebertransplantation
        int kleinesJohanniswuermchenID = 49774;
        int lebertransplantationID = 83979;
        double kJohannisLebertransExpected = 0.0;

        // bemehlen - anmustern
        int bemehlenID = 57534;
        int anmusternID = 119463;

        // blasphemisch - regressiv
        int blasphemischID = 94396;
        int regressivID = 94411;

        return Stream.of(
                Arguments.of(bambusID, veilchenID, 0, bambusVeilchenRawExpected),
                Arguments.of(bambusID, veilchenID, 10, 5.62),
                Arguments.of(bambusID, bambusID, 1, identityExpected),
                Arguments.of(bambusID, bambusID, 10, 10.0),
                Arguments.of(veilchenID, veilchenID, 1, identityExpected),
                Arguments.of(kleinesJohanniswuermchenID, kleinesJohanniswuermchenID, 1, identityExpected),
                Arguments.of(lebertransplantationID, lebertransplantationID, 1, identityExpected),
                Arguments.of(kleinesJohanniswuermchenID, lebertransplantationID, 1, kJohannisLebertransExpected),
                Arguments.of(bemehlenID, anmusternID, 0, 0.16),
                Arguments.of(bemehlenID, anmusternID, 1, 0.0),
                Arguments.of(bemehlenID, bemehlenID, 0, 3.53),
                Arguments.of(bemehlenID, bemehlenID, 1, 1.0),
                Arguments.of(blasphemischID, regressivID, 0, 0.05),
                Arguments.of(blasphemischID, regressivID, 1, 0.0),
                Arguments.of(blasphemischID, blasphemischID, 0, 3.09),
                Arguments.of(blasphemischID, blasphemischID, 1, 1.0)
        );
    }

    // Find all synsets whose wordCategory does not match that of all its direct hypernyms.
    // This should never happen.
    @Test
    void catDoesntMatchHyperTest() {
        List<Synset> synsetList = gnetCaseSensitive.getSynsets();

        int cnt = 0;
        for (Synset synset1 : synsetList) {
            boolean mismatch = false;
            for (Synset hyper : synset1.getRelatedSynsets(ConRel.has_hypernym)) {
                if (hyper.getId() == GNROOT_ID) {
                    continue;
                }
                if (synset1.getWordCategory() != hyper.getWordCategory()) {
                    mismatch = true;
                    cnt++;
                    LOGGER.warn("WordCat mismatch. synset1: [{}] \tsynset2: [{}]", synset1, hyper);
                }
            }
        }
        assertEquals(0, cnt);
    }

    //@Test
    // Find all noun synsets that have exactly 1 hypernym,
    // and whose wordClass does not match that of its hypernym.
    // For informational purposes only, since this can sometimes happen.
    void wordClassOneHyperDoesntMatchNounTest() {
        List<Synset> synsetList = gnetCaseSensitive.getSynsets();

        int cnt = 0;
        for (Synset synset : synsetList) {
            if (!synset.inWordCategory(WordCategory.nomen)) {
                continue;
            }
            List<Synset> hypernyms = synset.getRelatedSynsets(ConRel.has_hypernym);

            // One hypernym, which is not GNROOT
            if (hypernyms.size() == 1) {
                Synset hyper = hypernyms.get(0);
                if ((hyper.getId() != GNROOT_ID)
                        && (synset.getWordClass() != hyper.getWordClass())) {
                    LOGGER.warn("WordClass of noun does not match its hypernym: synset: {}", synset);
                    cnt++;
                }
            }
        }
        LOGGER.info("{} instances of noun that does not match WordClass of its hypernym.", cnt);
    }

    //@Test
    // Find all verb synsets that have exactly 1 hypernym,
    // and whose wordClass does not match that of its hypernym.
    // For informational purposes only, since this can sometimes happen.
    void wordClassOneHyperDoesntMatchVerbTest() {
        List<Synset> synsetList = gnetCaseSensitive.getSynsets();

        int cnt = 0;
        for (Synset synset : synsetList) {
            if (!synset.inWordCategory(WordCategory.verben)) {
                continue;
            }
            List<Synset> hypernyms = synset.getRelatedSynsets(ConRel.has_hypernym);
            if (hypernyms.size() == 1) {
                Synset hyper = hypernyms.get(0);
                if ((hyper.getId() != GNROOT_ID)
                        && (synset.getWordClass() != hyper.getWordClass())) {
                    LOGGER.warn("WordClass of verb does not match its hypernym: synset: {}", synset);
                    cnt++;
                }
            }
        }
        LOGGER.info("{} instances of verb that does not match WordClass of its hypernym.", cnt);
    }

    //@Test
    // Find all adjective synsets that have exactly 1 hypernym,
    // and whose wordClass does not match that of its hypernym.
    // For informational purposes only, since this can sometimes happen.
    void wordClassOneHyperDoesntMatchAdjTest() {
        List<Synset> synsetList = gnetCaseSensitive.getSynsets();

        int cnt = 0;
        for (Synset synset : synsetList) {
            if (!synset.inWordCategory(WordCategory.adj)) {
                continue;
            }
            List<Synset> hypernyms = synset.getRelatedSynsets(ConRel.has_hypernym);
            if (hypernyms.size() == 1) {
                Synset hyper = hypernyms.get(0);
                if ((hyper.getId() != GNROOT_ID)
                        && (synset.getWordClass() != hyper.getWordClass())) {
                    LOGGER.warn("WordClass of adj does not match its hypernym: synset: {}", synset);
                    cnt++;
                }
            }
        }
        LOGGER.info("{} instances of adj that does not match WordClass of its hypernym.", cnt);
    }

    //@Test
    // Find all noun synsets that have multiple hypernyms,
    // and whose wordClass does not match that of any of its hypernyms.
    // For informational purposes only, since this can sometimes happen.
    void wordClassMultHyperNoneMatchNounTest() {
        List<Synset> synsetList = gnetCaseSensitive.getSynsets();

        int cnt = 0;
        for (Synset synset : synsetList) {
            if (!synset.inWordCategory(WordCategory.nomen)) {
                continue;
            }
            List<Synset> hypernyms = synset.getRelatedSynsets(ConRel.has_hypernym);
            if (hypernyms.size() > 1) {
                boolean matchFound = false;
                for (Synset hyper : hypernyms) {
                    if (synset.getWordClass() == hyper.getWordClass()) {
                        matchFound = true;
                        break;
                    }
                }
                if (!matchFound) {
                    LOGGER.warn("WordClass of noun does not match any of its hypernyms: synset: {}", synset);
                    cnt++;
                }
            }
        }
        LOGGER.info("{} instances of noun that does not match WordClass of any of its hypernyms.", cnt);
    }

    //@Test
    // Find all verb synsets that have multiple hypernyms,
    // and whose wordClass does not match that of any of its hypernyms.
    // For informational purposes only, since this can sometimes happen.
    void wordClassMultHyperNoneMatchVerbTest() {
        List<Synset> synsetList = gnetCaseSensitive.getSynsets();

        int cnt = 0;
        for (Synset synset : synsetList) {
            if (!synset.inWordCategory(WordCategory.verben)) {
                continue;
            }
            List<Synset> hypernyms = synset.getRelatedSynsets(ConRel.has_hypernym);
            if (hypernyms.size() > 1) {
                boolean matchFound = false;
                for (Synset hyper : hypernyms) {
                    if (synset.getWordClass() == hyper.getWordClass()) {
                        matchFound = true;
                        break;
                    }
                }
                if (!matchFound) {
                    LOGGER.warn("WordClass of verb does not match any of its hypernyms: synset: {}", synset);
                    cnt++;
                }
            }
        }
        LOGGER.info("{} instances of verb that does not match WordClass of any of its hypernyms.", cnt);
    }

    //@Test
    // Find all adjective synsets that have multiple hypernyms,
    // and whose wordClass does not match that of any of its hypernyms.
    // For informational purposes only, since this can sometimes happen.
    void wordClassMultHyperNoneMatchAdjTest() {
        List<Synset> synsetList = gnetCaseSensitive.getSynsets();

        int cnt = 0;
        for (Synset synset : synsetList) {
            if (!synset.inWordCategory(WordCategory.adj)) {
                continue;
            }
            List<Synset> hypernyms = synset.getRelatedSynsets(ConRel.has_hypernym);
            if (hypernyms.size() > 1) {
                boolean matchFound = false;
                for (Synset hyper : hypernyms) {
                    if (synset.getWordClass() == hyper.getWordClass()) {
                        matchFound = true;
                        break;
                    }
                }
                if (!matchFound) {
                    LOGGER.warn("WordClass of adj does not match any of its hypernyms: synset: {}", synset);
                    cnt++;
                }
            }
        }
        LOGGER.info("{} instances of adj that does not match WordClass of any of its hypernyms.", cnt);
    }
}
