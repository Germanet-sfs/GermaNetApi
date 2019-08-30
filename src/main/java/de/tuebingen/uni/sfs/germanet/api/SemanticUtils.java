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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;

import static java.util.stream.Collectors.toCollection;

/**
 * Utility class for doing calculations needed by the semanitic relatedness algorithms.
 *
 * @author Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 * @author Ben Campbell, Seminar für Sprachwissenschaft, Universität Tübingen
 */
class SemanticUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticUtils.class);
    private GermaNet gnet;
    private Map<WordCategory, Map<Integer, SynsetNode>> catDistMap;
    private Map<WordCategory, Set<LeastCommonSubsumer>> catLongestLCSMap;
    private Map<WordCategory, Integer> catMaxDistMap;

    SemanticUtils(GermaNet gnet) {
        this.gnet = gnet;
        catLongestLCSMap = new HashMap<>(WordCategory.values().length);
        catDistMap = new HashMap<>(WordCategory.values().length);
        catMaxDistMap = new HashMap<>(WordCategory.values().length);
        loadDistanceMaps();
    }

    /**
     * For each WordCategory, create a map of SynsetNodes with synsetIDs as keys.
     * Each SynsetNode contains the shortest distance to each of its hypernyms on all
     * paths from the synset to root. Finds the longest path between two synsets (using
     * hypernym relations only), for later use in calculating the longest least common
     * subsumer.
     */
    private void loadDistanceMaps() {
        LOGGER.info("Loading distance maps...");

        long startTime = System.currentTimeMillis();

        // create a separate map for each word category
        for (WordCategory wordCategory : WordCategory.values()) {
            Map<Integer, SynsetNode> hypernymDistances = new HashMap<>();
            List<SynsetNode> hypernymDistanceList = new ArrayList<>();
            List<Synset> synsetList = gnet.getSynsets(wordCategory);

            //longest path between any synset and any of its hypernyms
            int maxHypernymDistance = 0;
            for (Synset synset : synsetList) {
                int synsetID = synset.getId();
                SynsetNode synsetNode = new SynsetNode(synsetID);

                // recursively find the shortest distances from this synset to all of its hypernyms
                buildHypernymTree(synsetNode, synset, 0);
                hypernymDistances.put(synsetID, synsetNode);
                hypernymDistanceList.add(synsetNode);

                // update maxHypernymDistance if necessary
                int synsetMaxDistance = synsetNode.getMaxDistance();
                if (synsetMaxDistance > maxHypernymDistance) {
                    maxHypernymDistance = synsetMaxDistance;
                }
            }
            catDistMap.put(wordCategory, hypernymDistances);
            catMaxDistMap.put(wordCategory, maxHypernymDistance);
        }

        long endTime = System.currentTimeMillis();
        double processingTime = (double) (endTime - startTime) / 1000;
        DecimalFormat dec = new DecimalFormat("#0.00");
        LOGGER.info("Done loading distance maps ({} seconds).", dec.format(processingTime));
    }

    /**
     * Recursively calculate the shortest distance from synsetNode to all of its hypernyms up to root.
     *
     * @param synsetNode the node currently being processed
     * @param synset     one of synsetNode's hypernyms on the path up to root
     * @param depth      distance from synsetNode to synset
     */
    private void buildHypernymTree(SynsetNode synsetNode, Synset synset, int depth) {
        List<Synset> hypernymList = synset.getRelatedSynsets(ConRel.has_hypernym);
        depth++;

        // process each direct hypernym of this synset
        for (Synset hypernym : hypernymList) {

            int hypernymID = hypernym.getId();
            Integer distanceToHypernym = synsetNode.getDistanceToHypernym(hypernymID);

            if (distanceToHypernym == null) {
                // this hypernym has not been seen before by this synsetNode, add it
                synsetNode.putDistanceMap(hypernymID, depth);
                synsetNode.addHypernymId(hypernymID);
                // update this synsetNode's max distance to any of its hypernyms, if necessary
                if (depth > synsetNode.getMaxDistance()) {
                    synsetNode.setMaxDistance(depth);
                }
            } else if (depth < distanceToHypernym) {
                // this distance is shorter than some previously calculated value, replace it
                synsetNode.putDistanceMap(hypernymID, depth);
            }

            // process the next level upwards for this synsetNode
            buildHypernymTree(synsetNode, hypernym, depth);
        }
    }

    /**
     * Calculate the longest least common subsumer(s) for wordCategory. This is used by the
     * semantic relatedness algorithms.
     *
     * @param wordCategory WordCategory to process
     * @return a set of LeastCommonSubsumers with the longest possible paths for WordCategory
     */
    Set<LeastCommonSubsumer> longestLeastCommonSubsumer(WordCategory wordCategory) {

        // if it has already been calculated, just return the value
        if (catLongestLCSMap.get(wordCategory) != null) {
            return catLongestLCSMap.get(wordCategory);
        }

        LOGGER.info("Calculating longest shortest path for {}...", wordCategory.name());
        Set<LeastCommonSubsumer> leastCommonSubsumers = new HashSet<>();

        // processing is faster if the nodes are sorted by maxDistance to any node on its path to root
        List<SynsetNode> hypernymDistances = catDistMap.get(wordCategory).values().stream()
                .sorted()
                .collect(toCollection(ArrayList::new));

        int longestDistance = 0;
        //longest path between any synset and any of its hypernyms
        int maxHypernymDistance = catMaxDistMap.get(wordCategory);

        // iterate over all pairs of synsets, avoiding processing any pair twice
        for (int i = 0; i < hypernymDistances.size(); i++) {
            SynsetNode synsetNode1 = hypernymDistances.get(i);
            int synsetNode1MaxHypernymDist = synsetNode1.getMaxDistance();

            // not possible for this synset to be one of the longest
            if (maxHypernymDistance + synsetNode1MaxHypernymDist < longestDistance) {
                continue;
            }

            // start at i+1 to avoid double processing
            for (int j = i + 1; j < hypernymDistances.size(); j++) {
                SynsetNode synsetNode2 = hypernymDistances.get(j);

                // not possible for these two nodes to have a path longer than
                // the longest distance so far
                if (synsetNode2.getMaxDistance() + synsetNode1MaxHypernymDist < longestDistance) {
                    continue;
                }

                // find the least common subsumers for these two synsets
                Set<LeastCommonSubsumer> lcsSet = getLeastCommonSubsumer(synsetNode1, synsetNode2);
                int pathLength = lcsSet.iterator().next().getDistance();

                if (pathLength > longestDistance) {
                    // path length is longer that any found so far
                    // replace LCS set with this one
                    leastCommonSubsumers.clear();
                    leastCommonSubsumers.addAll(lcsSet);
                    longestDistance = pathLength;
                } else if (pathLength == longestDistance) {
                    // path length is equal to longest so far
                    // add this one to result
                    leastCommonSubsumers.addAll(lcsSet);
                }
            }
        }

        catLongestLCSMap.put(wordCategory, leastCommonSubsumers);
        return leastCommonSubsumers;
    }

    /**
     * Find the least common subsumer(s) for these two synsets. This is the closest common parent,
     * using hypernym relations only.
     *
     * @param synset1 one synset
     * @param synset2 another synset
     * @return a set of LeastCommonSubsumer objects, each of which contains a synset ID of a synset that is
     * a common parent of both input synsets, and which has the shortest possible distance of all common parents.
     * It is possible that multiple least common subsumers exist for the input synsets, in which case
     * all least common subsumers will have the same, shortest, distance.
     */
    Set<LeastCommonSubsumer> getLeastCommonSubsumer(Synset synset1, Synset synset2) {
        //  ToDo: do something if the word categories are not the same
        // throw exception, return empty Set, return null?
        WordCategory wordCategory = synset1.getWordCategory();
        if (!synset2.inWordCategory(wordCategory)) {
            return null;
        }

        SynsetNode synsetNode1 = catDistMap.get(wordCategory).get(synset1.getId());
        SynsetNode synsetNode2 = catDistMap.get(wordCategory).get(synset2.getId());

        return getLeastCommonSubsumer(synsetNode1, synsetNode2);
    }

    /**
     * Find the least common subsumer(s) for these two synsets. This is the closest common parent,
     * using hypernym relations only.
     *
     * @param synsetNode1 one synsetNode
     * @param synsetNode2 another synsetNode
     * @return a set of LeastCommonSubsumer objects, each of which contains a synset ID of a synset that is
     * a common parent of both input synsets, and which has the shortest possible distance of all common parents.
     * It is possible that multiple least common subsumers exist for the input synsets, in which case
     * all least common subsumers will have the same, shortest, distance.
     */
    private Set<LeastCommonSubsumer> getLeastCommonSubsumer(SynsetNode synsetNode1, SynsetNode synsetNode2) {

        Set<LeastCommonSubsumer> rval = new HashSet<>();
        int shortestDistance = Integer.MAX_VALUE;

        Set<Integer> node1Ids = synsetNode1.getHypernymIds();
        Set<Integer> node2Ids = synsetNode2.getHypernymIds();
        // the intersection of the hypernyms are the common subsumers
        Set<Integer> intersection = Sets.intersection(node1Ids, node2Ids);

        int synsetNode1ID = synsetNode1.getSynsetId();
        int synsetNode2ID = synsetNode2.getSynsetId();

        // find all of the common subsumers with the shortest distance between the 2 nodes
        for (int hypernymID : intersection) {
            int distance = getDistanceBetweenTwoNodesWithHypernym(hypernymID, synsetNode1, synsetNode2);
            if (distance < shortestDistance) {
                rval.clear();
                rval.add(new LeastCommonSubsumer(hypernymID, Sets.newHashSet(synsetNode1ID, synsetNode2ID), distance));
                shortestDistance = distance;
            } else if (distance == shortestDistance) {
                rval.add(new LeastCommonSubsumer(hypernymID, Sets.newHashSet(synsetNode1ID, synsetNode2ID), distance));
            }
        }
        return rval;
    }

    /**
     * Find the distance between the 2 nodes through the common hypernym.
     *
     * @param hypernym a hypernym of both nodes
     * @param node1    a node
     * @param node2    another node
     * @return the distance between the 2 nodes through the common hypernym.
     */
    private int getDistanceBetweenTwoNodesWithHypernym(int hypernym, SynsetNode node1, SynsetNode node2) {
        return node1.getDistanceToHypernym(hypernym) + node2.getDistanceToHypernym(hypernym);
    }
}
