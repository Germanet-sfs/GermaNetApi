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
             File gnetDir = new File("/Users/abrskva/NetBeansProjects/GermaNetTools/xml_output_no_null_inverse");
             GermaNet gnet = new GermaNet(gnetDir);

//             System.out.println(gnet.getIliRecords());
             for (LexUnit lu : gnet.getLexUnits())
                 System.out.println(lu.getWiktionaryParaphrases());


        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

}
