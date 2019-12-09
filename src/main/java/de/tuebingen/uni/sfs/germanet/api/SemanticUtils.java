/*
 * Copyright (C) 2019 Department of General and Computational Linguistics,
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

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * Utility class for doing calculations needed by the semantic relatedness algorithms.
 *
 * @author Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public class SemanticUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticUtils.class);
    private Map<WordCategory, Integer> catMaxHypernymDistanceMap;
    private Map<WordCategory, Set<LeastCommonSubsumer>> catLongestLCSMap;
    private Map<WordCategory, Integer> catMaxDepthMap;
    private Map<WordCategory, Map<SemRelMeasure, List<Double>>> catNormalizationMap;
    private GermaNet gnet;

    SemanticUtils(Map<WordCategory, Integer> catMaxHypernymDistanceMap, GermaNet gnet) {
        this.catMaxHypernymDistanceMap = catMaxHypernymDistanceMap;
        this.gnet = gnet;
        catMaxDepthMap = new HashMap<>(3);
        catNormalizationMap = new HashMap<>();
        LOGGER.info("Initializing SemanticUtils object...");
        initCatLongestLCSMap();
        initMaxDepthMap();
        initMinMaxValues();
        LOGGER.info("Done initializing SemanticUtils object.");
    }

    private void initMaxDepthMap() {
        LOGGER.info("Calculating max depths...");
        for (WordCategory wordCategory : WordCategory.values()) {
            List<Synset> synsets = gnet.getSynsets(wordCategory);
            int maxDepth = 0;

            for (Synset synset : synsets) {
                int depth = synset.getDepth();
                if (depth > maxDepth) {
                    maxDepth = depth;
                }
            }
            catMaxDepthMap.put(wordCategory, maxDepth);
            LOGGER.info("Max depth for {}: {}", wordCategory, maxDepth);
        }
        LOGGER.info("Done calculating max depths.");
    }

    /**
     * Initialize the normalization values for each word category and each relatedness measure.
     */
    private void initMinMaxValues() {
        //
        for (WordCategory wordCategory : WordCategory.values()) {
            Map<SemRelMeasure, List<Double>> normalizeMinMaxValues = new HashMap<>();

            for (SemRelMeasure semRelMeasure : SemRelMeasure.values()) {
                // min at index1, max at index2
                List<Double> maxMin = new ArrayList<>(2); // min at index1, max at index2
                List<Integer> maxDistantSynsetIds = Lists.newArrayList(catLongestLCSMap.get(wordCategory).iterator().next().getFromToSynsetIDs());
                Synset synset1 = gnet.getSynsetByID(maxDistantSynsetIds.get(0));
                Synset synset2 = gnet.getSynsetByID(maxDistantSynsetIds.get(1));
                double minVal = 0;
                double maxVal = 1;

                switch (semRelMeasure) {
                    case Path:
                        minVal = getSimilarityPath(synset1, synset2, 0); // least similar, lowest value
                        maxVal = getSimilarityPath(synset1, synset1, 0); // most similar, highest value
                        break;

                    case LeacockAndChodorow:
                        minVal = getSimilarityLeacockChodorow(synset1, synset2, 0); // least similar, lowest value
                        maxVal = getSimilarityLeacockChodorow(synset1, synset1, 0); // most similar, highest value
                        break;

                    case WuAndPalmer:
                        minVal = getSimilarityWuAndPalmer(synset1, synset2, 0); // least similar, lowest value
                        maxVal = getSimilarityWuAndPalmer(synset1, synset1, 0); // most similar, highest value
                        break;

                    default:
                        break;
                }

                maxMin.add(minVal);
                maxMin.add(maxVal);
                normalizeMinMaxValues.put(semRelMeasure, maxMin);
            }
            catNormalizationMap.put(wordCategory, normalizeMinMaxValues);
        }
    }

    /**
     * Normalize the relatedness value, taking into consideration the WordCategory, algorithm, and upper bound.
     * The lower bound is 0.0.
     * @param wordCategory the WordCategory
     * @param semRelMeasure the algorithm
     * @param rawValue raw value of the algorithm
     * @param normalizedMax upper bound on normalization range (lower bound is 0)
     * @return the normalized value, taking into consideration the WordCategory, algorithm, and requested upper bound
     */
    private double normalize(WordCategory wordCategory, SemRelMeasure semRelMeasure, double rawValue, int normalizedMax) {
        double minVal = catNormalizationMap.get(wordCategory).get(semRelMeasure).get(0);
        double maxVal = catNormalizationMap.get(wordCategory).get(semRelMeasure).get(1);

        // ToDo: check normalization function
        return (rawValue / maxVal) * normalizedMax;
    }

    private void initCatLongestLCSMap() {
        if (catLongestLCSMap == null) {
            catLongestLCSMap = new HashMap<>();
        }
        LOGGER.info("Calculating longest LCS's...");
        for (WordCategory wordCategory : WordCategory.values()) {
            // don't calculate again if it was already done
            if (catLongestLCSMap.get(wordCategory) == null) {
                long startTime = System.currentTimeMillis();
                Set<LeastCommonSubsumer> lcsSet = longestLeastCommonSubsumer(wordCategory);
                catLongestLCSMap.put(wordCategory, lcsSet);

                long endTime = System.currentTimeMillis();
                double processingTime = (double) (endTime - startTime) / 1000;
                LOGGER.info("Found {} longest LCS for {} of length {} (in {} seconds).", lcsSet.size(),
                        wordCategory.name(), lcsSet.iterator().next().getDistance(), processingTime);
            }
        }
        LOGGER.info("Done calculating longest LCS's.");
    }

    /**
     * Calculate the longest least common subsumer(s) for wordCategory. This is used by
     * some of the semantic relatedness algorithms.
     *
     * @param wordCategory WordCategory to process
     * @return a set of LeastCommonSubsumers with the longest possible paths for WordCategory
     */
    private Set<LeastCommonSubsumer> longestLeastCommonSubsumer(WordCategory wordCategory) {
        Set<LeastCommonSubsumer> leastCommonSubsumers = new HashSet<>();

        // processing is faster if the synsets are sorted by maxDistance to any synset on its path to root
        List<Synset> hypernymDistances = gnet.getSynsets(wordCategory);
        Collections.sort(hypernymDistances, (s1, s2) -> (s2.getMaxDistance() - s1.getMaxDistance()));

        // longest distance between 2 synsets found so far
        int longestDistance = 0;
        // longest path between a synset and any of its hypernyms in the graph
        int maxHypernymDistance = catMaxHypernymDistanceMap.get(wordCategory);

        // iterate over all pairs of synsets, avoiding processing any pair twice
        for (int i = 0; i < hypernymDistances.size(); i++) {
            Synset synset1 = hypernymDistances.get(i);
            int synset1MaxHypernymDist = synset1.getMaxDistance();

            // not possible for this synset to be one of the longest
            if (maxHypernymDistance + synset1MaxHypernymDist < longestDistance) {
                continue;
            }

            // start at i+1 to avoid double processing
            for (int j = i + 1; j < hypernymDistances.size(); j++) {
                Synset synset2 = hypernymDistances.get(j);

                // not possible for these two synsets to have a path longer than
                // the longest distance so far
                if (synset2.getMaxDistance() + synset1MaxHypernymDist < longestDistance) {
                    continue;
                }

                // find the least common subsumers for these two synsets
                Set<LeastCommonSubsumer> lcsSet = synset1.getLeastCommonSubsumers(synset2);
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
        return leastCommonSubsumers;
    }

    // ToDo: javadoc
    public int getDistanceBetweenSynsets(Synset fromSynset, Synset toSynset) {
        return (fromSynset.getDistanceToSynset(toSynset));
    }

    // ToDo: javadoc
    /**
    public List<Synset> getPathBetweenSynsets(Synset fromSynset, Synset toSynset) {
        List<Synset> path = new ArrayList<>();

    }
     */

    /**
     * A simple relatedness measure based on the distance between two nodes and
     * the longest possible 'shortest path' between any two Synsets in GermaNet:<br>
     *
     * <code>rel(s1,s2) = (MAX_SHORTEST_PATH - distance(s2,s2)) / MAX_SHORTEST_PATH</code><br><br>
     *
     * If normalizedMax is > 0, then synsets that are very similar will be close to that
     * value, and dissimilar synsets will have a value close to 0.0.<br><br>
     *
     * Synsets must be in the same WordCategory. This implementation uses the
     * MAX_SHORTEST_PATH for the WordCategory that the input synsets belong to.
     *
     * @param s1 first Synset
     * @param s2 second Synset
     * @param normalizedMax value to use for maximal similarity (raw value is returned if <= 0)
     * @return The similarity using a simple algorithm with optional normalization, or -1 if
     * the synsets do not belong to the same WordCategory.
     */
    double getSimilarityPath(Synset s1, Synset s2,  int normalizedMax) {
        if ((s1 == null) || (s2 == null) || ! s2.inWordCategory(s1.getWordCategory())) {
            return -1;
        }

        // the longest LCS for any two synsets in GermaNet for the WordCategory that
        // s1 and s2 belong to
        Set<LeastCommonSubsumer> lcsSet = catLongestLCSMap.get(s1.getWordCategory());

        // all LCS's have the same distance, just get the first one
        int maxShortestPathLength = lcsSet.iterator().next().getDistance();

        int pathLength = s1.getDistanceToSynset(s2);

        double rawValue = (maxShortestPathLength - pathLength) / (double) maxShortestPathLength;
        return (normalizedMax > 0) ?  normalize(s1.getWordCategory(), SemRelMeasure.Path, rawValue, normalizedMax) : rawValue;
    }

    double getSimilarityWuAndPalmer(Synset s1, Synset s2,  int normalizedMax) {
        Set<LeastCommonSubsumer> lcsSet;
        int maxLCSdistToRoot = 0;

        lcsSet = s1.getLeastCommonSubsumers(s2);
        for (LeastCommonSubsumer lcs : lcsSet) {
            Synset lcsSynset = gnet.getSynsetByID(lcs.getLcsID());
            int lcsSynsetDistToRoot = lcsSynset.getDistanceToHypernym(GermaNet.GNROOT_ID);
            if (lcsSynsetDistToRoot > maxLCSdistToRoot) {
                maxLCSdistToRoot = lcsSynsetDistToRoot;
            }
        }

        int pathLength = s1.getDistanceToSynset(s2);
        double doubleMaxLCSdistToRoot = 2.0 * maxLCSdistToRoot;
        double rawValue = doubleMaxLCSdistToRoot / (pathLength * doubleMaxLCSdistToRoot);
        return (normalizedMax > 0) ?  normalize(s1.getWordCategory(), SemRelMeasure.WuAndPalmer, rawValue, normalizedMax) : rawValue;
    }

    /**
     * Relatedness according to Leacock&Chodorow, 1998: "Combining Local Context
     * and WordNet Relatedness for Word Sense Identification".<br>
     * <p>
     * Since GermaNet has a unique root node, the formula can be simplified to:
     * <code>rel(s1,s2) = -log(pathlength/2D)</code><br>
     * <p>
     * pathlength = number of nodes on the shortest path from s1 to s2<br>
     * D = max depth of taxonomy using node counting: maxDepth = 20 for nouns, 15 for verbs, and 10 for adjectives
     * in release 14.0 of the GermaNet data. It is possible that these values are slightly different for other data
     * releases.<br>
     * If normalizedMax is > 0, then synsets that are very similar will be close to that
     * value, and dissimilar synsets will have a value close to 0.0.<br><br>
     *
     * Synsets must be in the same WordCategory.
     *
     * @param s1 first synset to be compared
     * @param s2 second synset to be compared
     * @param normalizedMax value to use for maximal similarity (raw value is returned if <= 0)
     * @return The similarity using the Leacock and Chodorow algorithm with optional normalization, or -1 if
     * the synsets do not belong to the same WordCategory.
     */
    double getSimilarityLeacockChodorow(Synset s1, Synset s2, int normalizedMax) {
        if ((s1 == null) || (s2 == null) || ! s2.inWordCategory(s1.getWordCategory())) {
            return -1.0;
        }

        // maxDepth and pathLength represent the number of edges
        // add 1 to get the number of nodes
        int maxDepth = catMaxDepthMap.get(s1.getWordCategory()) +1;

        // the distance, using hypernym relations, between s1 and s2
        int pathLength = s1.getDistanceToSynset(s2) +1;

        double rawValue = -Math.log(pathLength / (2.0*maxDepth));
        return (normalizedMax > 0) ?  normalize(s1.getWordCategory(), SemRelMeasure.LeacockAndChodorow, rawValue, normalizedMax) : rawValue;
    }

    /**
     * Longest LCS were calculated when the data was loaded, and passed to this constructor. Just return the value
     * for the given WordCategory.
     *
     * @param wordCategory WordCategory to get the longest LCS of
     * @return the longest shortest path(s) between any two synsets of the same WordCategory in GermaNet.
     */
    Set<LeastCommonSubsumer> getLongestLeastCommonSubsumer(WordCategory wordCategory) {
        return catLongestLCSMap.get(wordCategory);
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
    public Set<LeastCommonSubsumer> getLeastCommonSubsumer(Synset synset1, Synset synset2) {
        return synset1.getLeastCommonSubsumers(synset2);
    }
}
