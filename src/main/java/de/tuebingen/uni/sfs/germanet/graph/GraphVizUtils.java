package de.tuebingen.uni.sfs.germanet.graph;

import de.tuebingen.uni.sfs.germanet.api.ConRel;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * author: Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public class GraphVizUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphVizUtils.class);

    public static void getHyperHypoImage(Synset synset, int hyperDepth, int hypoDepth, Format format, File imgFile) throws IOException {

        String label = synset.getId() + " " + synset.getAllOrthForms();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\ndigraph G {\n");
        stringBuilder.append("rankdir=TB;\n");

        // add the root node with a color
        stringBuilder.append(synset.getId() + " [label=\"" + label + "\", color=blue ];\n");
        // add all hypernyms
        getHyperHypoImage(synset, ConRel.has_hypernym, 0, hyperDepth, stringBuilder);
        // add all hyponyms
        getHyperHypoImage(synset, ConRel.has_hyponym, 0, hypoDepth, stringBuilder);

        stringBuilder.append("}\n");

        LOGGER.info("dot graph: {}\n", stringBuilder);

        MutableGraph g = Parser.read(stringBuilder.toString());
        Graphviz.fromGraph(g).render(format).toFile(imgFile);
    }

    private static void getHyperHypoImage(Synset synset, ConRel rel, int depth, int maxDepth, StringBuilder stringBuilder) {
        // stop recursion when we get to the requested depth
        if (depth >= maxDepth) {
            return;
        }

        // add the related synsets and edges
        List<Synset> relatedSynsets = synset.getRelatedSynsets(rel);
        List<Integer> relatedIds = new ArrayList<>();
        for (Synset related : relatedSynsets) {
            String label = related.getId() + " " + related.getAllOrthForms();
            stringBuilder.append(related.getId() + " [label=\"" + label + "\" ];\n");

            // reverse the edge direction and relation for hypernyms
            // which results in a better-looking graph
            if (rel == ConRel.has_hypernym) {
                stringBuilder.append(related.getId()+ " -> " + synset.getId()  + ";\n");
            } else {
                stringBuilder.append(synset.getId() + " -> " + related.getId() + ";\n");
            }
            relatedIds.add(related.getId());
            getHyperHypoImage(related, rel, depth+1, maxDepth, stringBuilder);
        }

        // related synsets should be displayed at the same level
        if (relatedIds.size() > 1) {
            stringBuilder.append("{rank=same; ");
            for (Integer id : relatedIds) {
                stringBuilder.append(id + ";");
            }
            stringBuilder.append("}\n");
        }
    }

    /*
    Attempt to make graph options more general by allowing any conceptual relationship types.
    But unable to figure out how to make graphviz render the hypernyms bottom-up and the hyponyms top-down.
    There must be a way...

    public static void getSubgraphPNG(Synset synset, ConRel rel1, int rel1depth, ConRel rel2, int rel2depth, File imgFile) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\ndigraph G {\n");
        stringBuilder.append("rankdir = \"BT\";\n");

        getSubgraphPNG(synset, rel1, 0, rel1depth, stringBuilder);
        getSubgraphPNG(synset, rel2, 0, rel2depth, stringBuilder);
        stringBuilder.append("}\n");

        LOGGER.info("dot graph: {}\n", stringBuilder);
        MutableGraph g = Parser.read(stringBuilder.toString());
        Graphviz.fromGraph(g).height(3600).render(Format.PNG).toFile(imgFile);
    }

    private static void getSubgraphPNG(Synset synset, ConRel rel, int depth, int maxDepth, StringBuilder stringBuilder) {
        if (depth >= maxDepth) {
            return;
        }
        // add the passed-in synset to the graph
        stringBuilder.append(synset.getId() + " [label=\"" + synset.getLabel(synset) + "\" ];\n");

        // add the related synsets and edges to them
        List<Synset> relatedSynsets = synset.getRelatedSynsets(rel);
        List<Integer> relatedIds = new ArrayList<>();
        for (Synset related : relatedSynsets) {
            stringBuilder.append(related.getId() + " [label=\"" + related.getLabel(related) + "\" ];\n");
            stringBuilder.append(synset.getId() + " -> " + related.getId());
            stringBuilder.append(" [label=\"" + rel.name() + "\"];\n");
            relatedIds.add(related.getId());
            getSubgraphPNG(related, rel, depth+1, maxDepth, stringBuilder);
        }

        // related synsets should be displayed at the same level
        if (relatedIds.size() > 1) {
            stringBuilder.append("rank=\"same\"; ");
            for (Integer id : relatedIds) {
                stringBuilder.append(id + ";");
            }
            stringBuilder.append("\n");
        }
    }
    */
}
