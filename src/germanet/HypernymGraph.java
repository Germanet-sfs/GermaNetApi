/*
 * Copyright (C) 2011 Verena Henrich, Department of General and Computational
 * Linguistics, University of Tuebingen
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
package germanet;

import java.io.*;
import java.util.*;

/**
 * Hypernym Graph is the tutorial task showcasing the most important GermaNet API features.
 *
 * @author Verena Henrich (verena.henrich at uni-tuebingen.de)
 * @version 7.0
*/
public class HypernymGraph {

    public static void main(String[] args) {
        try {
            Scanner keyboard = new Scanner(System.in);
            String destName;
            File gnetDir;
            String word;
            int depth;
            Writer dest;
            System.out.println("HypernymGraph creates a GraphViz graph "
                    + "description of hypernyms and hyponyms of a GermaNet"
                    + "concept up to a given depth.");
            System.out.println("Enter <word> <depth> <outputFile> "
                    + "[eg: Automobil 2 auto.dot]: ");
            word = keyboard.next();
            depth = keyboard.nextInt();
            destName = keyboard.nextLine().trim();
            gnetDir = new File("/afs/sfs/home/abrskva/GermaNet/GN_V60/GN_V60");
            GermaNet gnet = new GermaNet(gnetDir);

            List<Synset> synsets;
            synsets = gnet.getSynsets(word);
            if (synsets.size() == 0) {
                System.out.println(word + " not found in GermaNet");
                System.exit(0);
            }
            String dotCode = "";
            dotCode += "graph G {\n";
            dotCode += "overlap=false\n";
            dotCode += "splines=true\n";
            dotCode += "orientation=landscape\n";
            dotCode += "size=\"13,15\"\n";
            HashSet<Synset> visited = new HashSet<Synset>();
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
    static String printHypernyms(Synset synset, int depth, HashSet<Synset> visited) {
        String rval = "";
        List<LexUnit> lexUnits;
        String orthForm = "";
        List<Synset> hypernyms = new ArrayList<Synset>();
        List<Synset> relations;
        String hypOrthForm;
        visited.add(synset);
        lexUnits = synset.getLexUnits();
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
                    rval += "\"" + hypOrthForm
                            + "\"[fontname=Helvetica,fontsize=8]\n";
                }
            }
        }
        return rval;
    }
}
