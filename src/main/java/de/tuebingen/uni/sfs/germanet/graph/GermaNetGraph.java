package de.tuebingen.uni.sfs.germanet.graph;

import de.tuebingen.uni.sfs.germanet.api.*;
import de.tuebingen.uni.sfs.germanet.semantic.LeastCommonSubsumer;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.lca.EulerTourRMQLCAFinder;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

// ToDo: add credits for:
//  JGraphT (LGPL 2.1, https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html)
//    OR (EPL) http://www.eclipse.org/org/documents/epl-v20.php
//  JGraphX (see licensing at https://jgraph.github.io/mxgraph/docs/manual_javavis.html#1.5)

/**
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public class GermaNetGraph {
    private GermaNet gnet;
    private Graph<GermaNetVertex, GermaNetEdge> synsetGraph;
    private Graph<GermaNetVertex, GermaNetEdge> hypernymGraph;
    private Graph<GermaNetVertex, GermaNetEdge> lexunitGraph; // ToDo: get rid of this
    private Graph<GermaNetVertex, GermaNetEdge> hyponymGraph; // ToDo: get rid of this
    private Graph<GermaNetVertex, GermaNetEdge> fullGraph; // ToDo: get rid of this
    //    private Set<Synset> synsetNodes;
//    private Set<LexUnit> lexunitNodes;
    private Map<ConRel, GermaNetEdge> conrelEdgeMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(GermaNetGraph.class);

    public GermaNetGraph(GermaNet gnet) {
        this.gnet = gnet;
        conrelEdgeMap = new HashMap<>();
        loadGraph();
    }

    void loadGraph() {
        synsetGraph = GraphTypeBuilder
                .<GermaNetVertex, GermaNetEdge>directed().allowingMultipleEdges(true).allowingSelfLoops(false)
                .edgeClass(GermaNetEdge.class).weighted(false).buildGraph();

        hypernymGraph = GraphTypeBuilder
                .<GermaNetVertex, GermaNetEdge>directed().allowingMultipleEdges(true).allowingSelfLoops(false)
                .edgeClass(GermaNetEdge.class).weighted(false).buildGraph();

        lexunitGraph = GraphTypeBuilder
                .<GermaNetVertex, GermaNetEdge>directed().allowingMultipleEdges(true).allowingSelfLoops(false)
                .edgeClass(GermaNetEdge.class).weighted(false).buildGraph();

        hyponymGraph = GraphTypeBuilder
                .<GermaNetVertex, GermaNetEdge>directed().allowingMultipleEdges(true).allowingSelfLoops(false)
                .edgeClass(GermaNetEdge.class).weighted(false).buildGraph();

        fullGraph = GraphTypeBuilder
                .<GermaNetVertex, GermaNetEdge>directed().allowingMultipleEdges(true).allowingSelfLoops(false)
                .edgeClass(GermaNetEdge.class).weighted(false).buildGraph();

        // add all synset nodes to synset graph and hyperHypo graph,
        // all lexunit nodes to lexunit graph,
        // and all synset and lexunit nodes to full graph
        for (Synset synset : gnet.getSynsets()) {
            synsetGraph.addVertex(synset);
            hypernymGraph.addVertex(synset);
            hyponymGraph.addVertex(synset);
            fullGraph.addVertex(synset);
            for (LexUnit lexUnit : synset.getLexUnits()) {
                lexunitGraph.addVertex(lexUnit);
                fullGraph.addVertex(lexUnit);
            }
        }

        // add all ConRel edges to synset graph
        // add only hypernym and hyponym edges to hypernym and hyponym graphs, respectively
        for (Synset synset : gnet.getSynsets()) {
            for (ConRel conRel : ConRel.values()) {
                for (Synset relatedSynset : synset.getRelatedSynsets(conRel)) {
                    synsetGraph.addEdge(synset, relatedSynset, new GermaNetEdge(conRel));
                    if (conRel == ConRel.has_hyponym) {
                        hyponymGraph.addEdge(synset, relatedSynset, new GermaNetEdge(conRel));
                    }
                    if (conRel == ConRel.has_hypernym) {
                        hypernymGraph.addEdge(synset, relatedSynset, new GermaNetEdge(conRel));
                    }
                }
            }
        }

        // add LexRel edges to lexunit graph
        for (LexUnit lexUnit : gnet.getLexUnits()) {
            for (LexRel lexRel : LexRel.values()) {
                for (LexUnit relatedLexUnit : lexUnit.getRelatedLexUnits(lexRel)) {
                    lexunitGraph.addEdge(lexUnit, relatedLexUnit, new GermaNetEdge(lexRel));
                }
            }
        }

        // create the full graph by adding edges to connect synsets to their lexunits
        for (Synset synset : gnet.getSynsets()) {
            for (LexUnit lexUnit : synset.getLexUnits()) {
                fullGraph.addEdge(synset, lexUnit, new GermaNetEdge(MixedRel.has_lexunit));
                fullGraph.addEdge(lexUnit, synset, new GermaNetEdge(MixedRel.is_lexunit_of));
            }
        }

    }

    public Graph<GermaNetVertex, GermaNetEdge> getSynsetGraph() {
        return synsetGraph;
    }

    public Graph<GermaNetVertex, GermaNetEdge> getLexunitGraph() {
        return lexunitGraph;
    }

    public Graph<GermaNetVertex, GermaNetEdge> getHyponymGraph() {
        return hyponymGraph;
    }

    public Graph<GermaNetVertex, GermaNetEdge> getFullGraph() {
        return fullGraph;
    }


    public GraphPath<GermaNetVertex, GermaNetEdge> shortestPath(Graph<GermaNetVertex, GermaNetEdge> graph, GermaNetVertex v1, GermaNetVertex v2) {

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return dijkstraShortestPath.getPath(v1, v2);
    }

    public GermaNetVertex lcs(Graph<GermaNetVertex, GermaNetEdge> graph, GermaNetVertex v1, GermaNetVertex v2) {
        //TarjanLCAFinder tarjanLCAFinder = new TarjanLCAFinder(graph, gnet.getSynsetByID(51001));
        //return (GermaNetVertex) tarjanLCAFinder.getLCA(v1, v2);
        EulerTourRMQLCAFinder eulerTourRMQLCAFinder = new EulerTourRMQLCAFinder(graph, gnet.getSynsetByID(51001));
        return (GermaNetVertex) eulerTourRMQLCAFinder.getLCA(v1, v2);
        //BinaryLiftingLCAFinder binaryLiftingLCAFinder = new BinaryLiftingLCAFinder(graph, gnet.getSynsetByID(51001));
        //return (GermaNetVertex) binaryLiftingLCAFinder.getLCA(v1, v2);

        //NaiveLCAFinder naiveLCAFinder = new NaiveLCAFinder(graph);
        //return (GermaNetVertex) naiveLCAFinder.getLCA(v1, v2);
    }
}
