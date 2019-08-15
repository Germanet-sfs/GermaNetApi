package de.tuebingen.uni.sfs.germanet.semantic;

import de.tuebingen.uni.sfs.germanet.api.Synset;

import java.util.*;

class GermaNetPath {
    private List<Synset> synsetList;
    private Set<Synset> synsetSet;
    private Map<Synset, Integer> synsetIndexMap;
    private int index;

    public GermaNetPath() {
        synsetList = new ArrayList<>();
        synsetSet = new HashSet<>();
        synsetIndexMap = new HashMap<>();
        index = 0;
    }

    void add(Synset synset) {
        synsetList.add(synset);
        synsetSet.add(synset);
        synsetIndexMap.put(synset, index);
        index++;
    }

    void addAll(GermaNetPath path) {
        for (Synset synset : path.synsetList) {
            add(synset);
        }
        //synsetList.addAll(path.synsetList);
        //synsetSet.addAll(path.synsetSet);
    }

    public Synset getLastOnPath() {
        if (synsetList.isEmpty()) {
            return null;
        }
        return synsetList.get(synsetList.size() - 1);
    }

    public List<Synset> getSynsetList() {
        return synsetList;
    }

    public Set<Synset> getSynsetSet() {
        return synsetSet;
    }

    boolean contains(Synset synset) {
        return synsetSet.contains(synset);
    }

    int indexOf(Synset synset) {
        return synsetIndexMap.get(synset);
    }

    // path length is the number of edges
    int pathLength() {
        return index-1;
    }

    int index() {
        return index;
    }

    public String toString() {
        return synsetList.toString();
    }
}
