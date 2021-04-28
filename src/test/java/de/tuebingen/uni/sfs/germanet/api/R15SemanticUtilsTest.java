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
import org.junit.jupiter.api.AfterAll;
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
import java.util.*;
import java.util.stream.Stream;

import static de.tuebingen.uni.sfs.germanet.api.GermaNet.GNROOT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Junit tests for the semantic relatedness functionality of the GermaNet API.
 *
 * @author Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class R15SemanticUtilsTest {
    static GermaNet gnet;
    static SemanticUtils semanticUtils;
    static String dataPath;

    private static final Logger LOGGER = LoggerFactory.getLogger(R15SemanticUtilsTest.class);

    // nouns: synset IDs
    static int alleebaumID = 100607;
    static int apfelbaumId = 46683;
    static int apfelID = 39494;
    static int bambusID = 46047;
    static int baumID = 46042;
    static int birneID = 39495;
    static int chinarindenbaumID = 46665;
    static int fussreflexzonenmassage1ID = 138670; // Fußreflexzonenmassage - Fußmassage
    static int fussreflexzonenmassage2ID = 145392; // Fußreflexzonenmassage - Reflexzonenmassage
    static int giftpflanzeId = 46650;
    static int heilpflanzeID = 46659;
    static int herztransplantationID = 20561;
    static int holzpflanzeID = 46041;
    static int kernobstID = 39491;
    static int kleinesJohanniswuermchenID = 49774;
    static int kompresseID = 7922;
    static int krautID = 46657;
    static int kulturpflanzeID = 44965;
    static int lebertransplantationID = 83979;
    static int lungentransplantationID = 147253;
    static int medizinischerArtikelID = 7917;
    static int mengeID = 33224;
    static int nierentransplantationID = 20560;
    static int nutzpflanzeID = 46311;
    static int objektID = 50981;
    static int obstbaumID = 46682;
    static int pferdAnimalID = 50869;
    static int pferdChessID = 11106;
    static int pflanzeID = 44960;
    static int veilchenID = 45380;

    // verbs: synset IDs
    static int anmusternID = 119463;
    static int aufkrempelnID = 150086;
    static int auftunID = 61151;
    static int bemehlenID = 57534;
    static int bewerbenID = 81102;
    static int einkaufenID = 107484;
    static int einstuelpenID = 120154;
    static int fahrenID = 57299;
    static int gebenID = 52270;
    static int hochrutschenID = 123240;
    static int hochschlagenID = 123246;
    static int laufenID = 57835;
    static int riechenID = 58565;
    static int schmeckenID = 58578;
    static int sollenID = 52068;
    static int springenID = 57328;
    static int verlautenID = 106731;

    // adj: synset IDs
    static int blasphemischID = 94396;
    static int denunziatorischID = 94543;
    static int gotteslaesterlichID = 95987;
    static int regressivID = 94411;
    static int rueckgebildetID = 95326;



    @BeforeAll
    static void setUp() {
        try {
            String release = "15";
            String userHome = System.getProperty("user.home");
            String sep = System.getProperty("file.separator");
            dataPath = userHome + sep + "Data" + sep + "GermaNetForApiUnitTesting" + sep;
            String goodDataPath = dataPath + "R" + release + sep + "XML-Valid" + sep;
            String freqListDir = dataPath + "R" + release + sep + "GN_V" + release + "0-FreqLists" + sep;
            String nounFreqListPath = freqListDir + "noun_freqs_decow14_16.txt";
            String verbFreqListPath = freqListDir + "verb_freqs_decow14_16.txt";
            String adjFreqListPath = freqListDir + "adj_freqs_decow14_16.txt";

            gnet = new GermaNet(goodDataPath, nounFreqListPath, verbFreqListPath, adjFreqListPath);
            semanticUtils = gnet.getSemanticUtils();
        } catch (IOException ex) {
            LOGGER.error("\nGermaNet data not found at {} \nAborting...", dataPath, ex);
            System.exit(0);
        } catch (XMLStreamException ex) {
            LOGGER.error("\nUnable to load GermaNet data at {} \nAborting...", dataPath, ex);
            System.exit(0);
        }
    }

    @AfterAll
    void cleanup() {
        gnet = null;
    }

    @ParameterizedTest(name = "{0} {1} {2} {3}")
    @MethodSource({"lcsNounProvider",
            "lcsVerbProvider",
            "lcsAdjProvider",
            "lcsLongestShortestNounProvider"})
    void lcsTest(String orthforms, int sID1, int sID2, Set<LeastCommonSubsumer> expected) {
        Synset synset1 = gnet.getSynsetByID(sID1);
        Synset synset2 = gnet.getSynsetByID(sID2);

        Set<LeastCommonSubsumer> actual = semanticUtils.getLeastCommonSubsumers(synset1, synset2);

        assertEquals(expected, actual, orthforms);
    }

    private static Stream<Arguments> lcsNounProvider() {

        // Apfel - Birne -> Kernobst
        Set<LeastCommonSubsumer> apfelBirneExpected = Sets.newHashSet(
                new LeastCommonSubsumer(kernobstID, Sets.newHashSet(apfelID, birneID), 2)
        );

        // Apfel - Baum -> Objekt
        Set<LeastCommonSubsumer> apfelBaumExpected = Sets.newHashSet(
                new LeastCommonSubsumer(objektID, Sets.newHashSet(apfelID, baumID), 11)
        );

        // Pferd (animal) - Pferd (Chess) -> Objekt
        Set<LeastCommonSubsumer> pferdExpected = Sets.newHashSet(
                new LeastCommonSubsumer(objektID, Sets.newHashSet(pferdAnimalID, pferdChessID), 20)
        );

        // Chinarindenbaum - Kompresse -> Medizinischer Artikel
        Set<LeastCommonSubsumer> chinarindenbaumKompresseExpected = Sets.newHashSet(
                new LeastCommonSubsumer(medizinischerArtikelID, Sets.newHashSet(chinarindenbaumID, kompresseID), 6)
        );

        // Kraut - Heilpflanze -> Kraut (Kraut is the direct hypernym of Heilpflanze)
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
        Set<LeastCommonSubsumer> einkaufenAuftunExpected = Sets.newHashSet(
                new LeastCommonSubsumer(gebenID, Sets.newHashSet(einkaufenID, auftunID), 7)
        );

        return Stream.of(
                Arguments.of("einkaufen - auftun -> geben", einkaufenID, auftunID, einkaufenAuftunExpected)
        );

    }

    private static Stream<Arguments> lcsAdjProvider() {

        // regressiv - denunziatorisch -> GNROOT
        Set<LeastCommonSubsumer> regressivDenunziatorischExpected = Sets.newHashSet(
                new LeastCommonSubsumer(0, Sets.newHashSet(regressivID, denunziatorischID), 18),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(regressivID, denunziatorischID), 18)
        );

        // regressiv - blasphemisch -> GNROOT
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
        Set<LeastCommonSubsumer> kleinesjohanniswuermchenLebertransplantationExpected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesJohanniswuermchenID, lebertransplantationID), 35)
        );

        // Kleines Johanniswürmchen - Nierentransplantation -> GNROOT
        Set<LeastCommonSubsumer> kleinesjohanniswuermchenNierentransplantationExpected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesJohanniswuermchenID, nierentransplantationID), 35)
        );

        // Kleines Johanniswürmchen - Herztransplantation -> GNROOT
        Set<LeastCommonSubsumer> kleinesjohanniswuermchenHerztransplantationExpected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesJohanniswuermchenID, herztransplantationID), 35)
        );

        // Kleines Johanniswürmchen - Fußreflexzonenmassage -> GNROOT
        Set<LeastCommonSubsumer> kleinesjohanniswuermchenFussreflexzonenmassageExpected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesJohanniswuermchenID, fussreflexzonenmassage1ID), 35)
        );

        return Stream.of(
                Arguments.of("Kleines Johanniswürmchen - Lebertransplantation -> GNROOT", kleinesJohanniswuermchenID, lebertransplantationID, kleinesjohanniswuermchenLebertransplantationExpected),
                Arguments.of("Kleines Johanniswürmchen - Nierentransplantation -> GNROOT", kleinesJohanniswuermchenID, nierentransplantationID, kleinesjohanniswuermchenNierentransplantationExpected),
                Arguments.of("Kleines Johanniswürmchen - Herztransplantation -> GNROOT", kleinesJohanniswuermchenID, herztransplantationID, kleinesjohanniswuermchenHerztransplantationExpected),
                Arguments.of("Kleines Johanniswürmchen - Fußreflexzonenmassage -> GNROOT", kleinesJohanniswuermchenID, fussreflexzonenmassage1ID, kleinesjohanniswuermchenFussreflexzonenmassageExpected)
        );
    }

    @Test
    void longestShortestNounTest() {
        // Kleines Johanniswürmchen - Lebertransplantation,
        // Kleines Johanniswürmchen - Nierentransplantation
        // Kleines Johanniswürmchen - Herztransplantation
        // Kleines Johanniswürmchen - Fußreflexzonenmassage (1)
        // Kleines Johanniswürmchen - Fußreflexzonenmassage (2)
        // Kleines Johanniswürmchen - Lungentransplantation
        Set<LeastCommonSubsumer> expected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesJohanniswuermchenID, lebertransplantationID), 35),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesJohanniswuermchenID, nierentransplantationID), 35),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesJohanniswuermchenID, herztransplantationID), 35),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesJohanniswuermchenID, fussreflexzonenmassage1ID), 35),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesJohanniswuermchenID, fussreflexzonenmassage2ID), 35),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(kleinesJohanniswuermchenID, lungentransplantationID), 35)
        );

        Set<LeastCommonSubsumer> actual = semanticUtils.getLongestLeastCommonSubsumers(WordCategory.nomen);

        assertEquals(expected, actual);
    }

    @Test
    void lcsNullTest() {

        Synset synset1 = gnet.getSynsetByID(laufenID); // laufen
        Synset synset2 = gnet.getSynsetByID(heilpflanzeID); // Heilpflanze

        // different word categories - should be null
        Set<LeastCommonSubsumer> actual = semanticUtils.getLeastCommonSubsumers(synset1, synset2);

        assertEquals(null, actual);
    }

    @Test
    void longestShortestVerbTest() {
        Set<LeastCommonSubsumer> expected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(verlautenID, hochschlagenID), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(verlautenID, einstuelpenID), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(verlautenID, bemehlenID), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(verlautenID, hochrutschenID), 28),

                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(anmusternID, einstuelpenID), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(anmusternID, bemehlenID), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(anmusternID, hochrutschenID), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(anmusternID, hochschlagenID), 28),

                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(aufkrempelnID, anmusternID), 28),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(aufkrempelnID, verlautenID), 28)
        );

        Set<LeastCommonSubsumer> actual = semanticUtils.getLongestLeastCommonSubsumers(WordCategory.verben);

        LOGGER.info("actual longestShortest LCS(s) for verb: {}", actual);

        assertEquals(expected, actual);
    }

    @Test
    void longestShortestAdjTest() {

        Set<LeastCommonSubsumer> expected = Sets.newHashSet(
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(rueckgebildetID, gotteslaesterlichID), 20),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(rueckgebildetID, blasphemischID), 20),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(regressivID, gotteslaesterlichID), 20),
                new LeastCommonSubsumer(GNROOT_ID, Sets.newHashSet(regressivID, blasphemischID), 20)
        );

        Set<LeastCommonSubsumer> actual = semanticUtils.getLongestLeastCommonSubsumers(WordCategory.adj);

        LOGGER.info("actual longestShortest LCS(s) for adj: {}", actual);

        assertEquals(expected, actual);
    }

    @Test
    void maxDepthNounTest() {

        List<Synset> synsets = gnet.getSynsets(WordCategory.nomen);
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

        List<Synset> synsets = gnet.getSynsets(WordCategory.verben);
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

        List<Synset> synsets = gnet.getSynsets(WordCategory.adj);
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

    @ParameterizedTest(name = "{0} {1} {2} DistanceBetweenSynsets")
    @MethodSource({"distanceBetweenSynsetsProvider"})
    void distanceBetweenSynsetsTest(Synset synset1, Synset synset2, Integer expected) {
        Integer actual = semanticUtils.getDistanceBetweenSynsets(synset1, synset2);
        assertEquals(expected, actual);
    }

    private static Stream<Arguments> distanceBetweenSynsetsProvider() {
        Synset chinarindenbaum = gnet.getSynsetByID(chinarindenbaumID);
        Synset alleebaum = gnet.getSynsetByID(alleebaumID);
        Synset apfelbaum = gnet.getSynsetByID(apfelbaumId);
        Synset giftpflanze = gnet.getSynsetByID(giftpflanzeId);
        Synset sollen = gnet.getSynsetByID(sollenID);
        Synset menge = gnet.getSynsetByID(mengeID);

        return Stream.of(
                Arguments.of(chinarindenbaum, alleebaum, 2),
                Arguments.of(chinarindenbaum, apfelbaum, 3),
                Arguments.of(apfelbaum, giftpflanze, 5),
                Arguments.of(sollen, menge, null),
                Arguments.of(sollen, null, null),
                Arguments.of(null, menge, null)
        );
    }

    @ParameterizedTest(name = "{0} {1} {2} PathBetweenSynsets")
    @MethodSource({"pathsBetweenSynsetsProvider"})
    void pathsBetweenSynsetsTest(Synset synset1, Synset synset2, Set<SynsetPath> expected) {
        Set<SynsetPath> actual = semanticUtils.getPathsBetweenSynsets(synset1, synset2);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> pathsBetweenSynsetsProvider() {
        Synset chinarindenbaum = gnet.getSynsetByID(chinarindenbaumID);
        Synset alleebaum = gnet.getSynsetByID(alleebaumID);
        Synset baum = gnet.getSynsetByID(baumID);
        Synset obstbaum = gnet.getSynsetByID(obstbaumID);
        Synset apfelbaum = gnet.getSynsetByID(apfelbaumId);
        Synset holzpflanze = gnet.getSynsetByID(holzpflanzeID);
        Synset pflanze = gnet.getSynsetByID(pflanzeID);
        Synset nutzpflanze = gnet.getSynsetByID(nutzpflanzeID);
        Synset kulturpflanze = gnet.getSynsetByID(kulturpflanzeID);
        Synset giftpflanze = gnet.getSynsetByID(giftpflanzeId);
        Synset sollen = gnet.getSynsetByID(sollenID);
        Synset menge = gnet.getSynsetByID(mengeID);

        // Chinarindenbaum - Alleebaum (Baum)
        Set<SynsetPath> expectedChinaAllee = new HashSet<>();
        List<Synset> pathChinaBaum = new ArrayList<>();
        pathChinaBaum.add(chinarindenbaum);
        pathChinaBaum.add(baum);
        List<Synset> pathAleeBaum = new ArrayList<>();
        pathAleeBaum.add(alleebaum);
        pathAleeBaum.add(baum);
        expectedChinaAllee.add(new SynsetPath(chinarindenbaum, alleebaum, baum.getId(), pathChinaBaum, pathAleeBaum));

        // Chinarindenbaum - Apfelbaum (Baum)
        Set<SynsetPath> expectedChinaAlpfel = new HashSet<>();
        List<Synset> pathApfelBaum = new ArrayList<>();
        pathApfelBaum.add(apfelbaum);
        pathApfelBaum.add(obstbaum);
        pathApfelBaum.add(baum);
        expectedChinaAlpfel.add(new SynsetPath(chinarindenbaum, apfelbaum, baum.getId(), pathChinaBaum, pathApfelBaum));

        // Apfelbaum - Giftpflanze (Pflanze) 2 lcs
        Set<SynsetPath> expectedAlpfelGift = new HashSet<>();
        List<Synset> pathApfelPflanze1 = new ArrayList<>();
        pathApfelPflanze1.add(apfelbaum);
        pathApfelPflanze1.add(obstbaum);
        pathApfelPflanze1.add(nutzpflanze);
        pathApfelPflanze1.add(kulturpflanze);
        pathApfelPflanze1.add(pflanze);
        List<Synset> pathGiftPflanze = new ArrayList<>();
        pathGiftPflanze.add(giftpflanze);
        pathGiftPflanze.add(pflanze);
        expectedAlpfelGift.add(new SynsetPath(apfelbaum, giftpflanze, pflanze.getId(), pathApfelPflanze1, pathGiftPflanze));

        List<Synset> pathApfelPflanze2 = new ArrayList<>();
        pathApfelPflanze2.add(apfelbaum);
        pathApfelPflanze2.add(obstbaum);
        pathApfelPflanze2.add(baum);
        pathApfelPflanze2.add(holzpflanze);
        pathApfelPflanze2.add(pflanze);
        expectedAlpfelGift.add(new SynsetPath(apfelbaum, giftpflanze, pflanze.getId(), pathApfelPflanze2, pathGiftPflanze));

        Set<SynsetPath> expectedBaumBaum = new HashSet<>();
        List<Synset> pathBaumBaum = new ArrayList<>();
        pathBaumBaum.add(baum);
        expectedBaumBaum.add(new SynsetPath(baum, baum, baum.getId(), pathBaumBaum, pathBaumBaum));

        return Stream.of(
                Arguments.of(chinarindenbaum, alleebaum, expectedChinaAllee),
                Arguments.of(chinarindenbaum, apfelbaum, expectedChinaAlpfel),
                Arguments.of(apfelbaum, giftpflanze, expectedAlpfelGift),
                Arguments.of(baum, baum, expectedBaumBaum),
                Arguments.of(sollen, menge, null),
                Arguments.of(sollen, null, null),
                Arguments.of(null, menge, null)
        );
    }

    @ParameterizedTest(name = "{0} {1} {2} {3}")
    @MethodSource({"simplePathProvider",
            "leacockChodorowProvider",
            "wuAndPalmerProvider",
            "resnikProvider",
            "jiangAndConrathProvider",
            "linProvider"})
    void similarityMeasuresTest(SemRelMeasure semRelMeasure, Integer sID1, Integer sID2, int normalizedMax, Double expected) {
        Synset synset1, synset2;

        synset1 = (sID1 == null) ? null : gnet.getSynsetByID(sID1);
        synset2 = (sID2 == null) ? null : gnet.getSynsetByID(sID2);

        double epsilon = 0.001; // tolerance for working with doubles
        Double actual = semanticUtils.getSimilarity(semRelMeasure, synset1, synset2, normalizedMax);

        if (sID1 == null || sID2 == null) {
            //LOGGER.info("{} {} {}, normMax: {}, exp: {}\n", semRelMeasure, sID1, sID2, normalizedMax, expected);
            assertNull(actual);
        } else {
            //LOGGER.info("{} {} {}, {} {}, normMax: {}, actual: {}", semRelMeasure, sID1, synset1.getAllOrthForms(), sID2, synset2.getAllOrthForms(), normalizedMax, actual);
            assertEquals(expected, actual, epsilon,
                    "\n" + sID1 + " " + synset1.getAllOrthForms() + "\n" + sID2 + " " + synset2.getAllOrthForms() + "\n" + normalizedMax);
        }
    }

    private static Stream<Arguments> simplePathProvider() {

        double bambusVeilchenRawExpected = 0.88571;
        double bambusVeilchen10Expected = 8.8571;
        double unsimilarExpected = 0.0;
        double identityRawExpected = 1.0;
        double identity10Expected = 10.0;
        double normalized10Expected = 8.5714;

        return Stream.of(
                Arguments.of(SemRelMeasure.SimplePath, bambusID, veilchenID, 0, bambusVeilchenRawExpected),
                Arguments.of(SemRelMeasure.SimplePath, kleinesJohanniswuermchenID, lebertransplantationID, 0, unsimilarExpected),

                Arguments.of(SemRelMeasure.SimplePath, bemehlenID, anmusternID, 0, unsimilarExpected),
                Arguments.of(SemRelMeasure.SimplePath, bemehlenID, bemehlenID, 0, identityRawExpected),

                Arguments.of(SemRelMeasure.SimplePath, blasphemischID, regressivID, 0, unsimilarExpected),
                Arguments.of(SemRelMeasure.SimplePath, blasphemischID, blasphemischID, 0, identityRawExpected),


                Arguments.of(SemRelMeasure.SimplePath, bambusID, veilchenID, 10, bambusVeilchen10Expected),
                Arguments.of(SemRelMeasure.SimplePath, kleinesJohanniswuermchenID, lebertransplantationID, 10, unsimilarExpected),

                Arguments.of(SemRelMeasure.SimplePath, bemehlenID, anmusternID, 10, unsimilarExpected),
                Arguments.of(SemRelMeasure.SimplePath, bemehlenID, bemehlenID, 10, identity10Expected),

                Arguments.of(SemRelMeasure.SimplePath, blasphemischID, regressivID, 10, unsimilarExpected),
                Arguments.of(SemRelMeasure.SimplePath, blasphemischID, blasphemischID, 10, identity10Expected),


                Arguments.of(SemRelMeasure.SimplePath, apfelbaumId, giftpflanzeId, 10, normalized10Expected),

                Arguments.of(SemRelMeasure.SimplePath, apfelbaumId, null, 10, null),
                Arguments.of(SemRelMeasure.SimplePath, null, giftpflanzeId, 10, null)
        );
    }

    private static Stream<Arguments> leacockChodorowProvider() {

        // Bambus - Veilchen, pathlength = 4
        double bambusVeilchenRawExpected = 0.92428;
        double bambusVeilchenNormalized10Expected = 5.50877;
        double identityExpectedNounRaw = 1.62325;
        double normalized10EdentityExpected = 10.0;

        // Kleines Johanniswürmchen - Lebertransplantation
        double kJohannisLebertransExpected = 0.066946;
        double kJohannisLebertransNorm10Expected = 0.0;

        // Apfelbaum - Giftpflanze
        double abaumGiftpRawExpected = 0.84509;
        double normalized10Expected = 5.0;

        // bemehlen - anmustern
        double identityExpectedVerbRaw = 1.50515;
        double bemAnmustRawExpected = 0.042752;
        double bemAnmustNorm10Expected = 0.0;

        // blasphemisch - regressiv
        double identityExpectedAdjRaw = 1.342423;
        double blasRegRawExpected = 0.020203;
        double blasRegNorm10Expected = 0.0;


        return Stream.of(
                Arguments.of(SemRelMeasure.LeacockAndChodorow, bambusID, veilchenID, 0, bambusVeilchenRawExpected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, bambusID, veilchenID, 10, bambusVeilchenNormalized10Expected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, bambusID, bambusID, 10, normalized10EdentityExpected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, veilchenID, veilchenID, 0, identityExpectedNounRaw),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, kleinesJohanniswuermchenID, kleinesJohanniswuermchenID, 10, normalized10EdentityExpected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, lebertransplantationID, lebertransplantationID, 0, identityExpectedNounRaw),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, kleinesJohanniswuermchenID, lebertransplantationID, 0, kJohannisLebertransExpected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, kleinesJohanniswuermchenID, lebertransplantationID, 10, kJohannisLebertransNorm10Expected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, bemehlenID, anmusternID, 0, bemAnmustRawExpected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, bemehlenID, anmusternID, 10, bemAnmustNorm10Expected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, bemehlenID, bemehlenID, 0, identityExpectedVerbRaw),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, blasphemischID, regressivID, 0, blasRegRawExpected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, blasphemischID, regressivID, 10, blasRegNorm10Expected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, blasphemischID, blasphemischID, 0, identityExpectedAdjRaw),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, blasphemischID, blasphemischID, 10, 10.0),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, apfelbaumId, giftpflanzeId, 0, abaumGiftpRawExpected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, apfelbaumId, giftpflanzeId, 10, normalized10Expected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, giftpflanzeId, apfelbaumId, 0, abaumGiftpRawExpected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, giftpflanzeId, apfelbaumId, 10, normalized10Expected),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, apfelbaumId, null, 10, null),
                Arguments.of(SemRelMeasure.LeacockAndChodorow, null, giftpflanzeId, 10, null)
        );
    }

    private static Stream<Arguments> wuAndPalmerProvider() {

        // Bambus - Veilchen, pathlength = 4
        double bambusVeilchenRawExpected = 0.75;
        double bambusVeilchenNormalized10Expected = 7.5;
        double identityExpectedRaw = 1.0;
        double normalized10EdentityExpected = 10.0;

        // Kleines Johanniswürmchen - Lebertransplantation
        double kJohannisLebertransExpected = 0.0;
        double kJohannisLebertransNorm10Expected = 0.0;

        // Apfelbaum - Giftpflanze
        double abaumGiftpRawExpected = 0.70588;
        double normalized10Expected = 7.0588;

        // bemehlen - anmustern
        double bemAnmustRawExpected = 0.0;
        double bemAnmustNorm10Expected = 0.0;

        // blasphemisch - regressiv
        double blasRegRawExpected = 0.0;
        double blasRegNorm10Expected = 0.0;


        return Stream.of(
                Arguments.of(SemRelMeasure.WuAndPalmer, bambusID, veilchenID, 0, bambusVeilchenRawExpected),
                Arguments.of(SemRelMeasure.WuAndPalmer, bambusID, veilchenID, 10, bambusVeilchenNormalized10Expected),
                Arguments.of(SemRelMeasure.WuAndPalmer, bambusID, bambusID, 10, normalized10EdentityExpected),
                Arguments.of(SemRelMeasure.WuAndPalmer, veilchenID, veilchenID, 0, identityExpectedRaw),
                Arguments.of(SemRelMeasure.WuAndPalmer, kleinesJohanniswuermchenID, kleinesJohanniswuermchenID, 10, normalized10EdentityExpected),
                Arguments.of(SemRelMeasure.WuAndPalmer, lebertransplantationID, lebertransplantationID, 0, identityExpectedRaw),
                Arguments.of(SemRelMeasure.WuAndPalmer, kleinesJohanniswuermchenID, lebertransplantationID, 0, kJohannisLebertransExpected),
                Arguments.of(SemRelMeasure.WuAndPalmer, kleinesJohanniswuermchenID, lebertransplantationID, 10, kJohannisLebertransNorm10Expected),
                Arguments.of(SemRelMeasure.WuAndPalmer, bemehlenID, anmusternID, 0, bemAnmustRawExpected),
                Arguments.of(SemRelMeasure.WuAndPalmer, bemehlenID, anmusternID, 10, bemAnmustNorm10Expected),
                Arguments.of(SemRelMeasure.WuAndPalmer, bemehlenID, bemehlenID, 0, identityExpectedRaw),
                Arguments.of(SemRelMeasure.WuAndPalmer, blasphemischID, regressivID, 0, blasRegRawExpected),
                Arguments.of(SemRelMeasure.WuAndPalmer, blasphemischID, regressivID, 10, blasRegNorm10Expected),
                Arguments.of(SemRelMeasure.WuAndPalmer, blasphemischID, blasphemischID, 0, identityExpectedRaw),
                Arguments.of(SemRelMeasure.WuAndPalmer, blasphemischID, blasphemischID, 10, normalized10EdentityExpected),
                Arguments.of(SemRelMeasure.WuAndPalmer, apfelbaumId, giftpflanzeId, 0, abaumGiftpRawExpected),
                Arguments.of(SemRelMeasure.WuAndPalmer, apfelbaumId, giftpflanzeId, 10, normalized10Expected),
                Arguments.of(SemRelMeasure.WuAndPalmer, apfelbaumId, null, 10, null),
                Arguments.of(SemRelMeasure.WuAndPalmer, null, giftpflanzeId, 10, null)
        );
    }

    private static Stream<Arguments> resnikProvider() {

        return Stream.of(
                Arguments.of(SemRelMeasure.Resnik, bambusID, veilchenID, 0, 2.56571),
                Arguments.of(SemRelMeasure.Resnik, bambusID, veilchenID, 10, 2.6043),
                Arguments.of(SemRelMeasure.Resnik, bambusID, bambusID, 10, 5.51221),
                Arguments.of(SemRelMeasure.Resnik, veilchenID, veilchenID, 0, 5.1826),
                Arguments.of(SemRelMeasure.Resnik, kleinesJohanniswuermchenID, kleinesJohanniswuermchenID, 10, 10.0),
                Arguments.of(SemRelMeasure.Resnik, lebertransplantationID, lebertransplantationID, 0, 6.3656),
                Arguments.of(SemRelMeasure.Resnik, kleinesJohanniswuermchenID, lebertransplantationID, 0, 0.0),
                Arguments.of(SemRelMeasure.Resnik, kleinesJohanniswuermchenID, lebertransplantationID, 10, 0.0),
                Arguments.of(SemRelMeasure.Resnik, bemehlenID, anmusternID, 0, 0.0),
                Arguments.of(SemRelMeasure.Resnik, bemehlenID, anmusternID, 10, 0.0),
                Arguments.of(SemRelMeasure.Resnik, bemehlenID, bemehlenID, 0, 7.51298),
                Arguments.of(SemRelMeasure.Resnik, blasphemischID, regressivID, 0, 0.0),
                Arguments.of(SemRelMeasure.Resnik, blasphemischID, regressivID, 10, 0.0),
                Arguments.of(SemRelMeasure.Resnik, blasphemischID, blasphemischID, 0, 6.03333),
                Arguments.of(SemRelMeasure.Resnik, blasphemischID, blasphemischID, 10, 6.47042),
                Arguments.of(SemRelMeasure.Resnik, apfelbaumId, giftpflanzeId, 0, 2.56571),
                Arguments.of(SemRelMeasure.Resnik, apfelbaumId, giftpflanzeId, 10, 2.60431),
                Arguments.of(SemRelMeasure.Resnik, giftpflanzeId, apfelbaumId, 0, 2.56571),
                Arguments.of(SemRelMeasure.Resnik, giftpflanzeId, apfelbaumId, 10, 2.60431),
                Arguments.of(SemRelMeasure.Resnik, riechenID, schmeckenID, 0, 1.35137),
                Arguments.of(SemRelMeasure.Resnik, laufenID, springenID, 10, 1.53634),
                Arguments.of(SemRelMeasure.Resnik, apfelbaumId, null, 10, null),
                Arguments.of(SemRelMeasure.Resnik, null, giftpflanzeId, 10, null)
        );
    }

    private static Stream<Arguments> jiangAndConrathProvider() {

        double identityExpectedNounRaw = 19.70579; //19.70359;
        double identityExpectedAdjRaw = 18.64896;
        double normalized10IdentityExpected = 10.0;

        return  Stream.of(
                Arguments.of(SemRelMeasure.JiangAndConrath, bambusID, veilchenID, 0, 14.22290),
                Arguments.of(SemRelMeasure.JiangAndConrath, bambusID, veilchenID, 10, 7.21843),
                Arguments.of(SemRelMeasure.JiangAndConrath, bambusID, bambusID, 10, normalized10IdentityExpected),
                Arguments.of(SemRelMeasure.JiangAndConrath, veilchenID, veilchenID, 0, identityExpectedNounRaw),
                Arguments.of(SemRelMeasure.JiangAndConrath, kleinesJohanniswuermchenID, kleinesJohanniswuermchenID, 10, normalized10IdentityExpected),
                Arguments.of(SemRelMeasure.JiangAndConrath, lebertransplantationID, lebertransplantationID, 0, identityExpectedNounRaw),
                Arguments.of(SemRelMeasure.JiangAndConrath, kleinesJohanniswuermchenID, lebertransplantationID, 0, 3.48727),
                Arguments.of(SemRelMeasure.JiangAndConrath, kleinesJohanniswuermchenID, lebertransplantationID, 10, 1.76978),
                Arguments.of(SemRelMeasure.JiangAndConrath, bemehlenID, anmusternID, 0, 4.37298),
                Arguments.of(SemRelMeasure.JiangAndConrath, bemehlenID, anmusternID, 10, 2.19490),
                Arguments.of(SemRelMeasure.JiangAndConrath, bemehlenID, bemehlenID, 0, 19.92338),
                Arguments.of(SemRelMeasure.JiangAndConrath, blasphemischID, regressivID, 0, 6.78404),
                Arguments.of(SemRelMeasure.JiangAndConrath, blasphemischID, regressivID, 10, 3.63776),
                Arguments.of(SemRelMeasure.JiangAndConrath, blasphemischID, blasphemischID, 0, identityExpectedAdjRaw),
                Arguments.of(SemRelMeasure.JiangAndConrath, blasphemischID, blasphemischID, 10, normalized10IdentityExpected),
                Arguments.of(SemRelMeasure.JiangAndConrath, apfelbaumId, giftpflanzeId, 0, 14.95344),
                Arguments.of(SemRelMeasure.JiangAndConrath, apfelbaumId, giftpflanzeId, 10, 7.58818),
                Arguments.of(SemRelMeasure.JiangAndConrath, giftpflanzeId, apfelbaumId, 0, 14.95344),
                Arguments.of(SemRelMeasure.JiangAndConrath, giftpflanzeId, apfelbaumId, 10, 7.58818),
                Arguments.of(SemRelMeasure.JiangAndConrath, riechenID, schmeckenID, 0, 14.7336),
                Arguments.of(SemRelMeasure.JiangAndConrath, laufenID, springenID, 10, 7.74654),
                Arguments.of(SemRelMeasure.JiangAndConrath, apfelbaumId, null, 10, null),
                Arguments.of(SemRelMeasure.JiangAndConrath, null, giftpflanzeId, 10, null)
        );
    }

    private static Stream<Arguments> linProvider() {

        double identityExpectedAllRaw = 1.0;
        double normalized10IdentityExpected = 10.0;

        return Stream.of(
                Arguments.of(SemRelMeasure.Lin, bambusID, veilchenID, 0, 0.48358),
                Arguments.of(SemRelMeasure.Lin, bambusID, veilchenID, 10, 4.8341),
                Arguments.of(SemRelMeasure.Lin, bambusID, bambusID, 10, normalized10IdentityExpected),
                Arguments.of(SemRelMeasure.Lin, veilchenID, veilchenID, 0, identityExpectedAllRaw),
                Arguments.of(SemRelMeasure.Lin, kleinesJohanniswuermchenID, kleinesJohanniswuermchenID, 10, normalized10IdentityExpected),
                Arguments.of(SemRelMeasure.Lin, lebertransplantationID, lebertransplantationID, 0, identityExpectedAllRaw),
                Arguments.of(SemRelMeasure.Lin, kleinesJohanniswuermchenID, lebertransplantationID, 0, 0.0),
                Arguments.of(SemRelMeasure.Lin, kleinesJohanniswuermchenID, lebertransplantationID, 10, 0.0),
                Arguments.of(SemRelMeasure.Lin, bemehlenID, anmusternID, 0, 0.0),
                Arguments.of(SemRelMeasure.Lin, bemehlenID, anmusternID, 10, 0.0),
                Arguments.of(SemRelMeasure.Lin, bemehlenID, bemehlenID, 0, identityExpectedAllRaw),
                Arguments.of(SemRelMeasure.Lin, blasphemischID, regressivID, 0, 0.0),
                Arguments.of(SemRelMeasure.Lin, blasphemischID, regressivID, 10, 0.0),
                Arguments.of(SemRelMeasure.Lin, blasphemischID, blasphemischID, 0, identityExpectedAllRaw),
                Arguments.of(SemRelMeasure.Lin, blasphemischID, blasphemischID, 10, 10.0),
                Arguments.of(SemRelMeasure.Lin, riechenID, schmeckenID, 0, 0.34244),
                Arguments.of(SemRelMeasure.Lin, laufenID, springenID, 10, 4.05390),
                Arguments.of(SemRelMeasure.Lin, apfelbaumId, giftpflanzeId, 0, 0.51933),
                Arguments.of(SemRelMeasure.Lin, apfelbaumId, giftpflanzeId, 10, 5.1914),
                Arguments.of(SemRelMeasure.Lin, giftpflanzeId, apfelbaumId, 0, 0.51933),
                Arguments.of(SemRelMeasure.Lin, giftpflanzeId, apfelbaumId, 10, 5.1914),
                Arguments.of(SemRelMeasure.Lin, apfelbaumId, null, 10, null),
                Arguments.of(SemRelMeasure.Lin, null, giftpflanzeId, 10, null)
        );
    }



    // Find all synsets whose wordCategory does not match that of all its direct hypernyms.
    // This should never happen.
    @Test
    void catDoesntMatchHyperTest() {
        List<Synset> synsetList = gnet.getSynsets();

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
        List<Synset> synsetList = gnet.getSynsets();

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

    @Test
        // Find synsets that have the same orthForm multiple times
    void duplicateOrthFormsTest() {
        List<String> allOrthFormsSet;
        List<String> allOrthFormsList;
        List<Synset> allSynsets = gnet.getSynsets();

        for (Synset synset : allSynsets) {
            allOrthFormsSet = synset.getAllOrthForms(); // uses a Set
            allOrthFormsList = new ArrayList<>();
            for (LexUnit lexUnit : synset.getLexUnits()) {
                allOrthFormsList.addAll(lexUnit.getOrthForms()); // uses a List
            }
            if (allOrthFormsSet.size() != allOrthFormsList.size()) {
                Collections.sort(allOrthFormsList);
                Collections.sort(allOrthFormsSet);
                LOGGER.warn("Synset {} \tcontains duplicate orthForms\n\t{}\n\t{}", synset.getId(), allOrthFormsSet, allOrthFormsList);
            }
        }
    }

    //@Test
    // Find all verb synsets that have exactly 1 hypernym,
    // and whose wordClass does not match that of its hypernym.
    // For informational purposes only, since this can sometimes happen.
    void wordClassOneHyperDoesntMatchVerbTest() {
        List<Synset> synsetList = gnet.getSynsets();

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
        List<Synset> synsetList = gnet.getSynsets();

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
        List<Synset> synsetList = gnet.getSynsets();

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
        List<Synset> synsetList = gnet.getSynsets();

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
        List<Synset> synsetList = gnet.getSynsets();

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

    //@Test
    // Find synsets with multiple LCSs and path lengths between 2 and 5
    // For information only
    void multLCSTest() {
        for (WordCategory wordCategory : WordCategory.values()) {
            LOGGER.info("\n\t{}\n", wordCategory);
            int minDist = 2;
            int maxDist = 5;
            List<Synset> synsets = gnet.getSynsets(wordCategory);
            int i=0;
            int j=synsets.size()-1;
            for (; i < j; i++) {
                for (; j > i; j--) {
                    Synset s1 = synsets.get(i);
                    Synset s2 = synsets.get(j);
                    Set<LeastCommonSubsumer> leastCommonSubsumers = semanticUtils.getLeastCommonSubsumers(s1, s2);

                    if (leastCommonSubsumers.size() > 1) {
                        int dist = leastCommonSubsumers.iterator().next().getDistance();
                        if (dist >= minDist && dist <= maxDist) {
                            System.out.println(leastCommonSubsumers + "\n");
                        }
                    }
                }
            }
        }
    }

    //@Test
    // Find all synsets that are of a different WordCategory as their hypernyms
    // For informational purposes only.
    void wordCatMismatchTest() {
        List<Synset> synsetList = gnet.getSynsets();

        int cnt = 0;
        for (Synset synset : synsetList) {
            List<Synset> hypernyms = synset.getRelatedSynsets(ConRel.has_hypernym);
            if (!hypernyms.isEmpty()) {
                for (Synset hyper : hypernyms) {
                    if (!hyper.inWordCategory(synset.getWordCategory())) {
                        LOGGER.info("WordCategory mismatch: {} {} to hypernym {} {}", synset.getId(), synset.getAllOrthForms(), hyper.getId(), hyper.getAllOrthForms());
                        cnt++;
                    }
                }
            }
        }
        LOGGER.info("{} instances of WordCategory mismatches.", cnt);
    }
}

