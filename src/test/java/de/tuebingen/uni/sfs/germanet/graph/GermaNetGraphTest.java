package de.tuebingen.uni.sfs.germanet.graph;

import de.tuebingen.uni.sfs.germanet.api.*;
import de.tuebingen.uni.sfs.germanet.semantic.GermaNetGraphUtils;
import de.tuebingen.uni.sfs.germanet.semantic.LeastCommonSubsumer;
import guru.nidi.graphviz.engine.Format;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static de.tuebingen.uni.sfs.germanet.semantic.Semantic.GNROOT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GermaNetGraphTest {
    static GermaNet gnetCaseSensitive;
    static String dataPath;
    static GermaNetGraph gnetGraph;
    static GermaNetGraphUtils gnetGraphUtils;

    private static final Logger LOGGER = LoggerFactory.getLogger(GermaNetGraphTest.class);

    @BeforeAll
    static void setUp() {
        try {
            String userHome = System.getProperty("user.home");
            String sep = System.getProperty("file.separator");
            dataPath = userHome + sep + "Data" + sep;
            String goodDataPath = dataPath + "GN-XML-ForApiUnitTesting/";

            gnetCaseSensitive = new GermaNet(goodDataPath, false);
            //gnetGraph = new GermaNetGraph(gnetCaseSensitive);
            gnetGraphUtils = new GermaNetGraphUtils(gnetCaseSensitive);
            LOGGER.info("Done loading graph.");
        } catch (IOException ex) {
            LOGGER.error("\nGermaNet data not found at <homeDirectory>/Data/GN-XML-ForApiUnitTesting/\nAborting...", ex);
            System.exit(0);
        } catch (XMLStreamException ex) {
            LOGGER.error("\nUnable to load GermaNet data at <homeDirectory>/Data/GN-XML-ForApiUnitTesting/\nAborting...", ex);
            System.exit(0);
        }
    }

    @Test
    void nodeCountTest() {
        // Release 14
        int numNodes = 136263 + 175000;
        assertEquals(numNodes, gnetGraph.getFullGraph().vertexSet().size());
    }

    /*
    @Test
    void edgeCountTest1() {
        // Release 14, lexical relations
        int numEdges = 12203;
        assertEquals(numEdges, gnetGraph.getLexunitGraph().edgeSet().size());
    }

    @Test
    void edgeCountTest2() {
        // Release 14, conceptual relations
        int numEdges = 150003;
        assertEquals(numEdges, gnetGraph.getSynsetGraph().edgeSet().size());
    }
    */

    @Test
    void edgeTypeTest1() {
        // Release 14, lexical relations
        for (GermaNetEdge edge : gnetGraph.getLexunitGraph().edgeSet()) {
            GraphRelation rel = edge.getRelation();
            if (!(rel instanceof LexRel)) {
                fail("non LexRel relations in lexunit graph");
            }
        }
    }

    @Test
    void edgeTypeTest2() {
        // Release 14, conceptual relations
        for (GermaNetEdge edge : gnetGraph.getSynsetGraph().edgeSet()) {
            GraphRelation rel = edge.getRelation();
            if (!(rel instanceof ConRel)) {
                fail("non ConRel relations in synset graph");
            }
        }
    }

    @Test
    void shortestPathDiejkstra() {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(39494); // Apfel
        Synset synset2 = gnetCaseSensitive.getSynsetByID(46042); // Baum

        List<Integer> expected = new ArrayList<>(Arrays.asList(39494, 39491, 39490, 39059, 39035, 50981, 50982, 50986, 50990, 44960, 48041, 46042));
        List<Integer> actual = new ArrayList<>();

        GraphPath<GermaNetVertex, GermaNetEdge> shortestPath = gnetGraph.shortestPath(gnetGraph.getHyponymGraph(), synset1, synset2);
        List<GermaNetVertex> pathNodes = shortestPath.getVertexList();
        for (GermaNetVertex vertex : pathNodes) {
            actual.add(vertex.getId());
        }

        assertEquals(expected, actual);
    }

    @Test
    void shortestPath2() {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(50869); // Pferd - animal
        Synset synset2 = gnetCaseSensitive.getSynsetByID(11106); // Pferd - chess

        List<Integer> expected = new ArrayList<>(Arrays.asList(39494, 39491, 39490, 39059, 39035, 50981, 50982, 50986, 50990, 44960, 48041, 46042));
        List<Integer> actual = new ArrayList<>();

        GraphPath<GermaNetVertex, GermaNetEdge> shortestPath = gnetGraph.shortestPath(gnetGraph.getHyponymGraph(), synset1, synset2);
        List<GermaNetVertex> pathNodes = shortestPath.getVertexList();
        for (GermaNetVertex vertex : pathNodes) {
            actual.add(vertex.getId());
        }

        assertEquals(expected, actual);
    }

    //@Test
    void vizTest() {
        Synset synset = gnetCaseSensitive.getSynsetByID(48523); // Naturheilmittel
        try {
            GraphVizUtils.getHyperHypoImage(synset, 5, 4, Format.SVG,
                    new File("/Users/meh/tmp/germaNet/images/NaturheilmittelDot.svg"));
            GraphVizUtils.getHyperHypoImage(synset, 3, 4, Format.PNG,
                    new File("/Users/meh/tmp/germaNet/images/NaturheilmittelDot.png"));
        } catch (IOException e) {
            fail("graph visualization failed.", e);
        }
    }

    @Test
    void lcsApfelBirneTest() {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(39494); // Apfel
        Synset synset2 = gnetCaseSensitive.getSynsetByID(39495); // Birne
        Synset expectedSynset = gnetCaseSensitive.getSynsetByID(39491); //Kernobst

        List<Integer> synset1PathToLcsIDs  = new ArrayList<>(Arrays.asList(39494, 39491));
        List<Synset> synset1PathToLcs  = new ArrayList<>();
        for (Integer id : synset1PathToLcsIDs) {
            synset1PathToLcs.add(gnetCaseSensitive.getSynsetByID(id));
        }
        List<Integer> synset2PathToLcsIDs  = new ArrayList<>(Arrays.asList(39495, 39491));
        List<Synset> synset2PathToLcs  = new ArrayList<>();
        for (Integer id : synset2PathToLcsIDs) {
            synset2PathToLcs.add(gnetCaseSensitive.getSynsetByID(id));
        }
        LeastCommonSubsumer expectedLCS = new LeastCommonSubsumer(expectedSynset, synset1PathToLcs, synset2PathToLcs, 2);
        Set<LeastCommonSubsumer> expected = new HashSet<>();
        expected.add(expectedLCS);

        Set<LeastCommonSubsumer> actual = gnetGraphUtils.leastCommonSubsumerOrig(synset1, synset2);

        LOGGER.info("actual: {}", actual);
        LOGGER.info("expected: {}", expected);
        assertEquals(expected, actual);
    }

    @Test
    void lcsApfelBaumTest() {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(39494); // Apfel
        Synset synset2 = gnetCaseSensitive.getSynsetByID(46042); // Baum
        Synset expectedSynset = gnetCaseSensitive.getSynsetByID(50981);

        List<Integer> synset1PathToLcsIDs  = new ArrayList<>(Arrays.asList(39494, 39491, 39490, 39059, 39035, 50981));
        List<Synset> synset1PathToLcs  = new ArrayList<>();
        for (Integer id : synset1PathToLcsIDs) {
            synset1PathToLcs.add(gnetCaseSensitive.getSynsetByID(id));
        }
        List<Integer> synset2PathToLcsIDs  = new ArrayList<>(Arrays.asList(46042, 46041, 44960, 50990, 50986, 50982, 50981));
        List<Synset> synset2PathToLcs  = new ArrayList<>();
        for (Integer id : synset2PathToLcsIDs) {
            synset2PathToLcs.add(gnetCaseSensitive.getSynsetByID(id));
        }
        LeastCommonSubsumer expectedLCS = new LeastCommonSubsumer(expectedSynset, synset1PathToLcs, synset2PathToLcs, 11);
        Set<LeastCommonSubsumer> expected = new HashSet<>();
        expected.add(expectedLCS);

        Set<LeastCommonSubsumer> actual = gnetGraphUtils.leastCommonSubsumerOrig(synset1, synset2);

        LOGGER.info("actual: {}", actual);
        LOGGER.info("expected: {}", expected);
        assertEquals(expected, actual);
    }



    /*
    @Test
    void lcsTest1() {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(39494); // Apfel
        Synset synset2 = gnetCaseSensitive.getSynsetByID(46042); // Baum

        GermaNetVertex expected = gnetCaseSensitive.getSynsetByID(50981);
        GermaNetVertex actual = gnetGraph.lcs(gnetGraph.getHyponymGraph(), synset1, synset2);

        assertEquals(expected, actual);
    }

    @Test
    void lcsTest2() {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(46665); // Chinarindenbaum
        Synset synset2 = gnetCaseSensitive.getSynsetByID(7922); // Kompresse

        GermaNetVertex expected = gnetCaseSensitive.getSynsetByID(7917);

        GermaNetVertex actual = gnetGraph.leastCommonSubsumer(synset1, synset2);

        assertEquals(expected, actual);
    }

    @Test
    void lcsTest3() {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(46657); // Kraut
        Synset synset2 = gnetCaseSensitive.getSynsetByID(46659); // Heilpflanze

        GermaNetVertex expected = gnetCaseSensitive.getSynsetByID(46657);
        GermaNetVertex actual = gnetGraph.lcs(gnetGraph.getHyponymGraph(), synset1, synset2);

        assertEquals(expected, actual);
    }

    @Test
    void lcsTest4() {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(57835); // laufen
        Synset synset2 = gnetCaseSensitive.getSynsetByID(46659); // Heilpflanze

        GermaNetVertex expected = gnetCaseSensitive.getSynsetByID(GNROOT_ID);
        GermaNetVertex actual = gnetGraph.lcs(gnetGraph.getHyponymGraph(), synset1, synset2);

        assertEquals(expected, actual);
    }
    */

    /*
    // ToDo: this is not a real test
    @Test
    void lcsNounTest() {
        List<Synset> synsetList = gnetCaseSensitive.getSynsets();
        List<LeastCommonSubsumer> lcsList;

        // get a list of ~200 nouns to get the LCS's of
        List<Synset> someNouns = new ArrayList<>();
        List<Synset> allNouns = gnetCaseSensitive.getSynsets(WordCategory.nomen);

        for (int i=0; i < allNouns.size(); i+=allNouns.size()/200) {
            someNouns.add(allNouns.get(i));
        }

        // get the lcs of a synset from the beginning of the list
        // and a synset from the end of the list,
        // moving both ends to the middle of the list
        Synset synset1, synset2;
        int i,j = 0;
        for (i=0,j=someNouns.size()-1; i < someNouns.size()/2; i++,j--) {
            synset1 = someNouns.get(i);
            synset2 = someNouns.get(j);

            //LOGGER.info("Processing synsets {} and {}.", synset1.getId(), synset2.getId());
            lcsList = gnetGraph.leastCommonSubsumer(synset1, synset2);

            if (lcsList.size() == 1) {
                LOGGER.info("LCS: {}", lcsList);
            } else if (lcsList.size() > 1) {
                LOGGER.info("Multiple LCS found:\n {}", lcsList);
            } else if (lcsList.isEmpty()) {
                LOGGER.info("No LCS found: synset1: {}\tsynset2: {}", synset1, synset2);
            }
        }
    }
     */

    /*
    // ToDo: this is not a test
    @Test
    void longestShortestNounTest() {

        LeastCommonSubsumer longestShortest = null;
        List<Synset> synsetList = gnetCaseSensitive.getSynsets(WordCategory.nomen);
        Synset gnRoot = gnetCaseSensitive.getSynsetByID(GNROOT_ID);
        Map<Synset, Integer> pathLengthToRoot = new HashMap<>();
        List<List<Synset>> pathsToRoot;

        // get all sysnets with a path to root > 20
        List<List<Synset>> longPaths = new ArrayList<>();
        int longPath = 12;
        List<Synset> longestPath = new ArrayList<>();
        for (Synset synset : synsetList) {
            pathsToRoot = gnetGraph.getAllPaths(synset, gnRoot);
            for (List<Synset> path : pathsToRoot) {
                int pathLength = path.size();
                if (pathLength > longPath) {
                    longPaths.add(path);
                }
                if (pathLength > longestPath.size()) {
                    longestPath = path;
                }
            }
        }

        LOGGER.info("{} long paths found for nouns.", longPaths.size());
        LOGGER.info("Longest path to root: {}", longestPath);

        // get the LCS between all pairs
        Synset synset1, synset2;
        List<LeastCommonSubsumer> lcsList;
        int longestShortestLength = 0;
        int multLCScnt = 0;
        int noLCScnt = 0;
        for (int i=0; i < longPaths.size(); i++) {
            // the first node on the path is the start node
            synset1 = longPaths.get(i).get(0);
            for (int j = i + 1; j < longPaths.size(); j++) {
                // the first node on the path is the start node
                synset2 = longPaths.get(j).get(0);


                    lcsList = gnetGraph.leastCommonSubsumer(synset1, synset2);

                    if (lcsList.isEmpty()) {
                        noLCScnt++;
                        //LOGGER.info("No LCS found: synset1: {}\tsynset2: {}", synset1, synset2);
                    } else if (lcsList.size() > 1) {
                        multLCScnt++;
                       // LOGGER.info("Multiple LCS found: synset1: {}\tsynset2: {}", synset1, synset2);
                    }

                    for (LeastCommonSubsumer lcsLocal : lcsList) {
                        if (lcsLocal.distance > longestShortestLength) {
                            longestShortestLength = lcsLocal.distance;
                            longestShortest = lcsLocal;
                        }
                    }
            }
        }
        LOGGER.info("longestShortest LCS for nouns: {}. \nmultLCScnt: {} noLCScnt: {}", longestShortest, multLCScnt, noLCScnt);
        assertEquals(35, longestShortestLength);
    }

    // ToDo: this is not a test
    @Test
    void longestShortestVerbTest() {

        LeastCommonSubsumer longestShortest = null;
        List<Synset> synsetList = gnetCaseSensitive.getSynsets(WordCategory.verben);
        Synset gnRoot = gnetCaseSensitive.getSynsetByID(GNROOT_ID);
        Map<Synset, Integer> pathLengthToRoot = new HashMap<>();
        List<List<Synset>> pathsToRoot;

        // get all sysnets with a path to root > 9
        List<List<Synset>> longPaths = new ArrayList<>();
        int longPath = 9;
        List<Synset> longestPath = new ArrayList<>();
        for (Synset synset : synsetList) {
            pathsToRoot = gnetGraph.getAllPaths(synset, gnRoot);
            for (List<Synset> path : pathsToRoot) {
                int pathLength = path.size();
                if (pathLength > longPath) {
                    longPaths.add(path);
                }
                if (pathLength > longestPath.size()) {
                    longestPath = path;
                }
            }
        }

        LOGGER.info("{} long paths found for verbs.", longPaths.size());
        LOGGER.info("Longest path to root: {}", longestPath);

        // get the LCS between all pairs
        Synset synset1, synset2;
        List<LeastCommonSubsumer> lcsList;
        int longestShortestLength = 0;
        int multLCScnt = 0;
        int noLCScnt = 0;
        for (int i=0; i < longPaths.size(); i++) {
            // the first node on the path is the start node
            synset1 = longPaths.get(i).get(0);
            for (int j = i + 1; j < longPaths.size(); j++) {
                // the first node on the path is the start node
                synset2 = longPaths.get(j).get(0);


                lcsList = gnetGraph.leastCommonSubsumer(synset1, synset2);

                if (lcsList.isEmpty()) {
                    noLCScnt++;
                    //LOGGER.info("No LCS found: synset1: {}\tsynset2: {}", synset1, synset2);
                } else if (lcsList.size() > 1) {
                    multLCScnt++;
                    // LOGGER.info("Multiple LCS found: synset1: {}\tsynset2: {}", synset1, synset2);
                }

                for (LeastCommonSubsumer lcsLocal : lcsList) {
                    if (lcsLocal.distance > longestShortestLength) {
                        longestShortestLength = lcsLocal.distance;
                        longestShortest = lcsLocal;
                    }
                }
            }
        }
        LOGGER.info("longestShortest LCS for verbs: {}. \nmultLCScnt: {} noLCScnt: {}", longestShortest, multLCScnt, noLCScnt);
        assertEquals(35, longestShortestLength);
    }

    // ToDo: this is not a test
    @Test
    void longestShortestAdjTest() {

        LeastCommonSubsumer longestShortest = null;
        List<Synset> synsetList = gnetCaseSensitive.getSynsets(WordCategory.adj);
        Synset gnRoot = gnetCaseSensitive.getSynsetByID(GNROOT_ID);
        Map<Synset, Integer> pathLengthToRoot = new HashMap<>();
        List<List<Synset>> pathsToRoot;

        // get all sysnets with a path to root > 7
        List<List<Synset>> longPaths = new ArrayList<>();
        int longPath = 7;
        List<Synset> longestPath = new ArrayList<>();
        for (Synset synset : synsetList) {
            pathsToRoot = gnetGraph.getAllPaths(synset, gnRoot);
            for (List<Synset> path : pathsToRoot) {
                int pathLength = path.size();
                if (pathLength > longPath) {
                    longPaths.add(path);
                }
                if (pathLength > longestPath.size()) {
                    longestPath = path;
                }
            }
        }

        LOGGER.info("{} long paths found for adj.", longPaths.size());
        LOGGER.info("Longest path to root: {}", longestPath);

        // get the LCS between all pairs
        Synset synset1, synset2;
        List<LeastCommonSubsumer> lcsList;
        int longestShortestLength = 0;
        int multLCScnt = 0;
        int noLCScnt = 0;
        for (int i=0; i < longPaths.size(); i++) {
            // the first node on the path is the start node
            synset1 = longPaths.get(i).get(0);
            for (int j = i + 1; j < longPaths.size(); j++) {
                // the first node on the path is the start node
                synset2 = longPaths.get(j).get(0);


                lcsList = gnetGraph.leastCommonSubsumer(synset1, synset2);

                if (lcsList.isEmpty()) {
                    noLCScnt++;
                    //LOGGER.info("No LCS found: synset1: {}\tsynset2: {}", synset1, synset2);
                } else if (lcsList.size() > 1) {
                    multLCScnt++;
                    // LOGGER.info("Multiple LCS found: synset1: {}\tsynset2: {}", synset1, synset2);
                }

                for (LeastCommonSubsumer lcsLocal : lcsList) {
                    if (lcsLocal.distance > longestShortestLength) {
                        longestShortestLength = lcsLocal.distance;
                        longestShortest = lcsLocal;
                    }
                }
            }
        }
        LOGGER.info("longestShortest LCS for adj: {}.\nmultLCScnt: {} noLCScnt: {}", longestShortest, multLCScnt, noLCScnt);
        assertEquals(35, longestShortestLength);
    }
    */

    @Test
    void longestShortestNounTest() {
        // 34
        Set<LeastCommonSubsumer> longestShortest = gnetGraphUtils.longestLeastCommonSubsumer(WordCategory.nomen);

        LOGGER.info("longestShortest LCS(s) for noun: {}", longestShortest);
        assertEquals(22, longestShortest.iterator().next().distance);
    }

    @Test
    void longestShortestVerbTest() {
         // 20
        Set<LeastCommonSubsumer> longestShortest = gnetGraphUtils.longestLeastCommonSubsumer(WordCategory.verben);

        LOGGER.info("longestShortest LCS(s) for verb: {}", longestShortest);

        assertEquals(32, longestShortest.iterator().next().distance);
    }

    @Test
    void allPathsToRootTimerTest() {
        GermaNetGraphUtils utils = new GermaNetGraphUtils(gnetCaseSensitive);
    }

    // ToDo: create lcsSet with expected lcs objects
    @Test
    void longestShortestAdjTest() {
        Set<LeastCommonSubsumer> longestShortest = gnetGraphUtils.longestLeastCommonSubsumer(WordCategory.adj);

        LOGGER.info("longestShortest LCS(s) for adj: {}", longestShortest);

        assertEquals(4, longestShortest.size());
        assertEquals(20, longestShortest.iterator().next().distance);
    }

    @Test
    void lcsAdjTest() {
        Synset synset1 = gnetCaseSensitive.getSynsetByID(94411); // regressiv
        Synset synset2 = gnetCaseSensitive.getSynsetByID(94543); // denunziatorisch

        Set<Synset> expected = new HashSet<>();
        expected.add(gnetCaseSensitive.getSynsetByID(0)); // klassenübergreifend
        expected.add(gnetCaseSensitive.getSynsetByID(GNROOT_ID)); // root

        Set<LeastCommonSubsumer> actual = gnetGraphUtils.leastCommonSubsumer(synset1, synset2);
        assertEquals(expected, actual);
    }

    // ToDo: this takes too long - optimize it by finding paths to root
    //@Test
    void longestShortestInefficientTest() {
        List<Synset> synsetList = gnetCaseSensitive.getSynsets();
        Set<LeastCommonSubsumer> lcsList;
        int longestShortest = 0;

        int cnt = 0;
        int increment = synsetList.size() / 100;
        for (int i=0; i < synsetList.size(); i+=increment) {
            //for (Synset synset1 : synsetList) {
            Synset synset1 = synsetList.get(i);
            for (int j = i+1; j < synsetList.size(); j++) {
                //for (Synset synset2 : synsetList) {
                Synset synset2 = synsetList.get(j);
                cnt++;
                if (cnt%100000 == 0) {
                    LOGGER.info("number processed: {}", cnt);
                }
                if (synset1.getWordCategory() == synset2.getWordCategory()) {
                    lcsList = gnetGraphUtils.leastCommonSubsumer(synset1, synset2);
                    if (lcsList.size() > 1) {
                        //LOGGER.info("Multiple LCS found: {}", lcsList);
                    } else if ( ! lcsList.isEmpty()) {
                        LeastCommonSubsumer lcs = lcsList.iterator().next();
                        if (lcs.distance > longestShortest) {
                            longestShortest = lcs.distance;
                        }
                    }
                }
            }
        }
        assertEquals(35, longestShortest);
    }


    // ToDo: this is not a real test
    //@Test
    void lcsAllTest() {
        List<Synset> synsetList = gnetCaseSensitive.getSynsets();
        GermaNetVertex lcs;
        for (Synset synset1 : synsetList) {
            for (Synset synset2 : synsetList) {
                if (synset1.getWordClass() == synset2.getWordClass()) {
                    lcs = gnetGraph.lcs(gnetGraph.getHyponymGraph(), synset1, synset2);
                    if (lcs != null && lcs.getId() == GNROOT_ID) {
                        LOGGER.warn("LCS is GNROOT for synsets in the same word category: synset1: [{}] \tsynset2: [{}] \tLCS: {}", synset1, synset2, lcs);
                    }
                }
            }
        }
    }

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

    // ToDo: this is not a real test
    @Test
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

    // ToDo: this is not a real test
    @Test
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

    // ToDo: this is not a real test
    @Test
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

    // ToDo: this is not a real test
    @Test
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

    // ToDo: this is not a real test
    @Test
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

    // ToDo: this is not a real test
    @Test
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
