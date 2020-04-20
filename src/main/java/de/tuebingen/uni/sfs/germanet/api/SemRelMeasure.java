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
 * author: Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public enum SemRelMeasure {
    SimplePath(MeasureType.Path),
    LeacockAndChodorow(MeasureType.Path),
    WuAndPalmer(MeasureType.Path),
    Resnik(MeasureType.InformationContent),
    Lin(MeasureType.InformationContent),
    JiangAndConrath(MeasureType.InformationContent);

    private MeasureType measureType;

    private SemRelMeasure(MeasureType measureType) {
        this.measureType = measureType;
    }

    /**
     * Returns true if the <code>String</code> <code>measureName</code> represents a
     * valid <code>SemRelMeasure</code>.
     * @param measureName the name of the measure to verify
     * @return true if the <code>String measureName</code> represents a valid
     * <code>SemRelMeasure</code>
     */
    public static boolean isSemRelMeasure(String measureName) {
        SemRelMeasure[] vals = values();

        for (int i = 0; i < vals.length; i++) {
            if (vals[i].toString().equals(measureName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the type of this <code>SemRelMeasure</code>.
     * @return the type of this <code>SemRelMeasure</code>.
     */
    public MeasureType getMeasureType() {
        return measureType;
    }

    /**
     * Returns true if this <code>SemRelMeasure</code> is the given type.
     * @param type the <code>MeasureType</code> to check for
     * @return true if this <code>SemRelMeasure</code> is the given type
     */
    public boolean isMeasureType(MeasureType type) {
        return measureType == type;
    }

    public enum MeasureType {
        Path,
        InformationContent
    }
}
