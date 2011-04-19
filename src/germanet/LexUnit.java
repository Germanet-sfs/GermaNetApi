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

import java.util.*;

/**
 * A <code>LexUnit</code> consists of an orthForm (represented as a Strings),
 * an orthVar (can be empty), an oldOrthForm (can be empty), and an oldOrthVar
 * (can be empty). <code>Examples</code> and <code>Frames</code> can belong to a
 * <code>LexUnit</code> as well as the following
 * attributes: styleMarking (boolean), sense (int), styleMarking (boolean),
 * artificial (boolean), namedEntity (boolean), and source (String).<br>
 * A <code>LexUnit</code> also has the lexical relations: <br><br>
 * 
 * <code>LexRel.has_antonym</code>, <code>LexRel.has_synonym</code>,
 * <code>LexRel.has_pertainym</code>, <code>LexRel.has_participle</code><br><br>
 * 
 * Methods are provided to get each of the attributes.<br><br>
 * 
 * The orthographic form can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;String orthForm = aLexUnit.getOrthForm();<br><br>
 * </code>

 * The orthographic form, and (if existent) the orthographic variant, the old
 * orthographic form, and the old orthographic variant can be retrieved at once:
 * <br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;String&gt; forms = aLexUnit.getAllOrthForms();<br><br>
 * </code>
 * 
 * Lexical relations can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;LexUnit&gt; antonyms = aLexUnit.getRelatedLexUnits(LexRel.antonymy);<br><br>
 * </code>
 * Neighbors (all LexUnits that are related to this one) can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;LexUnit&gt; neighbors = aLexUnit.getRelatedLexUnits();<br><br>
 * </code>
 *
 * Unless otherwise stated, methods will return an empty List rather than null
 * to indicate that no objects exist for the given request. 
 * 
 * @author Verena Henrich (verena.henrich at uni-tuebingen.de)
 * @version 6.0
 */
public class LexUnit {
    private int id;
    private String source;
    private boolean styleMarking, artificial, namedEntity;
    private Synset synset;
    private String orthForm, orthVar, oldOrthForm, oldOrthVar;
    private int sense;
    private ArrayList<Frame> frames;
    private ArrayList<Example> examples;

    // Relations of this LexUnit
    private EnumMap<LexRel, ArrayList<LexUnit>> relations;

    /**
     * Constructs a <code>LexUnit</code> with the specified attributes.
     * @param id unique identifier
     * @param synset <code>Synset</code> to which this <code>LexUnit</code> belongs
     * @param sense running sense number
     * @param markedStyle boolean attribute
     * @param artificial boolean attribute
     * @param orthForm boolean attribute
     * @param orthVar boolean attribute
     * @param oldOrthForm boolean attribute
     * @param oldOrthVar
     * @param namedEntity boolean attribute
     * @param source source of this <code>LexUnit</code> (eg "core")
     */
    LexUnit(int id, Synset synset, int sense,
            boolean markedStyle, boolean artificial, String orthForm, String orthVar,
            String oldOrthForm, String oldOrthVar, boolean namedEntity, String source) {
        this.id = id;
        this.synset = synset;
        this.sense = sense;
        this.styleMarking = markedStyle;
        this.artificial = artificial;
        this.orthForm = orthForm;
        this.orthVar = orthVar;
        this.oldOrthForm = oldOrthForm;
        this.oldOrthVar = oldOrthVar;
        this.namedEntity = namedEntity;
        this.source = source;
        this.relations = new EnumMap<LexRel, ArrayList<LexUnit>>(LexRel.class);
        this.frames = new ArrayList<Frame>();
        this.examples = new ArrayList<Example>();
    }

    /**
     * Returns the <code>Synset</code> to which this <code>LexUnit</code> belongs.
     * @return the <code>Synset</code> to which this <code>LexUnit</code> belongs
     */
    public Synset getSynset() {
        return this.synset;
    }

    /**
     * Returns the sense number of this <code>LexUnit</code>.
     * @return the sense number of this <code>LexUnit</code>
     */
    public int getSense() {
        return this.sense;
    }

    /**
     * Returns the unique identifier of this <code>LexUnit</code>.
     * @return the unique identifier of this <code>LexUnit</code>
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns true if the <code>styleMarking</code> attribute is set, false otherwise.
     * @return true if the <code>styleMarking</code> attribute is set, false otherwise
     */
    public boolean isStyleMarking() {
        return this.styleMarking;
    }

    /**
     * Returns true if the <code>artificial</code> attribute is set, false otherwise.
     * @return true if the <code>artificial</code> attribute is set, false otherwise
     */
    public boolean isArtificial() {
        return this.artificial;
    }

    /**
     * Returns true if the <code>namedEntity</code> attribute is set, false otherwise.
     * @return true if the <code>namedEntity</code> attribute is set, false otherwise
     */
    public boolean isNamedEntity() {
        return this.namedEntity;
    }

//    protected void setOrthForm(String orthForm) {
//        this.orthForm = orthForm;
//    }
//
//    protected void setOrthVar(String orthVar) {
//        this.orthVar = orthVar;
//    }
//
//    protected void setOldOrthForm(String oldOrthForm) {
//        this.oldOrthForm = oldOrthForm;
//    }
//
//    protected void setOldOrthVar(String oldOrthVar) {
//        this.oldOrthVar = oldOrthVar;
//    }

    /**
     * Trims all <code>ArrayLists</code>
     */
    protected void trimAll() {
        ArrayList<LexUnit> list;

        for (LexRel rel : this.relations.keySet()) {
            list = this.relations.get(rel);
            list.trimToSize();
            this.relations.put(rel, list);
        }
    }

    /**
     * Returns the orthographic form of this <code>LexUnit</code>.
     * @return the orthographic form of this <code>LexUnit</code>
     */
    public String getOrthForm() {
        return this.orthForm;
    }

    /**
     * Returns the orthographic variant of this <code>LexUnit</code>.
     * @return the orthographic variant of this <code>LexUnit</code>
     */
    public String getOrthVar() {
        return this.orthVar;
    }

    /**
     * Returns the old orthographic form of this <code>LexUnit</code>.
     * @return the old orthographic form of this <code>LexUnit</code>
     */
    public String getOldOrthForm() {
//        System.out.println(orthForm + " " + oldOrthForm);
        return this.oldOrthForm;
    }

    /**
     * Returns the old orthographic variant of this <code>LexUnit</code>.
     * @return the old orthographic variant of this <code>LexUnit</code>
     */
    public String getOldOrthVar() {
        return oldOrthVar;
    }

    /**
     * Returns a <code>List</code> of all orthographic forms of this
     * <code>LexUnit</code> (i.e. the attributes <code>orthForm</code>,
     * <code>orthVar</code>, <code>oldOrthForm</code>, and <code>oldOrthVar</code>).
     * @return a <code>List</code> of all orthographic forms of this
     * <code>LexUnit</code> (i.e. the attributes <code>orthForm</code>,
     * <code>orthVar</code>, <code>oldOrthForm</code>, and <code>oldOrthVar</code>)
     */
    public List<String> getOrthForms() {
        List<String> allOrthForms = new ArrayList<String>();
        allOrthForms.add(this.orthForm);

        if (getOrthVar() != null) {
            allOrthForms.add(this.orthVar);
        }

//        System.out.println((getOldOrthForm() != null) + " " + !allOrthForms.contains(this.oldOrthForm));
        if (getOldOrthForm() != null && !allOrthForms.contains(this.oldOrthForm)) {
//            System.out.println("if");
            allOrthForms.add(this.oldOrthForm);
        }

        if (getOldOrthVar() != null && !allOrthForms.contains(this.oldOrthVar)) {
            allOrthForms.add(this.oldOrthVar);
        }

        return allOrthForms;
    }

    /**
     * Adds a relation of the specified type to the target <code>LexUnit</code>.
     * @param type the type of relation (eg. <code>LexRel.antonymy</code>)
     * @param target the target <code>LexUnit</code>
     */
    protected void addRelation(LexRel type, LexUnit target) {
        ArrayList<LexUnit> relationList = this.relations.get(type);

        if (relationList == null) {
            relationList = new ArrayList<LexUnit>(1);
        }
        relationList.add(target);
        this.relations.put(type, relationList);
    }

    /**
     * Returns a <code>List</code> of <code>LexUnits</code> that have the
     * relation <code>type</code> to this <code>LexUnit</code>.
     * @param type type of relation to retrieve
     * @return a <code>List</code> of <code>LexUnits</code> that have the
     * relation <code>type</code> to this <code>LexUnit</code>. For example,
     * antonyms of this <code>LexUnit</code> can be retrieved with the type
     * <code>LexRel.antonymy</code>
     */
    @SuppressWarnings("unchecked")
    public List<LexUnit> getRelatedLexUnits(LexRel type) {
        ArrayList<LexUnit> rval = null;
        if (type.equals(LexRel.has_synonym)) {
            return getSynonyms();
        } else {
            rval = this.relations.get(type);
            if (rval == null) {
                rval = new ArrayList<LexUnit>(0);
            } else {
                rval = (ArrayList<LexUnit>) rval.clone();
            }
        }
        return rval;
    }

    /**
     * Returns the synonyms of this <code>LexUnit</code> - a <code>List</code>
     * of <code>LexUnits</code> that are part of this <code>LexUnit</code>'s
     * <code>Synset</code>.
     * @return  the synonyms of this <code>LexUnit</code>
     * Same as <code>getRelatedLexUnits(LexRel.synonymy)</code>
     */
    public List<LexUnit> getSynonyms() {
        List<LexUnit> rval = this.synset.getLexUnits();
        rval.remove(this);
        return rval;
    }

    /**
     * Returns a <code>List</code> of all of the <code>LexUnits</code> that this
     * <code>LexUnit</code> has any relation to.
     * @return a <code>List</code> of all of the <code>LexUnits</code> that this
     * <code>LexUnit</code> has any relation to
     */
    public List<LexUnit> getRelatedLexUnits() {
        List<LexUnit> rval = new ArrayList<LexUnit>();

        for (Map.Entry<LexRel, ArrayList<LexUnit>> entry : this.relations.entrySet()) {
            rval.addAll(entry.getValue());
        }
        return rval;
    }

    /**
     * Returns a <code>String</code> representation of this <code>LexUnit</code>.
     * @return a <code>String</code> representation of this <code>LexUnit</code>
     */
    @Override
    public String toString() {
        String lexUnitAsString = "id: " + this.id +
                ", orth form: " + this.orthForm;

        if (getOrthVar() != null) {
            lexUnitAsString += ", orth var: " + this.orthVar;
        }

        if (getOldOrthForm() != null) {
            lexUnitAsString += ", old orth form: " + this.oldOrthForm;
        }

        if (getOldOrthVar() != null) {
            lexUnitAsString += ", old orth var: " + this.oldOrthVar;
        }

        lexUnitAsString += ", synset id: " + this.synset.getId() +
                ", sense: " + this.sense +
                ", source: " + this.source +
                ", named entity: " + this.namedEntity +
                ", artificial: " + this.artificial +
                ", style marking: " + this.styleMarking;

        return lexUnitAsString;
    }

    /**
     * Adds an <code>Example</code> to this <code>Synset</code>.
     * @param example the <code>Example</code> to add
     */
    protected void addExample(Example example) {
        this.examples.add(example);
    }

    /**
     * Adds a <code>Frame</code> to this <code>Synset</code>.
     * @param frame the <code>Frame</code> to add
     */
    protected void addFrame(Frame frame) {
        this.frames.add(frame);
    }

    /**
     * Returns the source of this <code>LexUnit</code>.
     * @return the source of this <code>LexUnit</code>
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Returns a <code>List</code> of this <code>LexUnit</code>'s
     * <code>Examples/code>.
     * @return a <code>List</code> of this <code>LexUnit</code>'s
     * <code>Examples</code>
     */
    public List<Example> getExamples() {
        return (List<Example>) this.examples.clone();
    }

    /**
     * Returns a <code>List</code> of this <code>LexUnit</code>'s
     * <code>Frames</code>.
     * @return a <code>List</code> of this <code>LexUnit</code>'s
     * <code>Frames</code>
     */
    public List<Frame> getFrames() {
        return (List<Frame>) this.frames.clone();
    }

    /**
     * Return the <code>WordCategory</code> of this <code>LexUnit</code>.
     * @return the <code>WordCategory</code> of this <code>LexUnit</code>
     * (eg. nomen, verben, adj).
     */
    public WordCategory getWordCategory() {
        return this.synset.getWordCategory();
    }

    /**
     * Return true if this <code>LexUnit</code> is in <code>wordCategory</code>.
     * @param wordCategory the <code>WordCategory</code> (eg. nomen, verben, adj)
     * @return true if this <code>LexUnit</code> is in <code>wordCategory</code>
     */
    public boolean inWordCategory(WordCategory wordCategory) {
        return wordCategory == this.synset.getWordCategory();
    }

    /**
     * Return the number of <code>Frames</code> in this <code>Synset</code>.
     * @return the number of <code>Frames</code> in this <code>Synset</code>
     */
    public int numFrames() {
        return this.frames.size();
    }

    /**
     * Return the number of <code>Examples</code> in this <code>Synset</code>.
     * @return the number of <code>Examples</code> in this <code>Synset</code>
     */
    public int numExamples() {
        return this.examples.size();
    }
}
