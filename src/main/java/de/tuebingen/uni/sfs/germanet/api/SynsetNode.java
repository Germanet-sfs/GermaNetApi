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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to store the distance of a synset to all of its hypernym parents up to root.
 *
 * author: Ben Campbell, Seminar für Sprachwissenschaft, Universität Tübingen
 */
class SynsetNode implements Comparable<SynsetNode> {
    private Integer synsetId;
    private Map<Integer, Integer> distanceMap;
    private Set<Integer> hypernymIds;
    private int maxDistance;


    SynsetNode(Integer synsetId) {
        this.synsetId = synsetId;
        maxDistance = 0;
        distanceMap = new HashMap<>();
        hypernymIds = new HashSet<>();
        distanceMap.put(this.synsetId, 0);
        hypernymIds.add(this.synsetId);
    }

    Integer getSynsetId() {
        return synsetId;
    }

    void putDistanceMap(Integer hypernymID, Integer depth) {
        distanceMap.put(hypernymID, depth);
    }

    Set<Integer> getHypernymIds() {
        return hypernymIds;
    }

    void addHypernymId(Integer hypernymID) {
        hypernymIds.add(hypernymID);
    }

    int getMaxDistance() {
        return maxDistance;
    }

    void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    Integer getDistanceToHypernym(int hypernymID) {
    	return distanceMap.get(hypernymID);
	}

    @Override
    public int compareTo(SynsetNode o) {
        return o.maxDistance - this.maxDistance;
    }
}