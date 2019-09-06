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
 * A <code>CompoundInfo</code> shows constituent parts of a German word, their
 * word categories and attributes.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
public class CompoundInfo {
    private String modifier1;
    private CompoundProperty modifier1Property;
    private CompoundCategory modifier1Category;
    private String modifier2;
    private CompoundProperty modifier2Property;
    private CompoundCategory modifier2Category;
    private String head;
    private CompoundProperty headProperty;

    /**
     * Constructs a <code>CompoundInfo</code> with the specified attributes.
     *
     * @param modifier1         modifier1
     * @param modifier1Property modifier1 property
     * @param modifier1Category modifier1 category
     * @param modifier2         modifier2
     * @param modifier2Property modifier2 property
     * @param modifier2Category modifier2 category
     * @param head              head
     * @param headProperty      head property
     */
    public CompoundInfo(String modifier1,
                        CompoundProperty modifier1Property,
                        CompoundCategory modifier1Category,
                        String modifier2,
                        CompoundProperty modifier2Property,
                        CompoundCategory modifier2Category,
                        String head, CompoundProperty headProperty) {
        this.modifier1 = modifier1;
        this.modifier1Property = modifier1Property;
        this.modifier1Category = modifier1Category;
        this.modifier2 = modifier2;
        this.modifier2Property = modifier2Property;
        this.modifier2Category = modifier2Category;
        this.head = head;
        this.headProperty = headProperty;
    }

    /**
     * Returns the first modifier of the compound
     *
     * @return the first modifier of the compound
     */
    public String getModifier1() {
        return this.modifier1;
    }

    /**
     * Returns the <code>CompoundProperty</code> of the first modifier
     * or null if it has not been set
     *
     * @return the <code>CompoundProperty</code> of the first modifier
     */
    public CompoundProperty getModifier1Property() {
        return this.modifier1Property;
    }

    /**
     * Returns the <code>CompoundCategory</code> of the first modifier
     * or null if it has not been set
     *
     * @return the <code>CompoundCategory</code> of the first modifier
     */
    public CompoundCategory getModifier1Category() {
        return this.modifier1Category;
    }

    /**
     * Returns the second, alternative modifier of the compound
     * or null if it has not been set
     *
     * @return the second modifier of the compound
     */
    public String getModifier2() {
        return this.modifier2;
    }

    /**
     * Returns the <code>CompoundProperty</code> of the second modifier
     * or null if it has not been set
     *
     * @return the <code>CompoundProperty</code> of the second modifier
     */
    public CompoundProperty getModifier2Property() {
        return this.modifier2Property;
    }

    /**
     * Returns the <code>CompoundCategory</code> of the second modifier
     * or null if it has not been set
     *
     * @return the <code>CompoundCategory</code> of the second modifier
     */
    public CompoundCategory getModifier2Category() {
        return this.modifier2Category;
    }

    /**
     * Returns the head of the compound
     *
     * @return the head of the compound
     */
    public String getHead() {
        return this.head;
    }

    /**
     * Returns the <code>CompoundProperty</code> of the head
     * or null if it has not been set
     *
     * @return the <code>CompoundProperty</code> of the head
     */
    public CompoundProperty getHeadProperty() {
        return this.headProperty;
    }

    /**
     * Return a String representation of the compound.
     *
     * @return a String representation of the compound.
     */
    @Override
    public String toString() {
        String compAsString = "";

        if (this.modifier1Property != null) compAsString += "<" + this.modifier1Property + "> ";
        compAsString += this.modifier1;
        if (this.modifier1Category != null) compAsString += " (" + this.modifier1Category + ")";

        if (this.modifier2 != null) {
            compAsString += " / ";
            if (this.modifier2Property != null) compAsString += "<" + this.modifier2Property + "> ";
            compAsString += this.modifier2;
            if (this.modifier2Category != null) compAsString += " (" + this.modifier2Category + ")";
        }

        compAsString += " + ";
        if (this.headProperty != null) compAsString += "<" + this.headProperty + "> ";
        compAsString += this.head;

        return compAsString;
    }

}
