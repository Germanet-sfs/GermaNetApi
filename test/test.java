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
             File gnetDir = new File("/Users/abrskva/NetBeansProjects/GN_V70_XML.zip");
             GermaNet gnet = new GermaNet(gnetDir);

//             System.out.println(gnet.getIliRecords());
             for (Synset s : gnet.getSynsets(WordClass.Menge))
                 System.out.println(s);


        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

}
