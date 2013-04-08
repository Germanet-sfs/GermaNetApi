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
//             File gnetDir = new File("/Users/abrskva/xml_output");
            File gnetDir = new File("/Users/abrskva/xml_out");
             GermaNet gnet = new GermaNet(gnetDir);

             
             for (LexUnit lu : gnet.getLexUnits()) {
                 for (IliRecord ili : lu.getIliRecords()) {
                     if (ili.getPwnWord() == null) {
                         System.out.println(ili);
                     }
                 }
             }



        } catch (Exception ex) {
            System.exit(0);
        }
    }

}
