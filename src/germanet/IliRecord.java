/*
 * Copyright (C) 2011 Verena Henrich, Department of General and Computational
 * Linguistics, University of Tuebingen
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
package germanet;

/**
 * An <code>IliRecord</code> consists of a gnWord (represented as a String),
 * an ewnRelation, a pwnWord, a pwnReading and a pwnId.
 *
 * Methods are provided to get each of the attributes.<br><br>
 *
 * The orthographic form (gnWord) can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;String orthForm = anIliRecord.getGnWord();<br><br>
 * </code>
 *
 * @author Verena Henrich (verena.henrich at uni-tuebingen.de)
 * @version 7.0
 */
public class IliRecord {

    private int id;
    private int lexUnitId;
    private String gnWord;
    private String ewnRelation;
    private String pwnWord;
    private int pwnSense;
    private String pwnId;

    /**
     * Constructs an <code>IliRecord</code> with the specified attributes.
     * @param iliId unique identifier
     * @param lexUnitId the identifier of the <code>LexUnit</code>
     * @param gnWord the orthographic form of this <code>IliRecord</code>
     * @param ewnRelation the EuroWordNet cross-language relation
     * @param pwnWord the corresponding English word
     * @param pwnSense the sense of the corresponding English word
     * @param pwnId the identifier of the corresponding English word
     */
    IliRecord(int iliId, int lexUnitId, String gnWord, String ewnRelation,
            String pwnWord, int pwnSense, String pwnId) {
        this.id = iliId;
        this.lexUnitId = lexUnitId;
        this.gnWord = gnWord;
        this.ewnRelation = ewnRelation;
        this.pwnWord = pwnWord;
        this.pwnSense = pwnSense;
        this.pwnId = pwnId;
    }

    /**
     * Returns the unique identifier of this <code>IliRecord</code>.
     * @return the unique identifier of this <code>IliRecord</code>
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the identifier of the <code>LexUnit</code>
     * corresponding to this <code>IliRecord</code>.
     * @return the identifier of the <code>LexUnit</code>
     */
    public int getLexUnitId() {
        return lexUnitId;
    }

    /**
     * Returns the orthographic form of this <code>IliRecord</code>.
     * @return the orthographic form of this <code>IliRecord</code>
     */
    public String getGnWord() {
        return gnWord;
    }

    /**
     * Returns the EuroWordNet cross-language relation of this <code>IliRecord</code>.
     * @return the EuroWordNet cross-language relation of this <code>IliRecord</code>
     */
    public String getEwnRelation() {
        return ewnRelation;
    }

    /**
     * Returns the corresponding English word from Princeton WordNet.
     * @return the corresponding English word from Princeton WordNet
     */
    public String getPwnWord() {
        return pwnWord;
    }

    /**
     * Returns the sense of the corresponding English word from Princeton WordNet.
     * @return the sense of the corresponding English word from Princeton WordNet
     */
    public int getPwnSense() {
        return pwnSense;
    }

    /**
     * Returns the identifier of the corresponding English word from Princeton WordNet.
     * @return the identifier of the corresponding English word from Princeton WordNet
     */
    public String getPwnId() {
        return pwnId;
    }

    /**
     * Returns a <code>String</code> representation of this <code>IliRecord</code>.
     * @return a <code>String</code> representation of this <code>IliRecord</code>
     */
    @Override
    public String toString() {
        String stringIli = "ILI ID: " + this.id +
                ", LexUnit ID: " + this.lexUnitId +
                ", gnWord: " + this.gnWord +
                ", EWN relation: " + this.ewnRelation +
                ", PWN word: " + this.pwnWord +
                ", PWN sense: " + this.pwnSense +
                ", PWN ID: " + this.pwnId;
        return stringIli;
    }
}

