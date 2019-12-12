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

import java.util.List;

/**
 * Simple class to store a path, using hypernym/hyponym relations, between two Synsets.
 * This class does not perform any calculations, it is designed to only store path information.
 * Getters are available for all fields.
 *
 * author: Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public class HyperHypoPath {
    private Synset synset1;
    private Synset synset2;
    private int lcsId;
    private List<Synset> path;

    public HyperHypoPath(Synset synset1, Synset synset2, int lcsId, List<Synset> path) {
        this.synset1 = synset1;
        this.synset2 = synset2;
        this.lcsId = lcsId;
        this.path = path;
    }

    public Synset getSynset1() {
        return synset1;
    }

    public Synset getSynset2() {
        return synset2;
    }

    public int getLcsId() {
        return lcsId;
    }

    public List<Synset> getPath() {
        return path;
    }
}
