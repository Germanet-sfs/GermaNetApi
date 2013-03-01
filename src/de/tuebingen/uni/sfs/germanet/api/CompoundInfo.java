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

import java.util.*;

/**
 * A <code>CompoundInfo</code> shows constituent parts of a German word, their
 * word categories and attributes.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 8.0
 */
public class CompoundInfo {
    private CompoundProperty modProp;
    private String modifier1;
    private CompoundCategory mod1Cat;
    private String modifier2;
    private CompoundCategory mod2Cat;
    private String head;
    private CompoundProperty headProp;

    /**
     * Constructs a <code>CompoundInfo</code> with the specified attributes.
     * @param modProp
     * @param modifier1
     * @param mod1Cat
     * @param modifier2
     * @param mod2Cat
     * @param head
     * @param headProp
     */
    public CompoundInfo (CompoundProperty modProp, String modifier1,
            CompoundCategory mod1Cat, String modifier2, CompoundCategory mod2Cat,
            String head, CompoundProperty headProp) {
        this.modProp = modProp;
        this.modifier1 = modifier1;
        this.mod1Cat = mod1Cat;
        this.modifier2 = modifier2;
        this.mod2Cat = mod2Cat;
        this.head = head;
        this.headProp = headProp;
    }

    /**
     * Returns the <code>CompoundProperty</code> of the modifier(s)
     * or null if it has not been set
     * @return the <code>CompoundProperty</code> of the modifier
     */
    public CompoundProperty getModifierProperty() {
        return this.modProp;
    }

    /**
     * Returns the first modifier of this <code>CompoundInfo</code>
     * @return the first modifier of this <code>CompoundInfo</code>
     */
    public String getModifier1() {
        return this.modifier1;
    }

    /**
     * Returns the <code>CompoundCategory</code> of the 1st modifier
     * or null if it has not been set
     * @return the <code>CompoundCategory</code> of the 1st modifier
     */
    public CompoundCategory getModifier1Category() {
        return this.mod1Cat;
    }

    /**
     * Returns the second modifier of this <code>CompoundInfo</code>
     * or null if it has not been set
     * @return the second modifier of this <code>CompoundInfo</code>
     */
    public String getModifier2() {
        return this.modifier2;
    }

    /**
     * Returns the <code>CompoundCategory</code> of the 2nd modifier
     * or null if it has not been set
     * @return the <code>CompoundCategory</code> of the 2nd modifier
     */
    public CompoundCategory getModifier2Category() {
        return this.mod2Cat;
    }

    /**
     * Returns the head of this <code>CompoundInfo</code>
     * @return the head of this <code>CompoundInfo</code>
     */
    public String getHead() {
        return this.head;
    }

    /**
     * Returns the <code>CompoundProperty</code> of the head
     * or null if it has not been set
     * @return the <code>CompoundProperty</code> of the head
     */
    public CompoundProperty getHeadProperty() {
        return this.headProp;
    }

    @Override
    public String toString() {
        String compAsString = "";

        if (this.modProp != null) compAsString += "<" + this.modProp + "> ";
        compAsString += this.modifier1;
        if (this.mod1Cat != null) compAsString += " (" + this.mod1Cat + ")";

        if (this.modifier2 != null) {
            compAsString += " / " + this.modifier2;
            if (this.mod2Cat != null) compAsString += " (" + this.mod2Cat + ")";
        }

        compAsString += " + ";
        if (this.headProp != null) compAsString += "<" + this.headProp + "> ";
        compAsString += this.head;

        return compAsString;
    }

}
