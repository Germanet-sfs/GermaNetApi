/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import de.tuebingen.uni.sfs.germanet.api.*;
import java.io.*;
import java.util.List;

/**
 *
 * @author Sfs-University of Tübingen
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

             int count = 0;
             for (LexUnit lu : gnet.getLexUnits()) {
                 
                 if (!lu.getExamples().isEmpty()) {
                     count++;
                 }
             }
             System.out.println(count);



        } catch (Exception ex) {
            System.exit(0);
        }
    }

}
