/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import germanet.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author abrskva
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            File gnetDir = new File("K:/from Agnia/GN_V60");
//            File iliFile = new File("/afs/sfs/home/abrskva/NetBeansProjects/ili.xml");
 //           String fileName = "/space/abrskva/NetBeansProjects/ConvertToXML/ili_with_syn.xml";
            GermaNet gnet = new GermaNet(gnetDir);
 //           gnet.loadIli(fileName);
 //           gnet.loadWictionaryParaphrase(new File ("/space/abrskva/GermaNet/GN_V60"));

            Synset s1 = gnet.getSynsets("leer").get(0);
            Synset s2 = gnet.getSynsets("geleert").get(0);
            System.out.println(s1.toString());
            System.out.println(s2.toString());
            if (s1.equals(s2))
                System.out.println("true");
/*            //test whether or not 1 synset has several LexUnits with corresponding ILI Records
            List<Synset> synsets = gnet.getSynsets();
            for (Synset ss : synsets) {
                List<LexUnit> units = ss.getLexUnits();
                int counter = 0;
                for (LexUnit lu : units) {
                    if (lu.hasIli())
                        counter++;
                }
                if ((ss.numLexUnits() > 1) && (counter == ss.numLexUnits()))
                    System.out.println(ss.toString());
                    
            }


            //test how wiktionary paraphrases load
            LexUnit unit = gnet.getLexUnitByID(3533);
            List<WiktionaryParaphrase> wikis = unit.getWiktionaryParaphrases();
            for (WiktionaryParaphrase wiki : wikis)
                System.out.println(wiki.toString());
            System.out.println();*/
            //test how ILI records load
 /*           List<LexUnit> units = gnet.getLexUnits("abblocken");
            for (LexUnit unit1 : units) {
                List<IliRecord> ili = unit1.getIliRecords();
                for (IliRecord i : ili)
                    System.out.println(i.toString());
            }
            System.out.println();
            Synset ss = gnet.getSynsetByID(5711);
            List<IliRecord> ili2 = ss.getIliRecords();
            for (IliRecord i : ili2)
                    System.out.println(i.toString());*/

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

}
