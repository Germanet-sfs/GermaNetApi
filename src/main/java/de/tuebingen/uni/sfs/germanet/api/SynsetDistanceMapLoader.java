package de.tuebingen.uni.sfs.germanet.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 * @author Ben Campbell, Seminar für Sprachwissenschaft, Universität Tübingen
 */
class SynsetDistanceMapLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynsetDistanceMapLoader.class);
    private GermaNet gnet;
    private Map<WordCategory, Set<LeastCommonSubsumer>> catLongestLCSMap;
    private Map<WordCategory, Integer> catMaxHypernymDistanceMap;

    protected SynsetDistanceMapLoader(GermaNet gnet) {
        this.gnet = gnet;
        catLongestLCSMap = new HashMap<>(WordCategory.values().length);
        catMaxHypernymDistanceMap = new HashMap<>(WordCategory.values().length);
    }

    /**
     * Add a map to each Synset containing the shortest distance to each of its hypernyms on all
     * paths from the synset to root. Finds the longest path between two synsets (using
     * hypernym relations only), for later use in calculating the longest least common
     * subsumer.
     */
    void loadDistanceMaps() {
        LOGGER.info("Loading distance maps...");
        Synset farthest = null;

        // create a separate map for each word category
        for (WordCategory wordCategory : WordCategory.values()) {
            List<Synset> synsetList = gnet.getSynsets(wordCategory);

            //longest path between any synset and any of its hypernyms
            int maxHypernymDistance = 0;
            for (Synset synset : synsetList) {

                // recursively find the shortest distances from this synset to all of its hypernyms
                buildHypernymTree(synset, synset, 0);

                // update maxHypernymDistance if necessary
                int synsetMaxDistance = synset.getMaxDistance();
                if (synsetMaxDistance > maxHypernymDistance) {
                    maxHypernymDistance = synsetMaxDistance;
                    farthest = synset;
                }
            }
            catMaxHypernymDistanceMap.put(wordCategory, maxHypernymDistance);
            LOGGER.info("farthest synset from root: {}", farthest);
        }
        LOGGER.info("Done loading distance maps.");
    }

    /**
     * Recursively calculate the shortest distance from synset to all of its hypernyms up to root.
     *
     * @param synset the synset currently being processed
     * @param hypernymOfSynset     one of synset's hypernyms on the path up to root
     * @param depth      distance from synset to hypernymOfSynset
     */
    private void buildHypernymTree(Synset synset, Synset hypernymOfSynset, int depth) {
        // move up one level towards root
        List<Synset> hypernymList = hypernymOfSynset.getRelatedSynsets(ConRel.has_hypernym);
        depth++;

        // process each direct hypernym of this hypernymOfSynset
        for (Synset hypernym : hypernymList) {

            int hypernymID = hypernym.getId();
            Integer distanceToHypernym = synset.getDistanceToHypernym(hypernymID);

            if (distanceToHypernym == null) {
                // this hypernym has not been seen before by this synset, add it
                synset.putDistanceMap(hypernymID, depth);
                synset.addHypernymId(hypernymID);
                // update this synset's max distance to any of its hypernyms, if necessary
                if (depth > synset.getMaxDistance()) {
                    synset.setMaxDistance(depth);
                }
            } else if (depth < distanceToHypernym) {
                // this distance is shorter than some previously calculated value, replace it
                synset.putDistanceMap(hypernymID, depth);
            }

            // process the next level upwards for this synset
            buildHypernymTree(synset, hypernym, depth);
        }
    }

    Map<WordCategory, Integer> getCatMaxHypernymDistanceMap() {
        return catMaxHypernymDistanceMap;
    }

    Map<WordCategory, Set<LeastCommonSubsumer>> getCatLongestLCSMap() {
        for (WordCategory wordCategory : WordCategory.values()) {
            // don't calculate again if it was already done
            if (catLongestLCSMap.get(wordCategory) == null) {
                catLongestLCSMap.put(wordCategory, longestLeastCommonSubsumer(wordCategory));
            }
        }
        return catLongestLCSMap;
    }

    /**
     * Calculate the longest least common subsumer(s) for wordCategory. This is used by
     * some of the semantic relatedness algorithms.
     *
     * @param wordCategory WordCategory to process
     * @return a set of LeastCommonSubsumers with the longest possible paths for WordCategory
     */
    private Set<LeastCommonSubsumer> longestLeastCommonSubsumer(WordCategory wordCategory) {

        LOGGER.info("Calculating longest shortest path for {}...", wordCategory.name());
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
}
