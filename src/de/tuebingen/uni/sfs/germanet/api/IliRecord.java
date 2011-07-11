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
package de.tuebingen.uni.sfs.germanet.api;

import java.util.*;

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
    private int pwn20Sense;
    private String pwn20Id;
    private String pwn30Id;
    private String source;
    private ArrayList<String> englishSynonyms;

    /**
     * Constructs an <code>IliRecord</code> with the specified attributes.
     * @param iliId unique identifier
     * @param lexUnitId the identifier of the <code>LexUnit</code>
     * @param gnWord the orthographic form of this <code>IliRecord</code>
     * @param ewnRelation the EuroWordNet cross-language relation
     * @param pwnWord the corresponding English word
     * @param pwn20Sense the sense of the corresponding English word from Princeton WordNet 2.0
     * @param pwn20Id the identifier of the corresponding English word from Princeton WordNet 2.0
     * @param pwn30Id the identifier of the corresponding English word from Princeton WordNet 3.0
     * @param source the source of this <code>IliRecord</code>
     */
    IliRecord(int iliId, int lexUnitId, String gnWord, String ewnRelation,
            String pwnWord, int pwn20Sense, String pwn20Id, String pwn30Id, String source) {
        this.id = iliId;
        this.lexUnitId = lexUnitId;
        this.gnWord = gnWord;
        this.ewnRelation = ewnRelation;
        this.pwnWord = pwnWord;
        this.pwn20Sense = pwn20Sense;
        this.pwn20Id = pwn30Id;
        this.pwn30Id = pwn30Id;
        this.source = source;
        this.englishSynonyms = new ArrayList<String>();
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
     * Returns the sense of the corresponding English word from Princeton WordNet 2.0.
     * @return the sense of the corresponding English word from Princeton WordNet 2.0
     */
    public int getPwn20Sense() {
        return pwn20Sense;
    }

    /**
     * Returns the identifier of the corresponding English word from Princeton WordNet 2.0.
     * @return the identifier of the corresponding English word from Princeton WordNet 2.0
     */
    public String getPwn20Id() {
        return pwn20Id;
    }

    /**
     * Returns the identifier of the corresponding English word from Princeton WordNet 3.0.
     * @return the identifier of the corresponding English word from Princeton WordNet 3.0
     */
    public String getPwn30Id() {
        return pwn30Id;
    }

    /**
     * Returns the source of the <code>IliRecord</code>.
     * @return the source of the <code>IliRecord</code>
     */
    public String getSource() {
        return source;
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
                ", PWN 2.0 sense: " + this.pwn20Sense +
                ", PWN 2.0 ID: " + this.pwn20Id +
                ", PWN 3.0 ID: " + this.pwn30Id +
                ", source: " + this.source;
        if (englishSynonyms.size() > 0) {
            stringIli += "\nEnglish synonyms from PWN 2.0: ";
            for (String synonym : englishSynonyms)
                stringIli += synonym + ", ";
            stringIli = stringIli.substring(0, stringIli.length() - 2);
        }
        return stringIli;
    }

    /**
     * Adds an English synonym to this <code>IliRecord</code>.
     * @param synonym the English synonym to add
     */
    protected void addEnglishSynonym(String synonym) {
        this.englishSynonyms.add(synonym);
    }

    /**
     * Returns a <code>List</code> of this <code>IliRecord</code>'s
     * English synonyms.
     * @return a <code>List</code> of this <code>IliRecord</code>'s
     * English synonyms
     */
    public List<String> getEnglishSynonyms() {
        return (List<String>) this.englishSynonyms.clone();
    }
}

