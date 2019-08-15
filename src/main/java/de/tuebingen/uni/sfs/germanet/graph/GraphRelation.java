package de.tuebingen.uni.sfs.germanet.graph;

/**
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public interface GraphRelation<T extends Enum<T>> {
    Class<T> getDeclaringClass();
    T getRel();
}
