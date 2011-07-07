import java.util.*;
import germanet.*;
import java.io.*;
import javax.xml.stream.XMLStreamException;
import java.util.zip.ZipException;

/**
 *
 * @author vhenrich
 */
public class AllOrthFormsTest {
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException, ZipException, IOException {
        File gnetDir = new File("/Users/vhenrich/NetBeansProjects/GN_V60");;
        GermaNet gnet = new GermaNet(gnetDir);

        List<LexUnit> synsets = gnet.getLexUnits("auf sein");
        if (synsets.size() == 0) {
            System.out.println("not found in GermaNet");
            System.exit(0);
        }

        for (LexUnit syn : synsets) {
            System.out.println("syn1=" + syn.toString());
        }

        System.out.println("");
        synsets = gnet.getLexUnits("aufsein");
        if (synsets.size() == 0) {
            System.out.println("not found in GermaNet");
            System.exit(0);
        }

        for (LexUnit syn : synsets) {
            System.out.println("syn2=" + syn.toString());
        }
    }
}

