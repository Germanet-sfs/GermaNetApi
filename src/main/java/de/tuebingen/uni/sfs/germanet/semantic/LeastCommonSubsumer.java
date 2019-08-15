package de.tuebingen.uni.sfs.germanet.semantic;

import de.tuebingen.uni.sfs.germanet.api.Synset;

import java.util.List;

/**
 * Simple class to store LeastCommonSubsumer information. All fields are public for easy access.
 */
public class LeastCommonSubsumer {
    public Synset lcs;
    public List<Synset> s1PathToLCS;
    public List<Synset> s2PathToLCS;
    public int distance;

    public LeastCommonSubsumer(Synset lcs, List<Synset> s1PathToLCS, List<Synset> s2PathToLCS, int distance) {
        this.lcs = lcs;
        this.s1PathToLCS = s1PathToLCS;
        this.s2PathToLCS = s2PathToLCS;
        this.distance = distance;
    }

    public String toString() {
        return  "\nlcs: " + lcs +
                "\ndistance: " + distance +
                "\ns1PathToLCS: (length " + (s1PathToLCS.size()-1) + ") " + s1PathToLCS +
                "\ns2PathToLCS: (length " + (s2PathToLCS.size()-1) + ") " + s2PathToLCS;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeastCommonSubsumer that = (LeastCommonSubsumer) o;

        if (distance != that.distance) return false;
        if (!lcs.equals(that.lcs)) return false;
        if (!s1PathToLCS.equals(that.s1PathToLCS)) return false;
        return s2PathToLCS.equals(that.s2PathToLCS);
    }

    @Override
    public int hashCode() {
        int result = lcs.hashCode();
        result = 31 * result + s1PathToLCS.hashCode();
        result = 31 * result + s2PathToLCS.hashCode();
        result = 31 * result + distance;
        return result;
    }
}
