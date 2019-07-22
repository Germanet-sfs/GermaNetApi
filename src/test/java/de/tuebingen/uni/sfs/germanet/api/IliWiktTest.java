package de.tuebingen.uni.sfs.germanet.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Extra (optional) tests for checking the correct behavior when ILI or Wiktionary files
 * are corrupt or zero-length.
 *
 * To run these tests, create additional datasets with corrupt and zero-length
 * ILI and Wiktionary files.
 *
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */

class IliWiktTest {

    static String dataPath;

    @BeforeAll
    static void setUp() {
        String userHome = System.getProperty("user.home");
        String sep = System.getProperty("file.separator");
        dataPath = userHome + sep + "Data" + sep;
    }

    @Test
    void corruptIliFileTest() {
        assertThrows(XMLStreamException.class, () -> {
            new GermaNet(dataPath + "GN-XML-ForApiUnitTestingCorruptILI/");
        });
    }

    @Test
    void corruptIliStreamTest() {
        assertThrows(XMLStreamException.class, () -> {
            new GermaNet(dataPath + "GN-XML-ForApiUnitTestingCorruptILI.zip/");
        });
    }

    @Test
    void corruptWiktFileTest() {
        assertThrows(XMLStreamException.class, () -> {
            new GermaNet(dataPath + "GN-XML-ForApiUnitTestingCorruptWikt/");
        });
    }

    @Test
    void corruptWiktStreamTest() {
        assertThrows(XMLStreamException.class, () -> {
            new GermaNet(dataPath + "GN-XML-ForApiUnitTestingCorruptWikt.zip/");
        });
    }

    @Test
    void zeroLengthIliFileTest() {
        assertThrows(XMLStreamException.class, () -> {
            new GermaNet(dataPath + "GN-XML-ForApiUnitTestingEmptyIli/");
        });
    }

    @Test
    void zeroLengthIliStreamTest() {
        assertThrows(XMLStreamException.class, () -> {
            new GermaNet(dataPath + "GN-XML-ForApiUnitTestingEmptyIli.zip/");
        });
    }

    @Test
    void zeroLengthWiktFileTest() {
        assertThrows(XMLStreamException.class, () -> {
            new GermaNet(dataPath + "GN-XML-ForApiUnitTestingEmptyWikt/");
        });
    }

    @Test
    void zeroLengthWiktStreamTest() {
        assertThrows(XMLStreamException.class, () -> {
            new GermaNet(dataPath + "GN-XML-ForApiUnitTestingEmptyWikt.zip/");
        });
    }
}
