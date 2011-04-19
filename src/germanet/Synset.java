/*
 * Copyright (C) 2009 Verena Henrich, Department of General and Computational
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
 * A <code>Synset</code> belongs to a <code>WordCategory</code>
 * (<code>WordCategory.adj</code>, <code>WordCategory.nomen</code>,
 * <code>WordCategory.verben</code>) and
 * consists of a paraphrase (Strings) and a list of <code>LexUnit</code>s.
 * The List of <b>LexUnit</b>s is never empty.<br>
 * A <code>Synset</code> also has the conceptual relations (<code>ConRel</code>),
 * of which hyperonymy, hyponymy, meronymy, and holonymy
 * are transitive:<br><br>
 * 
 * <code>ConRel.hyperonymy</code>, <code>ConRel.hyponymy</code>,<br>
 * <code>ConRel.meronymy</code>, <code>ConRel.holonymy</code>,<br>
 * <code>ConRel.entailment</code>, <code>ConRel.entailed</code>,<br>
 * <code>ConRel.causation</code>, <code>ConRel.caused</code>,<br>
 * <code>ConRel.association</code><br><br>
 * 
 * Methods are provided to get the <code>WordCategory</code>, paraphrase, and
 * the <code>LexUnit</code>s.<br><br>
 * 
 * Conceptual relations can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;Synset&gt; hyperonyms = aSynset.getRelatedLexUnits(ConRel.hyperonymy);<br><br>
 * </code>
 * Transitive relations can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;List&lt;Synset&gt;&gt; meronyms = aSynset.getTransRelations(ConRel.meronymy);<br>
 * </code>
 *    which returns a List of Lists, each representing the Synsets found at a depth.<br><br>
 * Neighbors (all Synsets that are related to this one) can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;Synset&gt; neighbors = aSynset.getRelatedLexUnits();<br><br>
 * </code>
 *  
 * Unless otherwise stated, methods will return an empty List rather than null
 * to indicate that no objects exist for the given request. 

 * @author Verena Henrich (verena.henrich at uni-tuebingen.de)
 * @version 2.0
 */
public class Synset {
    private int id;
    private WordCategory wordCategory;
    private ArrayList<LexUnit> lexUnits;
    private String paraphrase;

    // Relations of this Synset
    private EnumMap<ConRel, ArrayList<Synset>> relations;

    /**
     * Constructs a <code>Synset</code> with the specified attributes.
     * @param id unique identifier
     * @param wordCategory the <code>WordCategory</code> of this <code>Synset</code>
     */
    protected Synset(int id, WordCategory wordCategory) {
        this.id = id;
        this.wordCategory = wordCategory;
        lexUnits = new ArrayList<LexUnit>(0);
        paraphrase = "";
        relations = new EnumMap<ConRel, ArrayList<Synset>>(ConRel.class);
    }

    /**
     * Returns the <code>WordCategory</code> that this <code>Synset</code> belongs to.
     * @return the <code>WordCategory</code> that this <code>Synset</code> belongs to
     */
    public WordCategory getWordCategory() {
        return wordCategory;
    }

    /**
     * Return true if this <code>Synset</code> is in <code>wordCategory</code>.
     * @param wordCategory the <code>WordCategory</code> (eg. nomen, verben, adj)
     * @return true if this <code>Synset</code> is in <code>wordCategory</code>
     */
    public boolean inWordCategory(WordCategory wordCategory) {
        return wordCategory == this.wordCategory;
    }

    /**
     * Return the number of <code>LexUnits</code> in this <code>Synset</code>.
     * @return the number of <code>LexUnits</code> in this <code>Synset</code>
     */
    public int numLexUnits() {
        return lexUnits.size();
    }

    /**
     * Return the unique identifier for this <code>Synset</code>.
     * @return the unique identifier for this <code>Synset</code> as it appears
     * in the data files.
     */
    public int getId() {
        return id;
    }

    /**
     * Adds a <code>LexUnit</code> to this <code>Synset</code>.
     * @param lexUnit the <code>LexUnit</code> to add.
     */
    protected void addLexUnit(LexUnit lexUnit) {
        lexUnits.add(lexUnit);
    }

    /**
     * Sets the paraphrase of this <code>Synset</code>.
     * @param paraphrase the paraphrase to set for this <code>Synset</code>
     */
    protected void setParaphrase(String paraphrase) {
        this.paraphrase = paraphrase;
    }

    /**
     * Trims all <code>ArrayLists</code> to conserve memory.
     */
    protected void trimAll() {
        ArrayList<Synset> list;
        lexUnits.trimToSize();

        // trim LexUnits
        for (LexUnit lu : lexUnits) {
            lu.trimAll();
        }

        // trim relations
        for (ConRel rel : relations.keySet()) {
            list = relations.get(rel);
            list.trimToSize();
            relations.put(rel, list);
        }
    }

    /**
     * Returns a <code>List</code> of this <code>Synset</code>'s
     * <code>LexUnits</code>. This <code>List</code> is never empty.
     * @return a <code>List</code> of this <code>Synset</code>'s
     * <code>LexUnits</code>
     */
    @SuppressWarnings("unchecked")
    public List<LexUnit> getLexUnits() {
        return (List<LexUnit>) lexUnits.clone();
    }

//    /**
//     * Returns a <code>List</code> of all orthographic forms contained in all
//     * <code>LexUnits</code> of this <code>Synset</code>.
//     * @return a <code>List</code> of all orthographic forms contained in all
//     * <code>LexUnits</code> of this <code>Synset</code>
//     */
//    public List<String> getAllOrthForms() {
//        List<String> rval = new ArrayList<String>();
//        List<LexUnit> luList = getLexUnits();
//        for (LexUnit lu : luList) {
//            rval.add(lu.getOrthForm());
//        }
//        return rval;
//    }

    /**
     * Returns a <code>List</code> of all (old) orthographic forms and variants
     * contained in all <code>LexUnits</code> of this <code>Synset</code>.
     * This <code>List</code> is never empty as the <code>List</code> of
     * <code>LexUnits</code> is never empty.
     * @return a <code>List</code> of all orthographic forms contained in all
     * <code>LexUnits</code> of this <code>Synset</code>
     */
    public List<String> getAllOrthForms() {
        List<String> rval = new ArrayList<String>(0);
        List<LexUnit> luList = getLexUnits();
        for (LexUnit lu : luList) {
            rval.addAll(lu.getOrthForms());
        }
        return rval;
    }

    /**
     * Returns this <code>Synset</code>'s paraphrase (can be empty).
     * @return this <code>Synset</code>'s paraphrase
     */
    public String getParaphrase() {
        return this.paraphrase;
    }

    /**
     * Add a relation of the specified type to target <code>Synset</code>.
     * @param type the type of relation (eg. <code>ConRel.hyperonymy</code>)
     * @param target the target <code>Synset</code>
     */
    protected void addRelation(ConRel type, Synset target) {
        ArrayList<Synset> relList = this.relations.get(type);
        if (relList == null) {
            relList = new ArrayList<Synset>(1);
        }
        relList.add(target);
        this.relations.put(type, relList);
    }

    /**
     * Returns a <code>List</code> of this <code>Synset</code>'s relations of
     * type <code>type</code>.
     * @param type type of relations to retrieve
     * @return a <code>List</code> of this <code>Synset</code>'s relations of
     * type <code>type</code>
     * For example, hyperonyms of this <code>Synset</code> can be retrieved with
     * the type <code>ConRel.hyperonymy</code>
     */
    public List<Synset> getRelatedSynsets(ConRel type) {
        ArrayList<Synset> rval = this.relations.get(type);
        if (rval == null) {
            rval = new ArrayList<Synset>(0);
        } else {
            rval = (ArrayList<Synset>) rval.clone();
        }
        return rval;
    }

    /**
     * Returns the transitive closure of all relations of type <code>type</code>
     * to this <code>Synset</code>. A <code>List</code> of <code>Lists</code> of
     * <code>Synsets</code> is returned, where the <code>List</code> at
     * position 0 contains this <code>Synset</code>, the <code>List</code> at
     * position 1 contains the relations at depth 1, the <code>List</code> at
     * position 2 contains the relations at depth 2, and so on up to the maximum
     * depth. The size of the <code>List</code> returned indicates the maximum
     * depth.<br>
     * Returns an empty <code>List</code> if type is not transitive.
     * @param type the <code>type</code> of relation (e.g.
     * <code>ConRel.hyperonymy</code>).
     * @return the transitive closure of all relations of type <code>type</code>
     * - a <code>List</code> of <code>Lists</code> of <code>Synsets</code>
     */
    public List<List<Synset>> getTransRelations(ConRel type) {
        List<List<Synset>> result = new ArrayList<List<Synset>>();
        List<Synset> resultPrevDepth = new ArrayList<Synset>(1);
        List<Synset> resultCurDepth;

        if (!type.isTransitive()) {
            return result;
        }
        resultPrevDepth.add(this);
        result.add(resultPrevDepth);
        while (resultPrevDepth.size() > 0) {
            resultCurDepth = new ArrayList<Synset>();
            for (Synset sset : resultPrevDepth) {
                List<Synset> ssetRels = sset.getRelatedSynsets(type);
                resultCurDepth.addAll(ssetRels);
            }
            if (resultCurDepth.size() > 0) {
                result.add(resultCurDepth);
            }
            resultPrevDepth = resultCurDepth;
        }
        return result;
    }

    /**
     * Returns a <code>List</code> of all of the <code>Synsets</code> that this
     * <code>Synset</code> has any relation to.
     * @return a <code>List</code> of all of the <code>Synsets</code> that this
     * <code>Synset</code> has any relation to
     */
    public List<Synset> getRelatedSynsets() {
        List<Synset> rval = new ArrayList<Synset>();

        for (Map.Entry<ConRel, ArrayList<Synset>> entry : relations.entrySet()) {
            rval.addAll(entry.getValue());
        }
        return rval;
    }

    /**
     * Returns a <code>String</code> representation of this <code>Synset</code>.
     * @return a <code>String</code> representation of this <code>Synset</code>
     */
    @Override
    public String toString() {
        String synsetAsString = "id: " + getId() + ", orth forms: " + getAllOrthForms().toString();
        if (getParaphrase() != null) {
            synsetAsString += ", paraphrase: " + getParaphrase();
        }

        return synsetAsString;
    }
}