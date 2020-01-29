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
import java.util.Objects;

/**
 * Simple class to store a path, using hypernym/hyponym relations, between two Synsets.
 * This class does not perform any calculations, it is designed to only store path information.
 * Getters are available for all fields.
 *
 * author: Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public class SynsetPath {
    private Synset fromSynset;
    private Synset toSynset;
    private int lcsId;
    private List<Synset> path;

    public SynsetPath(Synset fromSynset, Synset toSynset, int lcsId, List<Synset> path) {
        this.fromSynset = fromSynset;
        this.toSynset = toSynset;
        this.lcsId = lcsId;
        this.path = path;
    }

    public Synset getFromSynset() {
        return fromSynset;
    }

    public Synset getToSynset() {
        return toSynset;
    }

    public int getLcsId() {
        return lcsId;
    }

    public List<Synset> getPath() {
        return path;
    }

    public String toString() {
        String rval = "";
        for (Synset synset : path) {
            rval += synset.getId() + " -> ";
        }
        return rval.substring(0, rval.length()-4);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SynsetPath that = (SynsetPath) o;

        if (lcsId != that.lcsId) return false;
        if (!fromSynset.equals(that.fromSynset)) return false;
        if (!toSynset.equals(that.toSynset)) return false;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        int result = fromSynset.hashCode();
        result = 31 * result + toSynset.hashCode();
        result = 31 * result + lcsId;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
