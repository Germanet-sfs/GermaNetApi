/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import de.tuebingen.uni.sfs.germanet.api.*;
import java.io.*;
import java.util.List;

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
             File gnetDir = new File("/Users/abrskva/xml_output_new");
//            File gnetDir = new File("/Users/abrskva/Resources/GN_V70/GN_V70_XML");
             GermaNet gnet = new GermaNet(gnetDir);

//             System.out.println(gnet.getIliRecords());
             for (LexUnit lu : gnet.getLexUnits()) {
                 if (lu.getCompound() != null) {
                     if (lu.getWordCategory().equals(WordCategory.adj) ||
                             lu.getWordCategory().equals(WordCategory.verben)) System.out.print("!!! ");
                     System.out.println(lu.getOrthForm() + " = " + lu.getCompound());
                 }
                 //System.out.println(s.getLexUnits().get(0).getWiktionaryParaphrases());
             }
//             for (LexUnit lu : gnet.getLexUnitsWithCompounds().keySet()) {
//                 System.out.println(gnet.getLexUnitsWithCompounds().get(lu));
//             }


        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

}
