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
 * A <code>LexUnit</code> consists of an orthForm (represented as a String),
 * an orthVar (can be empty), an oldOrthForm (can be empty), and an oldOrthVar
 * (can be empty). <code>Examples</code>, <code>Frames</code>, <code>IliRecords</code>,
 * and <code>WiktionaryParaphrases</code> can belong to a
 * <code>LexUnit</code> as well as the following
 * attributes: styleMarking (boolean), sense (int), styleMarking (boolean),
 * artificial (boolean), namedEntity (boolean), and source (String).<br>
 * A <code>LexUnit</code> also has lexical relations such as: <br><br>
 *
 * <code>LexRel.has_antonym</code>, <code>LexRel.has_synonym</code>,
 * <code>LexRel.has_pertainym</code>, <code>LexRel.has_participle</code>, etc.<br><br>
 * <p>
 * Methods are provided to get each of the attributes.<br><br>
 * <p>
 * The orthographic form can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;String orthForm = aLexUnit.getOrthForm();<br><br>
 * </code>
 * <p>
 * The orthographic form, and (if existent) the orthographic variant, the old
 * orthographic form, and the old orthographic variant can be retrieved at once:
 * <br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;String&gt; forms = aLexUnit.getAllOrthForms();<br><br>
 * </code>
 * <p>
 * Lexical relations can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;LexUnit&gt; antonyms = aLexUnit.getRelatedLexUnits(LexRel.antonymy);<br><br>
 * </code>
 * Neighbors (all LexUnits that are related to this one) can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;LexUnit&gt; neighbors = aLexUnit.getRelatedLexUnits();<br><br>
 * </code>
 * <p>
 * Unless otherwise stated, methods will return an empty List rather than null
 * to indicate that no objects exist for the given request.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
public class LexUnit {
    private int id;
    private String source;
    private boolean styleMarking, artificial, namedEntity;
    private Synset synset;
    private String orthForm, orthVar, oldOrthForm, oldOrthVar;
    private List<String> allOrthForms;
    private int sense;
    private ArrayList<Frame> frames;
    private ArrayList<Example> examples;
    private ArrayList<IliRecord> iliRecords;
    private ArrayList<WiktionaryParaphrase> wiktionaryParaphrases;
    // Relations of this LexUnit
    private EnumMap<LexRel, Set<LexUnit>> outgoingRelations;
    private EnumMap<LexRel, Set<LexUnit>> incomingRelations;
    private CompoundInfo compoundInfo;

    /**
     * Constructs a <code>LexUnit</code> with the specified attributes.
     *
     * @param id          unique identifier
     * @param synset      <code>Synset</code> to which this <code>LexUnit</code> belongs
     * @param sense       running sense number
     * @param markedStyle boolean attribute
     * @param artificial  boolean attribute
     * @param orthForm    boolean attribute
     * @param orthVar     boolean attribute
     * @param oldOrthForm boolean attribute
     * @param oldOrthVar  boolean attribute
     * @param namedEntity boolean attribute
     * @param source      source of this <code>LexUnit</code> (eg "core")
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
        this.outgoingRelations = new EnumMap<>(LexRel.class);
        this.incomingRelations = new EnumMap<>(LexRel.class);
        this.frames = new ArrayList<>();
        this.examples = new ArrayList<>();
        this.iliRecords = new ArrayList<>();
        this.wiktionaryParaphrases = new ArrayList<>();

        allOrthForms = new ArrayList<>();
        allOrthForms.add(orthForm);
        if (orthVar != null) {
            allOrthForms.add(orthVar);
        }
        if (oldOrthForm != null) {
            allOrthForms.add(oldOrthForm);
        }
        if (oldOrthVar != null) {
            allOrthForms.add(oldOrthVar);
        }
    }

    /**
     * Returns the <code>Synset</code> to which this <code>LexUnit</code> belongs.
     *
     * @return the <code>Synset</code> to which this <code>LexUnit</code> belongs
     */
    public Synset getSynset() {
        return synset;
    }

    /**
     * Returns the sense number of this <code>LexUnit</code>.
     *
     * @return the sense number of this <code>LexUnit</code>
     */
    public int getSense() {
        return sense;
    }

    /**
     * Returns the unique identifier of this <code>LexUnit</code>.
     *
     * @return the unique identifier of this <code>LexUnit</code>
     */
    public int getId() {
        return id;
    }

    /**
     * Returns true if the <code>styleMarking</code> attribute is set, false otherwise.
     *
     * @return true if the <code>styleMarking</code> attribute is set, false otherwise
     */
    public boolean isStyleMarking() {
        return styleMarking;
    }

    /**
     * Returns true if the <code>artificial</code> attribute is set, false otherwise.
     *
     * @return true if the <code>artificial</code> attribute is set, false otherwise
     */
    public boolean isArtificial() {
        return artificial;
    }

    /**
     * Returns true if the <code>namedEntity</code> attribute is set, false otherwise.
     *
     * @return true if the <code>namedEntity</code> attribute is set, false otherwise
     */
    public boolean isNamedEntity() {
        return namedEntity;
    }

    /**
     * Trims all <code>ArrayLists</code>
     */
    void trimAll() {
        frames.trimToSize();
        examples.trimToSize();
        iliRecords.trimToSize();
        wiktionaryParaphrases.trimToSize();
    }

    /**
     * Returns the orthographic form of this <code>LexUnit</code>.
     *
     * @return the orthographic form of this <code>LexUnit</code>
     */
    public String getOrthForm() {
        return orthForm;
    }

    /**
     * Returns the orthographic variant of this <code>LexUnit</code>.
     *
     * @return the orthographic variant of this <code>LexUnit</code>
     */
    public String getOrthVar() {
        return orthVar;
    }

    /**
     * Returns the old orthographic form of this <code>LexUnit</code>.
     *
     * @return the old orthographic form of this <code>LexUnit</code>
     */
    public String getOldOrthForm() {
        return oldOrthForm;
    }

    /**
     * Returns the old orthographic variant of this <code>LexUnit</code>.
     *
     * @return the old orthographic variant of this <code>LexUnit</code>
     */
    public String getOldOrthVar() {
        return oldOrthVar;
    }

    /**
     * Return the orthForm variant specified by <code>variant</code>.
     * Can be null if <code>variant</code> is not defined for this <code>LexUnit</code>.
     *
     * @param variant the orthForm variant to get
     * @return the orthForm variant specified by <code>variant</code>
     */
    public String getOrthForm(OrthFormVariant variant) {
        switch (variant) {
            case orthForm:
                return getOrthForm();
            case orthVar:
                return getOrthVar();
            case oldOrthForm:
                return getOldOrthForm();
            case oldOrthVar:
                return getOldOrthVar();
            default:
                return null;
        }
    }

    /**
     * Returns a <code>List</code> of all orthographic forms of this
     * <code>LexUnit</code> (i.e. the attributes <code>orthForm</code>,
     * <code>orthVar</code>, <code>oldOrthForm</code>, and <code>oldOrthVar</code>).
     *
     * @return a <code>List</code> of all orthographic forms of this
     * <code>LexUnit</code> (i.e. the attributes <code>orthForm</code>,
     * <code>orthVar</code>, <code>oldOrthForm</code>, and <code>oldOrthVar</code>)
     */
    public List<String> getOrthForms() {
        return allOrthForms;
    }

    /**
     * If <code>direction</code> is <code>RelDirection.outgoing</code>, add an
     * outgoing relation of the specified type to this <code>LexUnit</code>.
     * If <code>direction</code> is <code>RelDirection.incoming</code>, add an
     * incoming relation of the specified type to this <code>LexUnit</code>.
     *
     * @param type   the type of relation (eg. <code>LexRel.antonymy</code>)
     * @param target the target <code>LexUnit</code>
     * @param direction the direction of the relation.
     */
    void addRelation(LexRel type, LexUnit target, RelDirection direction) {
        EnumMap<LexRel, Set<LexUnit>> relations;

        relations =  (direction == RelDirection.outgoing) ? outgoingRelations : incomingRelations;
        Set<LexUnit> related = relations.get(type);

        if (related == null) {
            related = new HashSet<>(1);
        }
        related.add(target);
        relations.put(type, related);
    }

    /**
     * Returns a <code>List</code> of <code>LexUnits</code> for which this
     * <code>LexUnit</code> has an outgoing <code>type</code> relation.
     * Same as calling:
     * <code>getRelatedLexUnits(type, RelDirection.outgoing)</code>.
     *
     * @param type type of relation to retrieve
     * @return a <code>List</code> of <code>LexUnits</code> that have the
     * relation <code>type</code> to this <code>LexUnit</code>. For example,
     * antonyms of this <code>LexUnit</code> can be retrieved with the type
     * <code>LexRel.antonymy</code>
     */
    @SuppressWarnings("unchecked")
    public List<LexUnit> getRelatedLexUnits(LexRel type) {
        return getRelatedLexUnits(type, RelDirection.outgoing);
    }

    /**
     * Returns a <code>List</code> of <code>LexUnits</code> with a
     * lexical relation of type <code>type</code> in direction <code>direction</code>
     * to this <code>LexUnit</code>.
     * Outgoing relations are from this <code>LexUnit</code> to another <code>LexUnit</code>.
     * Incoming relations are from another <code>LexUnit</code> to this <code>LexUnit</code>.
     *
     * @param type type of relation to retrieve
     * @param direction direction of the relation (incoming or outgoing)
     * @return a <code>List</code> of <code>LexUnits</code> with a
     * lexical relation of type <code>type</code> in direction <code>direction</code>
     * to this <code>LexUnit</code>
     */
    public List<LexUnit> getRelatedLexUnits(LexRel type, RelDirection direction) {
        ArrayList<LexUnit> rval = null;
        Set<LexUnit> rels;

        // direction doesn't matter for synonyms
        if (type.equals(LexRel.has_synonym)) {
            return getSynonyms();
        } else {
            rels = (direction == RelDirection.outgoing) ? outgoingRelations.get(type) : incomingRelations.get(type);
            if (rels == null) {
                rval = new ArrayList<>(0);
            } else {
                rval = new ArrayList<>(rels);
            }
        }
        return rval;
    }

    /**
     * Returns the synonyms of this <code>LexUnit</code> - a <code>List</code>
     * of <code>LexUnits</code> that are part of this <code>LexUnit</code>'s
     * <code>Synset</code>.
     *
     * @return the synonyms of this <code>LexUnit</code>
     * Same as <code>getRelatedLexUnits(LexRel.synonymy)</code>
     */
    public List<LexUnit> getSynonyms() {
        List<LexUnit> rval = synset.getLexUnits();
        rval.remove(this);
        return rval;
    }

    /**
     * Returns a <code>List</code> of all of the <code>LexUnits</code> that this
     * <code>LexUnit</code> has any outgoing relation to.
     * Same as calling:
     * <code>getRelatedLexUnits(RelDirection.outgoing)</code>
     * @return a <code>List</code> of all of the <code>LexUnits</code> that this
     * <code>LexUnit</code> has any outgoing relation to
     */
    public List<LexUnit> getRelatedLexUnits() {
        return getRelatedLexUnits(RelDirection.outgoing);
    }

    /**
     * Returns a <code>List</code> of all of the <code>LexUnits</code> that this
     * <code>LexUnit</code> has any relation to, in the given direction.
     * Outgoing relations are from this <code>LexUnit</code> to another <code>LexUnit</code>.
     * Incoming relation are from another <code>LexUnit</code> to this <code>LexUnit</code>.
     *
     * @param direction the direction of the relation (incoming or outgoing)
     * @return a <code>List</code> of all of the <code>LexUnits</code> that this
     * <code>LexUnit</code> has any relation to, in the given direction.
     */
    public List<LexUnit> getRelatedLexUnits(RelDirection direction) {
        List<LexUnit> rval = new ArrayList<>();
        Map<LexRel, Set<LexUnit>> relations;

        relations = (direction == RelDirection.outgoing) ? outgoingRelations : incomingRelations;

        for (Map.Entry<LexRel, Set<LexUnit>> entry : relations.entrySet()) {
            rval.addAll(entry.getValue());
        }

        // include synonyms
        rval.addAll(getSynonyms());

        return rval;
    }

    /**
     * Returns a <code>String</code> representation of this <code>LexUnit</code>.
     *
     * @return a <code>String</code> representation of this <code>LexUnit</code>
     */
    @Override
    public String toString() {
        String lexUnitAsString = "id: " + id
                + ", orth form: " + orthForm;

        if (getOrthVar() != null) {
            lexUnitAsString += ", orth var: " + orthVar;
        }

        if (getOldOrthForm() != null) {
            lexUnitAsString += ", old orth form: " + oldOrthForm;
        }

        if (getOldOrthVar() != null) {
            lexUnitAsString += ", old orth var: " + oldOrthVar;
        }

        lexUnitAsString += ", synset id: " + synset.getId()
                + ", sense: " + sense
                + ", source: " + source
                + ", named entity: " + namedEntity
                + ", artificial: " + artificial
                + ", style marking: " + styleMarking;

        return lexUnitAsString;
    }

    /**
     * Adds an <code>Example</code> to this <code>Synset</code>.
     *
     * @param example the <code>Example</code> to add
     */
    void addExample(Example example) {
        examples.add(example);
    }

    /**
     * Adds a <code>Frame</code> to this <code>Synset</code>.
     *
     * @param frame the <code>Frame</code> to add
     */
    void addFrame(Frame frame) {
        frames.add(frame);
    }

    /**
     * Returns the source of this <code>LexUnit</code>.
     *
     * @return the source of this <code>LexUnit</code>
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns a <code>List</code> of this <code>LexUnit</code>'s
     * <code>Examples</code>.
     *
     * @return a <code>List</code> of this <code>LexUnit</code>'s
     * <code>Examples</code>
     */
    public List<Example> getExamples() {
        return new ArrayList<>(examples);
    }

    /**
     * Returns a <code>List</code> of this <code>LexUnit</code>'s
     * <code>Frames</code>.
     *
     * @return a <code>List</code> of this <code>LexUnit</code>'s
     * <code>Frames</code>
     */
    public List<Frame> getFrames() {
        return new ArrayList<>(frames);
    }

    /**
     * Return the <code>WordCategory</code> of this <code>LexUnit</code>.
     *
     * @return the <code>WordCategory</code> of this <code>LexUnit</code>
     * (eg. nomen, verben, adj).
     */
    public WordCategory getWordCategory() {
        return synset.getWordCategory();
    }

    /**
     * Return true if this <code>LexUnit</code> is in <code>wordCategory</code>.
     *
     * @param wordCategory the <code>WordCategory</code> (eg. nomen, verben, adj)
     * @return true if this <code>LexUnit</code> is in <code>wordCategory</code>
     */
    public boolean inWordCategory(WordCategory wordCategory) {
        return wordCategory == synset.getWordCategory();
    }


    /**
     * Return the <code>WordClass</code> of this <code>LexUnit</code>.
     *
     * @return the <code>WordClass</code> of this <code>LexUnit</code>
     * (eg. Menge, Allgemein).
     */
    public WordClass getWordClass() {
        return synset.getWordClass();
    }

    /**
     * Return true if this <code>LexUnit</code> is in <code>wordClass</code>.
     *
     * @param wordClass the <code>WordClass</code> (eg.Menge, Allgemein)
     * @return true if this <code>LexUnit</code> is in <code>wordClass</code>
     */
    public boolean inWordClass(WordClass wordClass) {
        return wordClass == synset.getWordClass();
    }

    /**
     * Return the number of <code>Frames</code> in this <code>Synset</code>.
     *
     * @return the number of <code>Frames</code> in this <code>Synset</code>
     */
    public int numFrames() {
        return frames.size();
    }

    /**
     * Return the number of <code>Examples</code> in this <code>Synset</code>.
     *
     * @return the number of <code>Examples</code> in this <code>Synset</code>
     */
    public int numExamples() {
        return examples.size();
    }

    /**
     * Return a <code>List</code> of <code>IliRecords</code> for this <code>LexUnit</code>.
     *
     * @return <code>List</code> of <code>IliRecords</code> for this <code>LexUnit</code>
     */
    public List<IliRecord> getIliRecords() {
        return new ArrayList<>(iliRecords);
    }

    /**
     * Add an <code>IliRecord</code> to this <code>LexUnit</code>.
     *
     * @param record <code>IliRecord</code> to add to this <code>LexUnit</code>
     */
    void addIliRecord(IliRecord record) {
        iliRecords.add(record);
    }

    /**
     * Return the <code>CompoundInfo</code> for this <code>LexUnit</code>, if it exists.
     *
     * @return the <code>CompoundInfo</code> for this <code>LexUnit</code>
     */
    public CompoundInfo getCompoundInfo() {
        return compoundInfo;
    }

    /**
     * Set the <code>CompoundInfo</code> for this <code>LexUnit</code>.
     *
     * @param compoundInfo the <code>CompoundInfo</code> for this <code>LexUnit</code>
     */
    void setCompoundInfo(CompoundInfo compoundInfo) {
        this.compoundInfo = compoundInfo;
    }

    /**
     * Return a <code>List</code> of <code>WiktionaryParaphrases</code>
     * for this <code>LexUnit</code>.
     *
     * @return <code>List</code> of <code>WiktionaryParaphrase</code>
     * for this <code>LexUnit</code>
     */
    public List<WiktionaryParaphrase> getWiktionaryParaphrases() {
        return new ArrayList<>(wiktionaryParaphrases);
    }

    /**
     * Add a <code>WiktionaryParaphrase</code> to this <code>LexUnit</code>.
     *
     * @param paraphrase <code>WiktionaryParaphrase</code> to add to this <code>LexUnit</code>
     */
    public void addWiktionaryParaphrase(WiktionaryParaphrase paraphrase) {
        wiktionaryParaphrases.add(paraphrase);
    }

    /**
     * Return true if this <code>LexUnit</code> is equal to another <code>LexUnit</code>.
     *
     * @param o the <code>LexUnit</code> to compare to
     * @return true if this <code>LexUnit</code> is equal to another <code>LexUnit</code>
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LexUnit lexUnit = (LexUnit) o;

        if (id != lexUnit.id) return false;
        return orthForm.equals(lexUnit.orthForm);
    }

    /**
     * Return the hashcode for this object
     *
     * @return the hashcode for this object
     */
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + orthForm.hashCode();
        return result;
    }
}
