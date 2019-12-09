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
    private Map<WordCategory, Integer> catMaxHypernymDistanceMap;

    protected SynsetDistanceMapLoader(GermaNet gnet) {
        this.gnet = gnet;
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
                }
            }
            catMaxHypernymDistanceMap.put(wordCategory, maxHypernymDistance);
        }
        LOGGER.info("Done loading distance maps.");
    }

    /**
     * Recursively calculate the shortest distance from synset to all of its hypernyms up to root.
     *
     * @param synset           the synset currently being processed
     * @param hypernymOfSynset one of synset's hypernyms on the path up to root
     * @param depth            distance from synset to hypernymOfSynset
     */
    private void buildHypernymTree(Synset synset, Synset hypernymOfSynset, int depth) {
        // move up one level towards root
        List<Synset> hypernymList = hypernymOfSynset.getRelatedSynsets(ConRel.has_hypernym);
        depth++;

        // process each direct hypernym of this hypernymOfSynset
        for (Synset hypernym : hypernymList) {

            int hypernymID = hypernym.getId();
            Integer distanceToHypernym = synset.getDistanceToHypernym(hypernymID);

            // this hypernym has not been seen before by this synset, add it
            // or this distance is shorter than some previously calculated value, replace it
            if ((distanceToHypernym == null)
                    || (depth < distanceToHypernym)) {
                synset.updateDistanceMap(hypernymID, depth);
            }

            // process the next level upwards for this synset
            buildHypernymTree(synset, hypernym, depth);
        }
    }

    Map<WordCategory, Integer> getCatMaxHypernymDistanceMap() {
        return catMaxHypernymDistanceMap;
    }
}
