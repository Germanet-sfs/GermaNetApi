import java.util.*;
import de.tuebingen.uni.sfs.germanet.api.*;
import java.io.*;

/**
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at sfs.uni-tuebingen.de)
 */
public class HypernymGraph {
    public static void main(String[] args) {
        try {
            Scanner keyboard = new Scanner(System.in);
            String destName;
            String word;
            int depth;
            Writer dest;
            File gnetDir;
            List<Synset> synsets = null;
            String dotCode = "";
            HashSet<Synset> visited;

            System.out.println("HypernymGraph creates a GraphViz graph " +
                    "description of hypernyms and hyponyms of a GermaNet " +
                    "concept up to a given depth.\n");
            System.out.println("Enter <word> <depth> <outputFile> " +
                    "[eg: Automobil 2 auto.dot]: ");
            word = keyboard.next();
            depth = keyboard.nextInt();
            destName = keyboard.nextLine().trim();

            gnetDir = new File("/germanet/GN_V90/GN_V90_XML");
            GermaNet gnet = new GermaNet(gnetDir);

            synsets = gnet.getSynsets(word);
            if (synsets.size() == 0) {
                System.out.println(word + " not found in GermaNet");
                System.exit(0);
            }

            dotCode += "graph G {\n";
            dotCode += "overlap=false\n";
            dotCode += "splines=true\n";
            dotCode += "orientation=landscape\n";
            dotCode += "size=\"13,15\"\n";

            visited = new HashSet<Synset>();
            for (Synset syn : synsets) {
                dotCode += printHypernyms(syn, depth, visited);
            }

            dotCode += "}";
            dest = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(new File(destName)), "UTF-8"));
            dest.write(dotCode);
            dest.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    static String printHypernyms(Synset synset, int depth,
            HashSet<Synset> visited) {

        String rval = "";
        List<LexUnit> lexUnits;
        String orthForm = "";
        List<Synset> hypernyms = new ArrayList<Synset>();
        List<Synset> relations;
        String hypOrthForm;

        visited.add(synset);

        lexUnits = synset.getLexUnits();
        // use just the first orthForm in the first LexUnit
        orthForm = lexUnits.get(0).getOrthForm();
        rval += "\"" + orthForm + "\" [fontname=Helvetica,fontsize=10]\n";

        relations = synset.getRelatedSynsets(ConRel.has_hypernym);
        hypernyms.addAll(relations);
        relations = synset.getRelatedSynsets(ConRel.has_hyponym);
        hypernyms.addAll(relations);

        for (Synset syn : hypernyms) {
            if (!visited.contains(syn)) {
                hypOrthForm = syn.getLexUnits().get(0).getOrthForm();
                rval += "\"" + orthForm + "\" -- \"" + hypOrthForm + "\";\n";

                if (depth > 1) {
                    rval += printHypernyms(syn, depth - 1, visited);
                } else {
                    rval += "\"" + hypOrthForm + "\" [fontname=Helvetica,fontsize=8]\n";
                }
            }
        }
        return rval;
    }
}

