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
 * The path between Synsets is stored as two ordered Lists: <br><br>
 *
 * The list of Synsets on the path from <code>fromSynset</code> to a least common subsumer of the two Synsets<br>
 * The list of Synsets on the path from <code>toSynset</code> to a least common subsumer of the two Synsets<br>
 *
 * Both lists have the originating Synset at index 0, and the least common subsumer as the last element.
 * The least common subsumer of two Synsets is the closest Synset that can be reached by both originating
 * Synsets using hypernym relations.<br>
 *
 * This class does not perform any calculations, it is designed to only store path information.
 * Getters are available for all fields.
 *
 * author: Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public class SynsetPath {
    private Synset fromSynset;
    private Synset toSynset;
    private int lcsId;
    private List<Synset> fromLcsPath;
    private List<Synset> toLcsPath;

    public SynsetPath(Synset fromSynset, Synset toSynset, int lcsId, List<Synset> fromLcsPath, List<Synset> toLcsPath) {
        this.fromSynset = fromSynset;
        this.toSynset = toSynset;
        this.lcsId = lcsId;
        this.fromLcsPath = fromLcsPath;
        this.toLcsPath = toLcsPath;
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

    public List<Synset> getFromLcsPath() {
        return fromLcsPath;
    }

    public List<Synset> getToLcsPath() {
        return toLcsPath;
    }

    @Override
    public String toString() {
        return "SynsetPath{" +
                "fromSynset=" + fromSynset +
                ", toSynset=" + toSynset +
                ", lcsId=" + lcsId +
                ", fromLcsPath=" + fromLcsPath +
                ", toLcsPath=" + toLcsPath +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SynsetPath that = (SynsetPath) o;

        if (lcsId != that.lcsId) return false;
        if (!fromSynset.equals(that.fromSynset)) return false;
        if (!toSynset.equals(that.toSynset)) return false;
        if (!fromLcsPath.equals(that.fromLcsPath)) return false;
        return toLcsPath.equals(that.toLcsPath);
    }

    @Override
    public int hashCode() {
        int result = fromSynset.hashCode();
        result = 31 * result + toSynset.hashCode();
        result = 31 * result + lcsId;
        result = 31 * result + fromLcsPath.hashCode();
        result = 31 * result + toLcsPath.hashCode();
        return result;
    }
}
