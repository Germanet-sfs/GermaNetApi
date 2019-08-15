package de.tuebingen.uni.sfs.germanet.graph;

import org.jgrapht.graph.DefaultEdge;

/**
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public class GermaNetEdge extends DefaultEdge {
    private GraphRelation<?> relation;

    public GermaNetEdge(GraphRelation<?> relation) {
        super();
        this.relation = relation;
    }

    GraphRelation<?> getRelation() {
        return relation;
    }

    public String getLabel() {
        return relation.toString();
    }
    public String toString() {
        return relation.getRel().name();
    }
}
