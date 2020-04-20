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

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration of possible word categories, i.e., part-of-speech. Note that not all <code>WordClass</code>es are defined for all
 * <code>WordCategory</code>'s. Therefore, only certain combinations of <code>WordClass</code> and
 * <code>WordCategory</code> occur in GermaNet. Please see the "GermaNet Structures" section of the
 * homepage for a complete list of combinations.
 *
 * The <code>occursWithWordClass</code> and <code>getWordClasses</code> methods can be used to verify possible combinations.
 * 
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
public enum WordCategory {
    adj(
            WordClass.Allgemein,
            WordClass.Bewegung,
            WordClass.Gefuehl,
            WordClass.Geist,
            WordClass.Gesellschaft,
            WordClass.Koerper,
            WordClass.Menge,
            WordClass.natPhaenomen,
            WordClass.Ort,
            WordClass.Pertonym,
            WordClass.Perzeption,
            WordClass.privativ,
            WordClass.Relation,
            WordClass.Substanz,
            WordClass.Verhalten,
            WordClass.Zeit
    ),
    nomen(
            WordClass.Gefuehl,
            WordClass.Koerper,
            WordClass.Menge,
            WordClass.natPhaenomen,
            WordClass.Ort,
            WordClass.Relation,
            WordClass.Substanz,
            WordClass.Zeit,
            WordClass.Artefakt,
            WordClass.Attribut,
            WordClass.Besitz,
            WordClass.Form,
            WordClass.Geschehen,
            WordClass.Gruppe,
            WordClass.Kognition,
            WordClass.Kommunikation,
            WordClass.Mensch,
            WordClass.Motiv,
            WordClass.Nahrung,
            WordClass.natGegenstand,
            WordClass.Pflanze,
            WordClass.Tier,
            WordClass.Tops
    ),
    verben(
            WordClass.Allgemein,
            WordClass.Gefuehl,
            WordClass.Gesellschaft,
            WordClass.Koerper,
            WordClass.natPhaenomen,
            WordClass.Perzeption,
            WordClass.Besitz,
            WordClass.Kognition,
            WordClass.Kommunikation,
            WordClass.Koerperfunktion,
            WordClass.Konkurrenz,
            WordClass.Kontakt,
            WordClass.Lokation,
            WordClass.Schoepfung,
            WordClass.Veraenderung,
            WordClass.Verbrauch
    );

    private List<WordClass> wordClasses;

    private WordCategory(WordClass... classes) {
        wordClasses = new ArrayList<>();
        for (WordClass wordClass : classes) {
            wordClasses.add(wordClass);
        }
    }

    /**
     * Return true if the given <code>wordCategoryName</code> represents a valid <code>WordCategory</code>,
     * otherwise false.
     *
     * @param wordCategoryName the name of the WordCategory to verify
     * @return true if the given <code>wordCategoryName</code> represents a valid <code>WordCategory</code>,
     * otherwise false
     */
    public static boolean isWordCategory(String wordCategoryName) {
        WordCategory[] vals = values();
        for (int i=0; i < vals.length; i++) {
            if (vals[i].toString().equals(wordCategoryName)) {
                return true;
            }
        }
        return false;
    }

    /**
     *  Returns true if this <code>WordCategory</code> occurs in combination with the given <code>WordClass</code>,
     *  otherwise false.
     *
     * @param wordClass the WordClass to check
     * @return true if this <code>WordCategory</code> occurs in combination with the given <code>WordClass</code>,
     * otherwise false
     */
    public boolean occursWithWordClass(WordClass wordClass) {
        return wordClasses.contains(wordClass);
    }

    /**
     * Get the non-empty list of <code>WordClass</code>es for which this <code>WordCategory</code> is defined.
     *
     * @return the non-empty list of <code>WordClass</code>es for which this <code>WordCategory</code> is defined
     */
    public List<WordClass> getWordClasses() {
        return wordClasses;
    }
}
