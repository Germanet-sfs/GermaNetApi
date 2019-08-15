package de.tuebingen.uni.sfs.germanet.semantic;

import de.tuebingen.uni.sfs.germanet.api.ConRel;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static de.tuebingen.uni.sfs.germanet.semantic.Semantic.GNROOT_ID;

/**
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public class GermaNetGraphUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(GermaNetGraphUtils.class);
    private GermaNet gnet;
    Map<Synset, Set<GermaNetPath>> synsetPathsToRootMap;

    public GermaNetGraphUtils(GermaNet gnet) {
        this.gnet = gnet;
        synsetPathsToRootMap = new HashMap<>(gnet.numSynsets());
        loadPathsToRootMap();
    }

    private void loadPathsToRootMap() {
        List<Synset> synsets = gnet.getSynsets();
        Synset gnRoot = gnet.getSynsetByID(GNROOT_ID);

        for (Synset synset : synsets) {
            // get all paths from each node to GNROOT, using hypernym relations only,
            // and add them to the map
            Set<GermaNetPath> paths = getAllPaths(synset, gnRoot);
            synsetPathsToRootMap.put(synset, paths);
        }
    }

    /**
     * Implementation of Ricardo Simões's APAC algorithm to get all paths from v1 to v2:
     * https://pdfs.semanticscholar.org/2722/24e29e2029a99c6c780659a4c0375ad4a9f2.pdf
     * Uses hypernym relations only
     *
     * @param s
     * @param t
     * @return
     */
    public Set<GermaNetPath> getAllPaths(Synset s, Synset t) {
        Set<GermaNetPath> rval = new HashSet<>();
        List<GermaNetPath> allCycles = new ArrayList<>();

        GermaNetPath P = new GermaNetPath();
        Stack<GermaNetPath> S = new Stack<>();
        GermaNetPath V;

        P.add(s);
        S.push(P);

        while (!S.empty()) {
            P = S.pop();

            // get the last node on the path
            Synset u = P.getLastOnPath();
            for (Synset v : u.getRelatedSynsets(ConRel.has_hypernym)) {
                if (!P.contains(v)) {
                    if (v.equals(t)) {
                        P.add(v);
                        rval.add(P);
                    } else {
                        V = new GermaNetPath();
                        V.addAll(P);
                        V.add(v);
                        S.push(V);
                    }
                } else {
                    allCycles.add(P);
                }
            }
        }
        if ( ! allCycles.isEmpty()) {
            LOGGER.debug("Cycles found: {}", allCycles);
        }
        return rval;
    }

    /**
     * ToDo: javadoc
     *
     * @param s1
     * @param s2
     * @return
     */
    // find all paths to root for both synsets
    // get the intersecting nodes of each path for synset1 and synset2
    // for each intersecting node:
    //     calculate the length of the path from s1 to s2 through that node
    //     keep track of the path with the shortest length
    // Too SLOW!
    public Set<LeastCommonSubsumer> leastCommonSubsumerOrig(Synset s1, Synset s2) {
        Set<LeastCommonSubsumer> lcsSet = new HashSet<>();

        //  ToDo: do something if the word categories are not the same
        // throw exception, return empty Set, return null?
        if (!s1.inWordCategory(s2.getWordCategory())) {
            return null;
        }

        // get all paths from each vertex to GNROOT, using hypernym relations only
        Set<GermaNetPath> s1PathsToRoot = synsetPathsToRootMap.get(s1);
        Set<GermaNetPath> s2PathsToRoot = synsetPathsToRootMap.get(s2);

        // compare all s1 paths to all s2 paths, looking for nodes that are contained in both paths
        int shortestPathLength = Integer.MAX_VALUE;

        for (GermaNetPath s1PathToRoot : s1PathsToRoot) {
            for (GermaNetPath s2PathToRoot : s2PathsToRoot) {

                Set<Synset> intersection = new HashSet<>();
                // add all nodes on s2PathToRoot to intersection set
                intersection.addAll(s2PathToRoot.getSynsetSet());
                // retain only the nodes that are in both sets
                intersection.retainAll(s1PathToRoot.getSynsetSet());

                LOGGER.debug("\ncomparing paths:\n\t{}\n\t{}\n\tIntersection nodes: {}", s1PathToRoot, s2PathToRoot, intersection);

                // for each node found, calculate the path length (from s1 to the node) + (from s2 to the node)
                // keep track of which is the shortest so far
                for (Synset intersectionNode : intersection) {

                    // number of edges from s1 to intersectionNode
                    int s1DistToIntersectionNode = s1PathToRoot.indexOf(intersectionNode);

                    // number of edges from s2 to intersectionNode
                    int s2DistToIntersectionNode = s2PathToRoot.indexOf(intersectionNode);

                    // sum is the total distance
                    int dist = s1DistToIntersectionNode + s2DistToIntersectionNode;

                    // this one is shorter than any found so far
                    if (dist < shortestPathLength) {
                        // replace the lcsSet with this LCS
                        LeastCommonSubsumer leastCommonSubsumer = new LeastCommonSubsumer(
                                intersectionNode,
                                s1PathToRoot.getSynsetList().subList(0, s1DistToIntersectionNode + 1),
                                s2PathToRoot.getSynsetList().subList(0, s2DistToIntersectionNode + 1),
                                dist);
                        lcsSet.clear();
                        lcsSet.add(leastCommonSubsumer);
                        shortestPathLength = dist;
                    } else if (dist == shortestPathLength) {
                        // add this LCS to the set
                        LeastCommonSubsumer leastCommonSubsumer = new LeastCommonSubsumer(
                                intersectionNode,
                                s1PathToRoot.getSynsetList().subList(0, s1DistToIntersectionNode + 1),
                                s2PathToRoot.getSynsetList().subList(0, s2DistToIntersectionNode + 1),
                                dist);
                        lcsSet.add(leastCommonSubsumer);
                    }
                }
            }
        }
        // return nodes with the shortest path
        return lcsSet;
    }

    // work our way up from s1 and s2, looking for an intersecting node
    // Even SLOWER!
    public Set<LeastCommonSubsumer> leastCommonSubsumer(Synset s1, Synset s2) {

        //  ToDo: do something if the word categories are not the same
        // throw exception, return empty Set, return null?
        if (!s1.inWordCategory(s2.getWordCategory())) {
            return null;
        }

        Set<Synset> s1Set = new HashSet<>();
        Set<Synset> s2Set = new HashSet<>();
        Set<Synset> s1LastAdded = new HashSet<>();
        Set<Synset> s2LastAdded = new HashSet<>();
        Map<Synset, Synset> s1SynsetParentMap = new HashMap<>();
        Map<Synset, Synset> s2SynsetParentMap = new HashMap<>();

        s1Set.add(s1);
        s2Set.add(s2);
        s1LastAdded.add(s1);
        s2LastAdded.add(s2);
        s1SynsetParentMap.put(s1, null);
        s2SynsetParentMap.put(s2, null);

        return leastCommonSubsumer(s1Set, s2Set, s1LastAdded, s2LastAdded, s1SynsetParentMap, s2SynsetParentMap);
    }

    private Set<LeastCommonSubsumer> leastCommonSubsumer(Set<Synset> s1Set, Set<Synset> s2Set, Set<Synset> s1LastAdded, Set<Synset> s2LastAdded, Map<Synset, Synset> s1SynsetParentMap, Map<Synset, Synset> s2SynsetParentMap) {

        Set<LeastCommonSubsumer> rval = new HashSet<>();

        // see if there are any intersecting nodes in the hypernym paths
        Set<Synset> intersection = new HashSet<>();
        intersection.addAll(s1Set);
        intersection.retainAll(s2Set);

        if (intersection.isEmpty()) {
            // no intersecting nodes yet, add the next nodes,
            // which are the hypernyms of the synsets added in the previous step
            Set<Synset> s1TmpLast = new HashSet<>();
            for (Synset last : s1LastAdded) {
                for (Synset hypernym : last.getRelatedSynsets(ConRel.has_hypernym)) {
                    if (!s1Set.contains(hypernym)) {
                        s1Set.add(hypernym);
                        s1SynsetParentMap.put(hypernym, last);
                        s1TmpLast.add(hypernym);
                    }
                }
            }
            Set<Synset> s2TmpLast = new HashSet<>();
            for (Synset last : s2LastAdded) {
                for (Synset hypernym : last.getRelatedSynsets(ConRel.has_hypernym)) {
                    if (!s2Set.contains(hypernym)) {
                        s2Set.add(hypernym);
                        s2SynsetParentMap.put(hypernym, last);
                        s2TmpLast.add(hypernym);
                    }
                }
            }

            // set nextSets for next iteration
            s1LastAdded = s1TmpLast;
            s2LastAdded = s2TmpLast;

            return leastCommonSubsumer(s1Set, s2Set, s1LastAdded, s2LastAdded, s1SynsetParentMap, s2SynsetParentMap);
        } else {
            for (Synset lcsNode : intersection) {
                List<Synset> s1PathToLCS = new ArrayList<>();
                Synset pathNode = lcsNode;
                while (pathNode != null) {
                    s1PathToLCS.add(pathNode);
                    pathNode = s1SynsetParentMap.get(pathNode);
                }
                List<Synset> s2PathToLCS = new ArrayList<>();
                pathNode = lcsNode;
                while (pathNode != null) {
                    s2PathToLCS.add(pathNode);
                    pathNode = s2SynsetParentMap.get(pathNode);
                }
                Collections.reverse(s1PathToLCS);
                Collections.reverse(s2PathToLCS);
                int distance = s1PathToLCS.size() + s2PathToLCS.size() - 2; // edges
                LeastCommonSubsumer lcs = new LeastCommonSubsumer(lcsNode,
                        s1PathToLCS, s2PathToLCS, distance);
                rval.add(lcs);
            }
            return rval;
        }
    }

    public Set<LeastCommonSubsumer> longestLeastCommonSubsumer(WordCategory wordCategory) {
        Set<LeastCommonSubsumer> lcsSet = new HashSet<>();
        int longestShortestPathLength = 0;
        Synset synset1;
        Synset synset2;

        Synset gnRoot = gnet.getSynsetByID(GNROOT_ID);
        List<Synset> synsetsWithWordCat = gnet.getSynsets(wordCategory);

        for (int i = 0; i < synsetsWithWordCat.size(); i++) {
            synset1 = synsetsWithWordCat.get(i);
            for (int j = i + 1; j < synsetsWithWordCat.size(); j++) {
                synset2 = synsetsWithWordCat.get(j);
                int dist;
                Set<LeastCommonSubsumer> tmpLcsList = leastCommonSubsumer(synset1, synset2);
                if (tmpLcsList != null && !tmpLcsList.isEmpty()) {
                    dist = tmpLcsList.iterator().next().distance;
                    if (dist > longestShortestPathLength) {
                        // replace the lcsList with this one
                        lcsSet.clear();
                        lcsSet.addAll(tmpLcsList);
                        longestShortestPathLength = dist;
                    } else if (dist == longestShortestPathLength) {
                        // add this LCS list to the running list
                        lcsSet.addAll(tmpLcsList);
                    }
                }
            }
        }

        // return list of longest shortest paths
        return lcsSet;
    }

    /*
    public Set<LeastCommonSubsumer> longestLeastCommonSubsumerUsingPrecalculatedPathsToRoot(WordCategory wordCategory) {
        Set<LeastCommonSubsumer> lcsSet = new HashSet<>();
        int longestShortestPathLength = 0;
        Synset synset1;
        Synset synset2 = null;

        List<Synset> synsetsWithWordCat = gnet.getSynsets(wordCategory);

        for (int i=0; i < synsetsWithWordCat.size(); i++) {
            synset1 = synsetsWithWordCat.get(i);
            // get all paths from synset1 to GNROOT from the map
            Set<GermaNetPath> s1PathsToRoot = synsetPathsToRootMap.get(synset1);

            for (int j = i + 1; j < synsetsWithWordCat.size(); j++) {
                synset2 = synsetsWithWordCat.get(j);
                // get all paths from synset2 to GNROOT from the map
                Set<GermaNetPath> s2PathsToRoot = synsetPathsToRootMap.get(synset2);

                // compare all s1 paths to all s2 paths,
                // looking for nodes that are contained in both paths
                for (GermaNetPath s1Path : s1PathsToRoot) {
                    for (GermaNetPath s2Path : s2PathsToRoot) {

                        // skip if the sum of the path lengths to root is shorter than minPathLength
                        //if (s1Path.pathLength() + s2Path.pathLength() < minPathLength) {
                        //    continue;
                        //}

                        Set<Synset> intersection = new HashSet<>();
                        // add all nodes on s2Path to intersection set
                        intersection.addAll(s2Path.getSynsetSet());
                        // retain only the nodes that are in both sets
                        intersection.retainAll(s1Path.getSynsetSet());

                        LOGGER.debug("\ncomparing paths:\n\t{}\n\t{}\n\tIntersection nodes: {}", s1Path, s2Path, intersection);

                        // for each node found, calculate the path length (from s1 to the node) + (from s2 to the node)
                        // keep track of which is the shortest so far
                        for (Synset intersectionNode : intersection) {

                            // number of edges from s1 to intersectionNode
                            int s1DistToIntersectionNode = s1Path.indexOf(intersectionNode);

                            // number of edges from s2 to intersectionNode
                            int s2DistToIntersectionNode = s2Path.indexOf(intersectionNode);

                            // sum is the total distance
                            int dist = s1DistToIntersectionNode + s2DistToIntersectionNode;

                            // this one is shorter than any found so far
                            LeastCommonSubsumer leastCommonSubsumer = new LeastCommonSubsumer(
                                    intersectionNode,
                                    s1Path.getSynsetList().subList(0, s1DistToIntersectionNode+1),
                                    s2Path.getSynsetList().subList(0, s2DistToIntersectionNode+1),
                                    dist,
                                    Math.min(s1Path.pathLength(), s2Path.pathLength()));
                            if (dist > longestShortestPathLength) {
                                // replace the lcsSet with this LCS
                                lcsSet.clear();
                                lcsSet.add(leastCommonSubsumer);
                                longestShortestPathLength = dist;
                                LOGGER.info("longer path found {}", lcsSet);
                            } else if (dist == longestShortestPathLength) {
                                // add this LCS to the list
                                lcsSet.add(leastCommonSubsumer);
                            }
                        }
                    }
                }
            }
        }


        // return the set of LCS's found
        return lcsSet;
    }
    */

    /*public Set<LeastCommonSubsumer> longestLeastCommonSubsumerOrig(WordCategory wordCategory, int minPathLength) {
        Set<LeastCommonSubsumer> lcsList = new HashSet<>();
        Map<Synset, List<GermaNetPath>> synsetPathsToRootMap = new HashMap<>();
        int longestShortestPathLength = 0;
        Synset synset1 = null;
        Synset synset2 = null;

        Synset gnRoot = gnet.getSynsetByID(GNROOT_ID);
        List<Synset> synsetsWithWordCat = gnet.getSynsets(wordCategory);

        for (int i=0; i < synsetsWithWordCat.size(); i++) {
            synset1 = synsetsWithWordCat.get(i);
            for (int j = i + 1; j < synsetsWithWordCat.size(); j++) {
                synset2 = synsetsWithWordCat.get(j);

                // get all paths from both nodes to GNROOT, using hypernym relations only
                // if it has already been calculated, it will be in the map
                // otherwise calculate it and add it to the map
                List<GermaNetPath> s1PathsToRoot = synsetPathsToRootMap.get(synset1);
                if (s1PathsToRoot == null) {
                    s1PathsToRoot = getAllPaths(synset1, gnRoot);
                    synsetPathsToRootMap.put(synset1, s1PathsToRoot);
                }
                List<GermaNetPath> s2PathsToRoot = synsetPathsToRootMap.get(synset2);
                if (s2PathsToRoot == null) {
                    s2PathsToRoot = getAllPaths(synset2, gnRoot);
                    synsetPathsToRootMap.put(synset2, s2PathsToRoot);
                }

                // compare all s1 paths to all s2 paths,
                // looking for nodes that are contained in both paths
                for (GermaNetPath s1Path : s1PathsToRoot) {

                    // skip if the sum of the path lengths to root is shorter than minPathLength
                    for (GermaNetPath s2Path : s2PathsToRoot) {
                        if (s1Path.pathLength() + s2Path.pathLength() < minPathLength) {
                            continue;
                        }

                        Set<Synset> intersection = new HashSet<>();
                        // add all nodes on s2Path to intersection set
                        intersection.addAll(s2Path.synsetSet);
                        // retain only the nodes that are in both sets
                        intersection.retainAll(s1Path.synsetSet);

                        LOGGER.debug("\ncomparing paths:\n\t{}\n\t{}\n\tIntersection nodes: {}", s1Path, s2Path, intersection);

                        // for each node found, calculate the path length (from s1 to the node) + (from s2 to the node)
                        // keep track of which is the shortest so far
                        for (Synset intersectionNode : intersection) {

                            // number of edges from s1 to intersectionNode
                            int s1DistToIntersectionNode = s1Path.indexOf(intersectionNode);

                            // number of edges from s2 to intersectionNode
                            int s2DistToIntersectionNode = s2Path.indexOf(intersectionNode);

                            // sum is the total distance
                            int dist = s1DistToIntersectionNode + s2DistToIntersectionNode;

                            // this one is shorter than any found so far
                            LeastCommonSubsumer leastCommonSubsumer = new LeastCommonSubsumer(
                                    intersectionNode,
                                    s1Path.synsetList.subList(0, s1DistToIntersectionNode+1),
                                    s2Path.synsetList.subList(0, s2DistToIntersectionNode+1),
                                    s1Path.synsetList, s2Path.synsetList,
                                    dist);
                            if (dist > longestShortestPathLength) {
                                // replace the lcsList with this LCS
                                lcsList.clear();
                                lcsList.add(leastCommonSubsumer);
                                longestShortestPathLength = dist;
                                LOGGER.info("longer path found {}", lcsList);
                            } else if (dist == longestShortestPathLength) {
                                // add this LCS to the list
                                lcsList.add(leastCommonSubsumer);
                            }
                        }
                    }
                }
            }
        }

        // return the node with the shortest path
        return lcsList;
    }*/

    /*
    public GermaNetVertex leastCommonSubsumer(GermaNetVertex v1, GermaNetVertex v2) {

        GermaNetVertex gnRoot = gnet.getSynsetByID(GNROOT_ID);

        AllDirectedPaths<GermaNetVertex, GermaNetEdge> pathFinder = new AllDirectedPaths<>(hypernymGraph);
        boolean simplePathsOnly = true;
        Integer maxPathLength = null; // ToDo: find value for this (~35?)

        // get all paths from each vertex to GNROOT, using hypernym relations only
        List<GraphPath<GermaNetVertex, GermaNetEdge>> v1PathsToRoot = pathFinder.getAllPaths(v1, gnRoot, simplePathsOnly, maxPathLength);
        List<GraphPath<GermaNetVertex, GermaNetEdge>> v2PathsToRoot = pathFinder.getAllPaths(v2, gnRoot, simplePathsOnly, maxPathLength);


        // compare all v1 paths to all v2 paths, looking for nodes that are contained in both paths
        // using Sets is more reliable and efficient than Lists
        //Set<GermaNetVertex> commonNodes = new HashSet<>();
        int shortestPathLength = Integer.MAX_VALUE;
        GermaNetVertex leastCommonSubsumer = null;
        for (GraphPath<GermaNetVertex, GermaNetEdge> v1Path : v1PathsToRoot) {
            Set<GermaNetVertex> v1NodeSet = new HashSet<>();
            List<GermaNetVertex> v1PathVertexList = v1Path.getVertexList();
            v1NodeSet.addAll(v1PathVertexList);

            for (GraphPath<GermaNetVertex, GermaNetEdge> v2Path : v2PathsToRoot) {
                Set<GermaNetVertex> intersection = new HashSet<>();
                List<GermaNetVertex> v2PathVertexList = v2Path.getVertexList();

                // add all nodes on v2Path to intersection set
                intersection.addAll(v2PathVertexList);
                // retain only the nodes that are in both sets
                intersection.retainAll(v1NodeSet);

                LOGGER.debug("\ncomparing paths:\n\t{}\n\t{}\n\tIntersection nodes: {}", v1PathVertexList, v2PathVertexList, intersection);

                // for each node found, calculate the path length (from v1 to the node) + (from v2 to the node)
                // keep track of which is the shortest so far
                for (GermaNetVertex intersectionNode : intersection) {
                    // count nodes from v1 to intersectionNode
                    int v1DistToIntersectionNode = 0;
                    for (GermaNetVertex v1PathNode : v1PathVertexList) {
                        if (!v1PathNode.equals(intersectionNode)) {
                            v1DistToIntersectionNode++;
                        }
                    }
                    // count nodes from v2 to intersectionNode
                    int v2DistToIntersectionNode = 0;
                    for (GermaNetVertex v2PathNode : v2PathVertexList) {
                        if (!v2PathNode.equals(intersectionNode)) {
                            v2DistToIntersectionNode++;
                        }
                    }

                    int dist = v1DistToIntersectionNode + v2DistToIntersectionNode;
                    if (dist < shortestPathLength) {
                        shortestPathLength = dist;
                        leastCommonSubsumer = intersectionNode;
                    }
                }
            }
        }

        // return the node with the shortest path
        return leastCommonSubsumer;
    }
     */
}
