/*
 * Copyright (C) 2012 Department of General and Computational Linguistics,
 * University of Tuebingen
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
package de.tuebingen.uni.sfs.germanet.api;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Extra (optional) tests for checking the correct behavior when ILI or Wiktionary files
 * are corrupt or zero-length.
 *
 * To run these tests, create additional datasets with corrupt and zero-length
 * ILI and Wiktionary files.
 *
 * author: Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class R14IliWiktTest {

    static String dataPath;
    static GermaNet gnet;

    @BeforeAll
    static void setUp() {
        String release = "14";
        String userHome = System.getProperty("user.home");
        String sep = System.getProperty("file.separator");
        dataPath = userHome + sep + "Data" + sep + "GermaNetForApiUnitTesting" + sep + "R" + release + sep;
    }

    @AfterEach
    void cleanup() {
        gnet = null;
    }

    @Test
    void corruptIliFileTest() {
        assertThrows(XMLStreamException.class, () -> {
            gnet = new GermaNet(dataPath + "XML-CorruptILI/");
        });
    }

    @Test
    void corruptWiktFileTest() {
        assertThrows(XMLStreamException.class, () -> {
            gnet = new GermaNet(dataPath + "XML-CorruptWikt/");
        });
    }

    @Test
    void zeroLengthIliFileTest() {
        assertThrows(XMLStreamException.class, () -> {
            gnet = new GermaNet(dataPath + "XML-EmptyIli/");
        });
    }

    @Test
    void zeroLengthWiktFileTest() {
        assertThrows(XMLStreamException.class, () -> {
            gnet = new GermaNet(dataPath + "XML-EmptyWikt/");
        });
    }
}
