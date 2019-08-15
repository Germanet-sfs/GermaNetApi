package de.tuebingen.uni.sfs.germanet.graph;

/**
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public enum MixedRel implements GraphRelation<MixedRel> {
    is_lexunit_of,
    has_lexunit;

    @Override
    public MixedRel getRel() {
        return this;
    }
}
