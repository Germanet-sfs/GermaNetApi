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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private Map<WordCategory, Map<Integer, Double>> catICMap;

    // used only during construction to generate IC maps
    private Map<WordCategory, Map<String, Long>> formFreqMaps;
    private Map<WordCategory, Map<Integer, Long>> individualFreqMaps;
    private Map<WordCategory, Map<Integer, Long>> cumulativeFreqMaps;

    private GermaNet gnet;

    SemanticUtils(Map<WordCategory, Integer> catMaxHypernymDistanceMap, GermaNet gnet,
                  File nounFreqFile, File verbFreqFile, File adjFreqFile) throws IOException {
        this.catMaxHypernymDistanceMap = catMaxHypernymDistanceMap;
        this.gnet = gnet;
        catMaxDepthMap = new HashMap<>(WordCategory.values().length);
        catNormalizationMap = new HashMap<>(WordCategory.values().length);
        catICMap = new HashMap<>(WordCategory.values().length);
        formFreqMaps = new HashMap<>(WordCategory.values().length);
        individualFreqMaps = new HashMap<>(WordCategory.values().length);
        cumulativeFreqMaps = new HashMap<>(WordCategory.values().length);

        long startTime = System.currentTimeMillis();
        LOGGER.info("Initializing SemanticUtils object...");
        initCatLongestLCSMap();
        initMaxDepthMap();

        LOGGER.info("Initializing normalization values for Path algorithms...");
        initPathNormalizationValues();

        LOGGER.info("Loading frequency lists...");
        formFreqMaps.put(WordCategory.nomen, loadFreqData(nounFreqFile));
        formFreqMaps.put(WordCategory.verben, loadFreqData(verbFreqFile));
        formFreqMaps.put(WordCategory.adj, loadFreqData(adjFreqFile));

        // initialize individual frequency maps
        LOGGER.info("Calculating individual frequencies...");
        initIndividualFreqMaps();


        // calculate cumulative frequency of each synset
        LOGGER.info("Calculating cumulative frequencies...");
        initCumulativeFreqMaps();

        // calculate the Information Content of each synset and normalization values for each word category
        LOGGER.info("Calculating Information Content values and normalization values...");
        initICMaps();

        // Delete maps that are not needed any more
        formFreqMaps = null;
        individualFreqMaps = null;
        cumulativeFreqMaps = null;

        long endTime = System.currentTimeMillis();
        double processingTime = (double) (endTime - startTime) / 1000;
        LOGGER.info("Done initializing SemanticUtils object ({} seconds).", processingTime);
    }

    private void initICMaps() throws IOException {

        for (WordCategory wordCategory : WordCategory.values()) {
            // calculate the Information Content of each synset in wordCategory
            // keep track of max IC values for the normalization map
            // min IC is always 0.0 (value for root)

            Map<Integer, Double> icMap = new HashMap<>();
            Map<Integer, Long> cumulativeFreqMap = cumulativeFreqMaps.get(wordCategory);
            List<Synset> synsetsByWordCat = gnet.getSynsets(wordCategory);
            Long cumFreqRoot = cumulativeFreqMap.get(GermaNet.GNROOT_ID);

            Double maxIC = Double.MIN_VALUE;

            for (Synset synset : synsetsByWordCat) {
                int synsetID = synset.getId();
                Long cumulativeFreq = cumulativeFreqMap.get(synsetID);

                // calculate the IC for this synset
                Double ic = null;
                ic = -Math.log10((double) cumulativeFreq / cumFreqRoot);

                // update max IC if this IC is larger than any seen so far
                if (Double.compare(ic, maxIC) > 0) {
                    maxIC = ic;
                }

                icMap.put(synsetID, ic);
            }

            // the root node will always have IC = 0.0
            // add an entry for root to the icMap for each WordCategory
            icMap.put(GermaNet.GNROOT_ID, 0.0);
            catICMap.put(wordCategory, icMap);

            // for each of the IC measures,
            // put the min/max IC values in the normalization map for this wordCategory
            Map<SemRelMeasure, List<Double>> normMap = catNormalizationMap.get(wordCategory);

            // Resnik: min 0.0, max maxIC
            List<Double> minMaxList = new ArrayList<>();
            minMaxList.add(0.0);
            minMaxList.add(maxIC);
            normMap.put(SemRelMeasure.Resnik, minMaxList);

            // JiangAndConrath: min 0.0, max 2 * -log10(1.0 / cumFreqRoot)
            minMaxList = new ArrayList<>();
            Double min = 0.0;
            Double max = 2 * -Math.log10(1.0 / cumFreqRoot);
            minMaxList.add(min);
            minMaxList.add(max);
            normMap.put(SemRelMeasure.JiangAndConrath, minMaxList);

            // Lin: min 0.0, max 1.0
            minMaxList = new ArrayList<>();
            min = 0.0;
            max = 1.0;
            minMaxList.add(min);
            minMaxList.add(max);
            normMap.put(SemRelMeasure.Lin, minMaxList);
            catNormalizationMap.put(wordCategory, normMap);
        }
    }

    private Map<String, Long> loadFreqData(File file) throws IOException {
        Map<String, Long> freqMap = new HashMap<>();
        Files.lines(Paths.get(file.getPath()))
                .map(line -> line.split("\\s+"))
                .forEach(splits -> {
                    String word = splits[0];
                    Long freq = Long.valueOf(splits[1]);
                    Long curFreq = freqMap.get(word);
                    if (curFreq != null) {
                        freq += curFreq;
                    }
                    freqMap.put(word, freq);
                });
        return freqMap;
    }

    private void initIndividualFreqMaps() {

        for (WordCategory wordCategory : WordCategory.values()) {
            Map<Integer, Long> individualFreqMap = new HashMap<>();
            List<Synset> synsetsByWordCat = gnet.getSynsets(wordCategory);
            Map<String, Long> formFreqMap = formFreqMaps.get(wordCategory);

            for (Synset synset : synsetsByWordCat) {
                Integer synsetID = synset.getId();

                Long totalFreq = 1L;
                List<String> allOrthForms = synset.getAllOrthForms();
                for (String orthForm : allOrthForms) {
                    Long orthFormFreq = formFreqMap.get(orthForm);
                    if (orthFormFreq != null) {
                        totalFreq += orthFormFreq;
                    }
                }
                individualFreqMap.put(synsetID, totalFreq);
            }

            // add freq of 1 for root
            individualFreqMap.put(GermaNet.GNROOT_ID, 1L);

            individualFreqMaps.put(wordCategory, individualFreqMap);
        }
    }

    private void initCumulativeFreqMaps() {

        for (WordCategory wordCategory : WordCategory.values()) {

            Map<Integer, Long> cumulativeFreqMap = new HashMap<>();
            List<Synset> synsetsByWordCat = gnet.getSynsets(wordCategory);

            for (Synset synset : synsetsByWordCat) {
                Integer synsetID = synset.getId();

                // ROOT is a special case that is handled separately
                // its word category is noun, but it has hyponyms of all word categories
                if (synsetID == GermaNet.GNROOT_ID) {
                    continue;
                }
                if (cumulativeFreqMap.get(synsetID) == null) {
                    calcCumulativeSynsetFreq(synset, cumulativeFreqMap);
                }
            }

            // add an entry for ROOT
            // count only cum freqs of hyponyms with the same word category
            List<Synset>  rootHyponyms = gnet.getSynsetByID(GermaNet.GNROOT_ID).getRelatedSynsets(ConRel.has_hyponym);
            Long rootCumFreq = 1L;
            for (Synset hyponym : rootHyponyms) {
                if (hyponym.getWordCategory() == wordCategory) {
                    rootCumFreq += cumulativeFreqMap.get(hyponym.getId());
                }
            }
            cumulativeFreqMap.put(GermaNet.GNROOT_ID, rootCumFreq);

            cumulativeFreqMaps.put(wordCategory, cumulativeFreqMap);
            LOGGER.info("Cumulative frequency for root ({}): {}", wordCategory, cumulativeFreqMap.get(GermaNet.GNROOT_ID));
        }
    }

    // recursively fill in the cumulativeFreqMap
    private Long calcCumulativeSynsetFreq(Synset synset, Map<Integer, Long> cumulativeFreqMap) {

        // See if we already calculated the cumulative frequency of the synset
        Integer synsetID = synset.getId();
        Long cumulativeFreq = cumulativeFreqMap.get(synsetID);
        if (cumulativeFreq != null) {
            return cumulativeFreq;
        }

        // Calculate and store cumulative freq for this synset (ie freq of this synset plus all its hyponyms (transitive))
        Map<Integer, Long> individualFreqMap = individualFreqMaps.get(synset.getWordCategory());
        Long individualFreq = individualFreqMap.get(synsetID); // this should never be null

        // include synset's own freq in its cumulativeFreq
        cumulativeFreq = individualFreq;
        List<Synset> hyponyms = synset.getRelatedSynsets(ConRel.has_hyponym);
        for (Synset hyponym : hyponyms) {
            cumulativeFreq += calcCumulativeSynsetFreq(hyponym, cumulativeFreqMap);
        }
        cumulativeFreqMap.put(synsetID, cumulativeFreq);

        return cumulativeFreq;
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
     * Initialize the normalization values for each word category and each path-based relatedness measure.
     */
    private void initPathNormalizationValues() {
        for (WordCategory wordCategory : WordCategory.values()) {
            Map<SemRelMeasure, List<Double>> normalizeMinMaxValues = new HashMap<>();

            for (SemRelMeasure semRelMeasure : SemRelMeasure.values()) {
                switch (semRelMeasure) {
                    case SimplePath:
                    case LeacockAndChodorow:
                    case WuAndPalmer:
                        // min at index0, max at index1
                        List<Double> minMax = new ArrayList<>(2); // min at index 0, max at index 1
                        Synset synset1;
                        Synset synset2;
                        double minVal = 0;
                        double maxVal = 1;

                        // Maximally distant synsets using path algorithms are those with the longest LCSs
                        List<Integer> maxDistantSynsetIds = Lists.newArrayList(catLongestLCSMap.get(wordCategory).iterator().next().getFromToSynsetIDs());
                        synset1 = gnet.getSynsetByID(maxDistantSynsetIds.get(0));
                        synset2 = gnet.getSynsetByID(maxDistantSynsetIds.get(1));

                        minVal = getSimilarity(semRelMeasure, synset1, synset2, 0); // least similar, lowest value, not normalized
                        maxVal = getSimilarity(semRelMeasure, synset1, synset1, 0); // most similar, highest value, not normalized

                        minMax.add(minVal);
                        minMax.add(maxVal);
                        normalizeMinMaxValues.put(semRelMeasure, minMax);

                        catNormalizationMap.put(wordCategory, normalizeMinMaxValues);
                        break;

                    default:
                        // IC measures fill the normalization maps during initialization
                        break;
                }
            }
        }
    }

    /**
     * Normalize the relatedness value, taking into consideration the WordCategory, algorithm,
     * and lower and upper bounds.
     *
     * @param wordCategory  the WordCategory
     * @param semRelMeasure the algorithm
     * @param rawValue      raw value of the algorithm
     * @param normalizedMax upper bound on normalization range (lower bound is 0)
     * @return the normalized value, taking into consideration the WordCategory, algorithm, and requested upper bound.
     * If rawValue is null, this method returns null.
     */
    private Double normalize(WordCategory wordCategory, SemRelMeasure semRelMeasure, Double rawValue, int normalizedMax) {
        if (rawValue == null)
            return null;
        double minVal = catNormalizationMap.get(wordCategory).get(semRelMeasure).get(0);
        double maxVal = catNormalizationMap.get(wordCategory).get(semRelMeasure).get(1);

        return ((rawValue - minVal) / (maxVal - minVal)) * normalizedMax;
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
        List<Synset> synsetsByCat = gnet.getSynsets(wordCategory);
        Collections.sort(synsetsByCat, (s1, s2) -> (s2.getMaxDistance() - s1.getMaxDistance()));

        // longest distance between 2 synsets found so far
        int longestDistance = 0;
        // longest path between a synset and any of its hypernyms in the graph
        int maxHypernymDistance = catMaxHypernymDistanceMap.get(wordCategory);

        // iterate over all pairs of synsets, avoiding processing any pair twice
        for (int i = 0; i < synsetsByCat.size(); i++) {
            Synset synset1 = synsetsByCat.get(i);
            int synset1MaxHypernymDist = synset1.getMaxDistance();

            // not possible for this synset to be one of the longest
            if (maxHypernymDistance + synset1MaxHypernymDist < longestDistance) {
                continue;
            }

            // start at i+1 to avoid double processing
            for (int j = i + 1; j < synsetsByCat.size(); j++) {
                Synset synset2 = synsetsByCat.get(j);

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

    /**
     * Get the shortest distance to between Synsets using hypernym / hyponym relations only. Both
     * Synsets must belong to the same WordCategory.
     *
     * @param synset1 one Synset
     * @param synset2 another Synset
     * @return The distance between the Synsets, or null if both do not belong to the
     * same WordCategory.
     */
    public Integer getDistanceBetweenSynsets(Synset synset1, Synset synset2) {
        if ((synset1 == null) || (synset2 == null) || !synset1.inWordCategory(synset2.getWordCategory())) {
            return null;
        }
        return (synset1.getDistanceToSynset(synset2));
    }

    /**
     * Get the set of all shortest paths between two Synsets, using hypernym/hyponym relations. Both synsets
     * must belong to the same WordCategory.
     *
     * @param fromSynset a synset
     * @param toSynset another synset
     * @return the set of all shortest paths between two Synsets, using hypernym/hyponym relations, or null
     * if the synsets belong to different Word Categories.
     */
    public Set<SynsetPath> getPathsBetweenSynsets(Synset fromSynset, Synset toSynset) {
        if ((fromSynset == null) || (toSynset == null) || !fromSynset.inWordCategory(toSynset.getWordCategory())) {
            return null;
        }

        Set<SynsetPath> paths = new HashSet<>();
        Set<LeastCommonSubsumer> lcsSet = fromSynset.getLeastCommonSubsumers(toSynset);

        for (LeastCommonSubsumer lcs : lcsSet) {
            List<List<Synset>> fromSynsetToLcsPaths = getPathToHypernym(fromSynset, lcs.getLcsID(), fromSynset.getDistanceToHypernym(lcs.getLcsID()));
            List<List<Synset>> toSynsetToLcsPaths = getPathToHypernym(toSynset, lcs.getLcsID(), toSynset.getDistanceToHypernym(lcs.getLcsID()));

            for (List<Synset> fromSynsetPath : fromSynsetToLcsPaths) {
                for (List<Synset> toSynsetPath : toSynsetToLcsPaths) {
                    paths.add(new SynsetPath(fromSynset, toSynset, lcs.getLcsID(), fromSynsetPath, toSynsetPath));
                }
            }
        }
        return paths;
    }

    /**
     * Helper method for getPathsBetweenSynsets. Get the path from this Synset to one of it's hypernyms,
     * or null if the hypernym (lcsId) is not a hypernym of synset or if lcsId is farther than maxDistance
     * from synset.
     *
     * @param synset start synset
     * @param lcsId end synset, which should be a hypernym of synset
     * @param maxDistance maximum distance between synset and hypernym (lcsId)
     * @return the shortest path between synset and hypernym with ID lcsId, or null if lcsId is not
     * a hypernym of synset or if lcsId is farther than maxDistance from synset.
     */
    private List<List<Synset>> getPathToHypernym(Synset synset, Integer lcsId, int maxDistance) {

        // the synset is not on the shortest path to lcs
        Integer synsetLcsDistance = synset.getDistanceToHypernym(lcsId);
        if (synsetLcsDistance == null || synsetLcsDistance > maxDistance) {
            return null;
        }

        List<List<Synset>> rval = new ArrayList<>();
        List<Synset> path = new LinkedList<>();

        // got to the lcs, we're done
        if (synset.getId() == lcsId) {
            path.add(synset);
            rval.add(path);
        } else {
            path.add(synset);

            // process each direct hypernym of this synset
            for (Synset hypernym : synset.getRelatedSynsets(ConRel.has_hypernym)) {
                List<List<Synset>> paths = getPathToHypernym(hypernym, lcsId, maxDistance - 1);
                if (paths != null && !paths.isEmpty()) {
                    for (List<Synset> partialPath : paths) {
                        partialPath.addAll(0, path);
                        rval.add(partialPath);
                    }
                }
            }
        }
        return rval;
    }

    /**
     * Convenience method for calling the various similarity methods.
     *
     * @param semRelMeasure similarity algorithm to use
     * @param s1 first synset
     * @param s2 second synset
     * @param normalizedMax value to use for maximal similarity (raw value is returned if &lt;= 0)
     * @return The similarity using the algorithm selected - with optional normalization, or null if
     * similarity cannot be computed. See documentation of each algorithm for more information.
     */
    public Double getSimilarity(SemRelMeasure semRelMeasure, Synset s1, Synset s2, int normalizedMax) {
        Double rval;

        switch (semRelMeasure) {
            case SimplePath:
                rval = getSimilaritySimplePath(s1, s2, normalizedMax);
                break;

            case LeacockAndChodorow:
                rval = getSimilarityLeacockChodorow(s1, s2, normalizedMax);
                break;

            case WuAndPalmer:
                rval = getSimilarityWuAndPalmer(s1, s2, normalizedMax);
                break;

            case Resnik:
                rval = getSimilarityResnik(s1, s2, normalizedMax);
                break;

            case JiangAndConrath:
                rval = getSimilarityJiangAndConrath(s1, s2, normalizedMax);
                break;

            case Lin:
                rval = getSimilarityLin(s1, s2, normalizedMax);
                break;

            default:
                rval = null;
        }
        return rval;
    }

    /**
     * A simple relatedness measure based on the distance between two nodes and
     * the longest possible 'shortest path' between any two Synsets in GermaNet:<br>
     *
     * <code>rel(s1,s2) = (MAX_SHORTEST_PATH - distance(s2,s2)) / MAX_SHORTEST_PATH</code><br><br>
     * <p>
     * If normalizedMax is &gt; 0, then synsets that are very similar will be close to that
     * value, and dissimilar synsets will have a value close to 0.0.<br><br>
     * <p>
     * Synsets must be in the same WordCategory. This implementation uses the
     * MAX_SHORTEST_PATH for the WordCategory that the input synsets belong to.
     *
     * @param s1            first Synset
     * @param s2            second Synset
     * @param normalizedMax value to use for maximal similarity (raw value is returned if &lt;= 0)
     * @return The similarity using a simple algorithm with optional normalization, or null if
     * the synsets do not belong to the same WordCategory.
     */
    public Double getSimilaritySimplePath(Synset s1, Synset s2, int normalizedMax) {
        if ((s1 == null) || (s2 == null) || !s2.inWordCategory(s1.getWordCategory())) {
            return null;
        }

        // the longest LCS for any two synsets in GermaNet for the WordCategory that
        // s1 and s2 belong to
        Set<LeastCommonSubsumer> lcsSet = catLongestLCSMap.get(s1.getWordCategory());

        // all LCS's have the same distance, just get the first one
        int maxShortestPathLength = lcsSet.iterator().next().getDistance();

        int pathLength = s1.getDistanceToSynset(s2);

        double rawValue = (maxShortestPathLength - pathLength) / (double) maxShortestPathLength;
        return (normalizedMax > 0) ? normalize(s1.getWordCategory(), SemRelMeasure.SimplePath, rawValue, normalizedMax) : rawValue;
    }


    /**
     * Relatedness/Similarity according to Wu and Palmer, 1994: "Verb Semantics
     * and Lexical Selection"<br>
     * <p>
     * ConSim(S1, S2) =  (2*D3) / (L1 + L2 + 2*D3)<br>
     * <p>
     * S1, S2: two synsets<br>
     * S3: least common subsumer (LCS) of S1 and S2<br>
     * L1 = path length S1,S3<br>
     * L2 = path length S2,S3<br>
     * D3 = depth (distance from ROOT) of S3<br>
     * <p>
     * If multiple LCS's exist, the one farthest from ROOT is used.
     * Synsets must be in the same WordCategory.
     *
     * @param s1 first synset
     * @param s2 second synset
     * @param normalizedMax value to use for maximal similarity (raw value is returned if &lt;= 0)
     * @return The similarity using the Wu and Palmer algorithm with optional normalization, or null if
     * the synsets do not belong to the same WordCategory.
     */
    public Double getSimilarityWuAndPalmer(Synset s1, Synset s2, int normalizedMax) {

        if ((s1 == null) || (s2 == null) || !s2.inWordCategory(s1.getWordCategory())) {
            return null;
        }

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
        double rawValue = doubleMaxLCSdistToRoot / (pathLength + doubleMaxLCSdistToRoot);
        return (normalizedMax > 0) ? normalize(s1.getWordCategory(), SemRelMeasure.WuAndPalmer, rawValue, normalizedMax) : rawValue;
    }

    /**
     * Relatedness according to Leacock&amp;Chodorow, 1998: "Combining Local Context
     * and WordNet Relatedness for Word Sense Identification".<br>
     * <p>
     * Since GermaNet has a unique root node, the formula can be simplified to:
     * <code>rel(s1,s2) = -log(pathlength/2D)</code><br>
     * <p>
     * pathlength = number of nodes on the shortest path from s1 to s2<br>
     * D = max depth of taxonomy using node counting. Max depth is calculated for each Word Category
     * when the data is loaded. The max depth values for release 14.0 of the GermaNet data are: 20 for nouns,
     * 15 for verbs, and 10 for adjectives. It is possible that these values are slightly different for other data
     * releases.<br>
     * If normalizedMax is &gt; 0, then synsets that are very similar will be close to that
     * value, and dissimilar synsets will have a value close to 0.0.<br><br>
     * <p>
     * Synsets must be in the same WordCategory.
     *
     * @param s1            first synset to be compared
     * @param s2            second synset to be compared
     * @param normalizedMax value to use for maximal similarity (raw value is returned if &lt;= 0)
     * @return The similarity using the Leacock and Chodorow algorithm with optional normalization, or null if
     * the synsets do not belong to the same WordCategory.
     */
    public Double getSimilarityLeacockChodorow(Synset s1, Synset s2, int normalizedMax) {
        if ((s1 == null) || (s2 == null) || !s2.inWordCategory(s1.getWordCategory())) {
            return null;
        }

        // maxDepth and pathLength represent the number of edges
        // add 1 to get the number of nodes
        int maxDepth = catMaxDepthMap.get(s1.getWordCategory()) + 1;

        // the distance, using hypernym relations, between s1 and s2
        int pathLength = s1.getDistanceToSynset(s2) + 1;

        double rawValue = -Math.log10(pathLength / (2.0 * maxDepth));
        return (normalizedMax > 0) ? normalize(s1.getWordCategory(), SemRelMeasure.LeacockAndChodorow, rawValue, normalizedMax) : rawValue;
    }

    /**
     * Relatedness according to Resnik 1995: "Using Information Content to
     * Evaluate Semantic Relatedness in a Taxonomy".<br>
     *
     * This algorithm uses the Information Content (IC) of synsets to determine similarity.
     * The IC of a synset is based on the frequency of the <code>Synset</code>'s
     * orthForms in a large corpus, and the frequencies of the <code>Synset</code>'s hyponyms.
     * <code>Synset</code>s for more general terms have a much higher cumulative frequency
     * than <code>Synset</code>s for specific terms. The IC of a <code>Synset</code> is
     * calculated by taking negative log10 of (cumFreq of synset) / (cumFreq of root) <br>
     * <br>
     *     IC(S) = -log10(cumFreq(S) / cumFreq(root))
     * <br>
     *
     * This method returns the IC value Least Common Subsumer (LCS) of the two input <code>Synset</code>s.
     * <br>
     * If more than one LCS exists, the highest IC value is returned.
     * <br>
     * Note that with Resnik's measure, it is possible for a synset to be 'more
     * related' to a different synset (one with a larger IC)
     * than to itself. <br>
     * <br>
     * If normalizedMax is &gt; 0, then synsets that are very similar will be close to that
     * value, and dissimilar synsets will have a value close to 0.0.<br><br>
     * <p>
     * Synsets must be in the same WordCategory.
     * <p>
     * @param s1 first synset to be compared
     * @param s2 second synset to be compared
     * @param normalizedMax value to use for maximal similarity (raw value is returned if &lt;= 0)
     * @return The similarity using the Resnik algorithm with optional normalization, or null if
     * the synsets do not belong to the same WordCategory, or if there is not enough information to
     * calculate the similarity.
     */
    public Double getSimilarityResnik(Synset s1, Synset s2, int normalizedMax) {
        if ((s1 == null) || (s2 == null) || !s2.inWordCategory(s1.getWordCategory())) {
            return null;
        }

        Double maxIC = getMaxICofLCSs(s1, s2);
        return (normalizedMax > 0) ? normalize(s1.getWordCategory(), SemRelMeasure.Resnik, maxIC, normalizedMax) : maxIC;
    }

    /**
     * Relatedness according to Jiang and Conrath 1997: "Semantic Relatedness
     * Based on Corpus Statistics and Lexical Taxonomy"<br>
     *
     * The distance measure presented in the paper is turned into a relatedness
     * measure by subtracting the distance measure from the maximum possible 'distance' between
     * two synsets in the wordnet.<br>
     *
     * <code>rel(s1,s2) = max_dist - dist(s1,s2)</code><br>
     * <code>dist(s1,s2) = IC(s1) + IC(s2) - 2 * IC(LCS)</code><br>
     * <code>max_dist = 2 * -log(1 / rootFrequency) = 2 * max_IC</code><br>
     *
     * If normalizedMax is &gt; 0, then synsets that are very similar will be close to that
     * value, and dissimilar synsets will have a value close to 0.0.<br><br>
     *
     * Synsets must be in the same WordCategory.
     *
     * @param s1 first synset to be compared
     * @param s2 second synset to be compared
     * @param normalizedMax value to use for maximal similarity (raw value is returned if &lt;= 0)
     * @return The similarity using the Jiang and Conrath algorithm with optional normalization, or null if
     * the synsets do not belong to the same WordCategory, or if there is not enough information to
     * calculate the similarity.
     */
    public Double getSimilarityJiangAndConrath(Synset s1, Synset s2, int normalizedMax) {
        if ((s1 == null) || (s2 == null) || !s2.inWordCategory(s1.getWordCategory())) {
            return null;
        }

        WordCategory cat = s1.getWordCategory();
        Map<Integer, Double> icMap = catICMap.get(cat);
        Double lcsIC = getMaxICofLCSs(s1, s2);

        double icS1 = icMap.get(s1.getId());
        double icS2 = icMap.get(s2.getId());
        double jcnMaxDist = catNormalizationMap.get(cat).get(SemRelMeasure.JiangAndConrath).get(1);
        double jcnDist = icS1 + icS2 - (2 * lcsIC);

        double sim = jcnMaxDist - jcnDist;
        return (normalizedMax > 0) ? normalize(s1.getWordCategory(), SemRelMeasure.JiangAndConrath, sim, normalizedMax) : sim;
    }

    /**
     * Relatedness according to Lin 1998: "An Information-Theoretic Definition of Similarity"<br>
     *
     * <code>sim(s1,s2) = (2 * IC(LCS)) / (IC(s1) + IC(s2))</code><br>
     *
     * If normalizedMax is &gt; 0, then synsets that are very similar will be close to that
     * value, and dissimilar synsets will have a value close to 0.0.<br><br>
     *
     * Synsets must be in the same WordCategory.
     *
     * @param s1 first synset to be compared
     * @param s2 second synset to be compared
     * @param normalizedMax value to use for maximal similarity (raw value is returned if &lt;= 0)
     * @return The similarity using the Lin algorithm with optional normalization, or null if
     * the synsets do not belong to the same WordCategory, or if there is not enough information to
     * calculate the similarity.
     */
    public Double getSimilarityLin(Synset s1, Synset s2, int normalizedMax) {
        if ((s1 == null) || (s2 == null) || !s2.inWordCategory(s1.getWordCategory())) {
            return null;
        }

        WordCategory cat = s1.getWordCategory();
        Map<Integer, Double> icMap = catICMap.get(cat);
        Double lcsIC = getMaxICofLCSs(s1, s2);

        double icS1 = icMap.get(s1.getId());
        double icS2 = icMap.get(s2.getId());

        double sim = (2 * lcsIC) / (icS1 + icS2);
        return (normalizedMax > 0) ? normalize(s1.getWordCategory(), SemRelMeasure.Lin, sim, normalizedMax) : sim;
    }


    /**
     * Return the maximum Information Content (IC) value of the Least Common Subsumer(s) of s1 and s2.
     * Helper method for the IC-based similarity measures.
     *
     * @param s1 first synset
     * @param s2 second synset
     * @return the maximum Information Content (IC) value of the Least Common Subsumer(s) of s1 and s2.
     */
    private Double getMaxICofLCSs(Synset s1, Synset s2) {
        Set<LeastCommonSubsumer> leastCommonSubsumers = getLeastCommonSubsumers(s1, s2);
        Map<Integer, Double> icMap = catICMap.get(s1.getWordCategory());
        Double curIC;
        Double prevIC = null;
        Double maxIC = null;
        Double epsilon = 0.00001;

        for (LeastCommonSubsumer leastCommonSubsumer : leastCommonSubsumers) {
            int lcsID = leastCommonSubsumer.getLcsID();
            curIC = icMap.get(lcsID);

            if (curIC != null) {
                if (prevIC == null) {
                    maxIC = curIC;
                } else {
                    double diff = curIC - prevIC;
                    if (diff > epsilon) {
                        maxIC = curIC;
                    }
                }
                prevIC = curIC;
            }
        }
        return maxIC;
    }

    /**
     * Longest LCS were calculated when the data was loaded, and passed to this constructor. Just return the value
     * for the given WordCategory.
     *
     * @param wordCategory WordCategory to get the longest LCS of
     * @return the longest shortest path(s) between any two synsets of the same WordCategory in GermaNet.
     */
    Set<LeastCommonSubsumer> getLongestLeastCommonSubsumers(WordCategory wordCategory) {
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
    public Set<LeastCommonSubsumer> getLeastCommonSubsumers(Synset synset1, Synset synset2) {
        return synset1.getLeastCommonSubsumers(synset2);
    }
}
