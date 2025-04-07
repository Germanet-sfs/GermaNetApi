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
    private String mod1LexUnitId1;
    private String mod1LexUnitId2;
    private String mod1LexUnitId3;
    private CompoundProperty modifier1Property;
    private CompoundCategory modifier1Category;
    private String modifier2;
    private String mod2LexUnitId1;
    private String mod2LexUnitId2;
    private String mod2LexUnitId3;
    private CompoundProperty modifier2Property;
    private CompoundCategory modifier2Category;
    private String head;
    private String headLexUnitId;
    private CompoundProperty headProperty;
    
	/**
	 * @param modifier1
	 * @param mod1LexUnitId1
	 * @param mod1LexUnitId2
	 * @param mod1LexUnitId3
	 * @param modifier1Property
	 * @param modifier1Category
	 * @param modifier2
	 * @param mod2LexUnitId1
	 * @param mod2LexUnitId2
	 * @param mod2LexUnitId3
	 * @param modifier2Property
	 * @param modifier2Category
	 * @param head
	 * @param headLexUnitId
	 * @param headProperty
	 */
	public CompoundInfo(String modifier1, String mod1LexUnitId1, String mod1LexUnitId2, String mod1LexUnitId3,
			CompoundProperty modifier1Property, CompoundCategory modifier1Category, String modifier2,
			String mod2LexUnitId1, String mod2LexUnitId2, String mod2LexUnitId3, CompoundProperty modifier2Property,
			CompoundCategory modifier2Category, String head, String headLexUnitId, CompoundProperty headProperty) {
		super();
		this.modifier1 = modifier1;
		this.mod1LexUnitId1 = mod1LexUnitId1;
		this.mod1LexUnitId2 = mod1LexUnitId2;
		this.mod1LexUnitId3 = mod1LexUnitId3;
		this.modifier1Property = modifier1Property;
		this.modifier1Category = modifier1Category;
		this.modifier2 = modifier2;
		this.mod2LexUnitId1 = mod2LexUnitId1;
		this.mod2LexUnitId2 = mod2LexUnitId2;
		this.mod2LexUnitId3 = mod2LexUnitId3;
		this.modifier2Property = modifier2Property;
		this.modifier2Category = modifier2Category;
		this.head = head;
		this.headLexUnitId = headLexUnitId;
		this.headProperty = headProperty;
	}

    /**
	 * @return the modifier1
	 */
	public String getModifier1() {
		return modifier1;
	}

	/**
	 * @param modifier1 the modifier1 to set
	 */
	public void setModifier1(String modifier1) {
		this.modifier1 = modifier1;
	}

	/**
	 * @return the mod1LexUnitId1
	 */
	public String getMod1LexUnitId1() {
		return mod1LexUnitId1;
	}

	/**
	 * @param mod1LexUnitId1 the mod1LexUnitId1 to set
	 */
	public void setMod1LexUnitId1(String mod1LexUnitId1) {
		this.mod1LexUnitId1 = mod1LexUnitId1;
	}

	/**
	 * @return the mod1LexUnitId2
	 */
	public String getMod1LexUnitId2() {
		return mod1LexUnitId2;
	}

	/**
	 * @param mod1LexUnitId2 the mod1LexUnitId2 to set
	 */
	public void setMod1LexUnitId2(String mod1LexUnitId2) {
		this.mod1LexUnitId2 = mod1LexUnitId2;
	}

	/**
	 * @return the mod1LexUnitId3
	 */
	public String getMod1LexUnitId3() {
		return mod1LexUnitId3;
	}

	/**
	 * @param mod1LexUnitId3 the mod1LexUnitId3 to set
	 */
	public void setMod1LexUnitId3(String mod1LexUnitId3) {
		this.mod1LexUnitId3 = mod1LexUnitId3;
	}

	/**
	 * @return the modifier1Property
	 */
	public CompoundProperty getModifier1Property() {
		return modifier1Property;
	}

	/**
	 * @param modifier1Property the modifier1Property to set
	 */
	public void setModifier1Property(CompoundProperty modifier1Property) {
		this.modifier1Property = modifier1Property;
	}

	/**
	 * @return the modifier1Category
	 */
	public CompoundCategory getModifier1Category() {
		return modifier1Category;
	}

	/**
	 * @param modifier1Category the modifier1Category to set
	 */
	public void setModifier1Category(CompoundCategory modifier1Category) {
		this.modifier1Category = modifier1Category;
	}

	/**
	 * @return the modifier2
	 */
	public String getModifier2() {
		return modifier2;
	}

	/**
	 * @param modifier2 the modifier2 to set
	 */
	public void setModifier2(String modifier2) {
		this.modifier2 = modifier2;
	}

	/**
	 * @return the mod2LexUnitId1
	 */
	public String getMod2LexUnitId1() {
		return mod2LexUnitId1;
	}

	/**
	 * @param mod2LexUnitId1 the mod2LexUnitId1 to set
	 */
	public void setMod2LexUnitId1(String mod2LexUnitId1) {
		this.mod2LexUnitId1 = mod2LexUnitId1;
	}

	/**
	 * @return the mod2LexUnitId2
	 */
	public String getMod2LexUnitId2() {
		return mod2LexUnitId2;
	}

	/**
	 * @param mod2LexUnitId2 the mod2LexUnitId2 to set
	 */
	public void setMod2LexUnitId2(String mod2LexUnitId2) {
		this.mod2LexUnitId2 = mod2LexUnitId2;
	}

	/**
	 * @return the mod2LexUnitId3
	 */
	public String getMod2LexUnitId3() {
		return mod2LexUnitId3;
	}

	/**
	 * @param mod2LexUnitId3 the mod2LexUnitId3 to set
	 */
	public void setMod2LexUnitId3(String mod2LexUnitId3) {
		this.mod2LexUnitId3 = mod2LexUnitId3;
	}

	/**
	 * @return the modifier2Property
	 */
	public CompoundProperty getModifier2Property() {
		return modifier2Property;
	}

	/**
	 * @param modifier2Property the modifier2Property to set
	 */
	public void setModifier2Property(CompoundProperty modifier2Property) {
		this.modifier2Property = modifier2Property;
	}

	/**
	 * @return the modifier2Category
	 */
	public CompoundCategory getModifier2Category() {
		return modifier2Category;
	}

	/**
	 * @param modifier2Category the modifier2Category to set
	 */
	public void setModifier2Category(CompoundCategory modifier2Category) {
		this.modifier2Category = modifier2Category;
	}

	/**
	 * @return the head
	 */
	public String getHead() {
		return head;
	}

	/**
	 * @param head the head to set
	 */
	public void setHead(String head) {
		this.head = head;
	}

	/**
	 * @return the headLexUnitId
	 */
	public String getHeadLexUnitId() {
		return headLexUnitId;
	}

	/**
	 * @param headLexUnitId the headLexUnitId to set
	 */
	public void setHeadLexUnitId(String headLexUnitId) {
		this.headLexUnitId = headLexUnitId;
	}

	/**
	 * @return the headProperty
	 */
	public CompoundProperty getHeadProperty() {
		return headProperty;
	}

	/**
	 * @param headProperty the headProperty to set
	 */
	public void setHeadProperty(CompoundProperty headProperty) {
		this.headProperty = headProperty;
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
