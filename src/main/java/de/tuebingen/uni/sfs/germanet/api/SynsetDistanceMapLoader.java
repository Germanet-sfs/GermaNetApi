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

import it.unimi.dsi.fastutil.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * @author Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 * @author Ben Campbell, Seminar für Sprachwissenschaft, Universität Tübingen
 */
class SynsetDistanceMapLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynsetDistanceMapLoader.class);

    /**
     * Add a map to each Synset containing the shortest distance to each of its hypernyms on all
     * paths from the synset to root. Finds the longest path between two synsets (using
     * hypernym relations only), for later use in calculating the longest least common
     * subsumer.
     * @param loaderData contains all synsets when passed in, distance maps are added here
     */
    static LoaderData loadDistanceMaps(LoaderData loaderData) {
        Map<WordCategory, Set<Synset>> catSynsetMap = loaderData.getCatSynsetMap();

        Object2IntMap<WordCategory> catMaxHypernymDistanceMap = new Object2IntOpenHashMap(WordCategory.values().length);
        LOGGER.info("Loading distance maps...");

        // create a separate map for each word category
        for (WordCategory wordCategory : WordCategory.values()) {
            ObjectArrayList<Synset> synsetList = new ObjectArrayList<>(catSynsetMap.get(wordCategory));
            ObjectIterator<Synset> iterator = synsetList.iterator();

            //longest path between any synset and any of its hypernyms
            int maxHypernymDistance = 0;
            Synset synset;
            while (iterator.hasNext()) {
                synset = iterator.next();

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
        loaderData.setCatMaxHypernymDistanceMap(catMaxHypernymDistanceMap);

        return loaderData;
    }

    /**
     * Recursively calculate the shortest distance from synset to all of its hypernyms up to root.
     *
     * @param synset           the synset currently being processed
     * @param hypernymOfSynset one of synset's hypernyms on the path up to root
     * @param depth            distance from synset to hypernymOfSynset
     */
    private static void buildHypernymTree(Synset synset, Synset hypernymOfSynset, int depth) {
        // move up one level towards root
        ObjectArrayList<Synset> hypernymList =
                new ObjectArrayList<>(hypernymOfSynset.getRelatedSynsets(ConRel.has_hypernym));
        depth++;

        // process each direct hypernym of this hypernymOfSynset
        ObjectIterator<Synset> iterator = hypernymList.iterator();
        Synset hypernym;
        while (iterator.hasNext()) {
            hypernym = iterator.next();

            int hypernymID = hypernym.getId();
            int distanceToHypernym = synset.getDistanceToHypernym(hypernymID);

            // this hypernym has not been seen before by this synset, add it
            // or this distance is shorter than some previously calculated value, replace it
            if ((distanceToHypernym < 0)
                    || (depth < distanceToHypernym)) {
                synset.updateDistanceMap(hypernymID, depth);
            }

            // process the next level upwards for this synset
            buildHypernymTree(synset, hypernym, depth);
        }
    }
}
