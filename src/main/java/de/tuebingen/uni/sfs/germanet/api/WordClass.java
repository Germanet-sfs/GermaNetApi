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

/**
 * Enumeration of possible word classes.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
public enum WordClass {
    Allgemein,
    Bewegung,
    Gefuehl,
    Geist,
    Gesellschaft,
    Koerper,
    Menge,
    natPhaenomen,
    Ort,
    Pertonym,
    Perzeption,
    privativ,
    Relation,
    Substanz,
    Verhalten,
    Zeit,
    Artefakt,
    Attribut,
    Besitz,
    Form,
    Geschehen,
    Gruppe,
    Kognition,
    Kommunikation,
    Mensch,
    Motiv,
    Nahrung,
    natGegenstand,
    Pflanze,
    Tier,
    Tops,
    Koerperfunktion,
    Konkurrenz,
    Kontakt,
    Lokation,
    Schoepfung,
    Veraenderung,
    Verbrauch;

    /**
     * Returns true if the given <code>wordClassName</code> represents a valid
     * <code>WordClass</code>, otherwise false.
     *
     * @param wordClassName the name of the wordClass to verify
     * @return true if the given <code>wordClassName</code> represents a valid <code>WordClass</code>
     */
    public static boolean isWordClass(String wordClassName) {
        WordClass[] vals = values();

        for (int i = 0; i < vals.length; i++) {
            if (vals[i].toString().equals(wordClassName)) {
                return true;
            }
        }
        return false;
    }
}
