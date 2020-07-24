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

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;

/**
 * A <code>Synset</code> belongs to a <code>WordCategory</code>
 * (<code>WordCategory.adj</code>, <code>WordCategory.nomen</code>,
 * <code>WordCategory.verben</code>) and
 * consists of a paraphrase (Strings) and a list of <code>LexUnit</code>s.
 * The List of <b>LexUnit</b>s is never empty.<br>
 * A <code>Synset</code> also has the conceptual relations (<code>ConRel</code>),
 * of which hypernymy, hyponymy, meronymy, and holonymy
 * are transitive:<br><br>
 *
 * <code>ConRel.has_hypernym</code>, <code>ConRel.has_hyponym</code>,<br>
 * <code>ConRel.has_component_meronym</code>, <code>ConRel.has_component_holonym</code>,<br>
 * <code>ConRel.has_member_meronym</code>, <code>ConRel.has_member_holonym</code>,<br>
 * <code>ConRel.has_substance_meronym</code>, <code>ConRel.has_substance_holonym</code>,<br>
 * <code>ConRel.has_portion_meronym</code>, <code>ConRel.has_portion_holonym</code>,<br>
 * <code>ConRel.entails</code>, <code>ConRel.is_entailed_by</code>,<br>
 * <code>ConRel.is_related_to</code>,<br>
 * <code>ConRel.causes</code><br><br>
 * <p>
 * Methods are provided to get the <code>WordCategory</code>, paraphrase, and
 * the <code>LexUnit</code>s.<br><br>
 * <p>
 * Conceptual relations can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;Synset&gt; hypernyms = aSynset.getRelatedLexUnits(ConRel.has_hypernym);<br><br>
 * </code>
 * Transitive relations can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;List&lt;Synset&gt;&gt; meronyms = aSynset.getTransRelatedSynsets(ConRel.meronymy);<br>
 * </code>
 * which returns a List of Lists, each representing the Synsets found at a depth.<br><br>
 * Neighbors (all Synsets that are related to this one) can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;List&lt;Synset&gt; neighbors = aSynset.getRelatedLexUnits();<br><br>
 * </code>
 * <p>
 * Unless otherwise stated, methods will return an empty List rather than null
 * to indicate that no objects exist for the given request.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
public class Synset implements Comparable {
    private int id;
    private WordCategory wordCategory;
    private WordClass wordClass;
    private ObjectOpenHashSet<LexUnit> lexUnits;
    private String paraphrase;
    private ObjectList<String> allOrthForms;

    // for semantic relatedness utils
    private Int2IntMap distanceMap;
    private IntSet hypernymIds;
    private int maxDistance; // to any hypernym on path to root
    private int depth; // distance from root

    // Relations of this Synset
    private Object2ObjectMap<ConRel, ObjectSet<Synset>> outgoingRelations;
    private Object2ObjectMap<ConRel, ObjectSet<Synset>> incomingRelations;

    /**
     * Constructs a <code>Synset</code> with the specified attributes.
     *
     * @param id           unique identifier
     * @param wordCategory the <code>WordCategory</code> of this <code>Synset</code>
     * @param wordClass    the <code>WordClass</code> of this <code>Synset</code>
     */
    Synset(int id, WordCategory wordCategory, WordClass wordClass) {
        this.id = id;
        this.wordCategory = wordCategory;
        this.wordClass = wordClass;
        lexUnits = new ObjectOpenHashSet<>();
        paraphrase = "";
        outgoingRelations = new Object2ObjectOpenHashMap<>();
        incomingRelations = new Object2ObjectOpenHashMap<>();
        maxDistance = 0;
        distanceMap = new Int2IntOpenHashMap();
        distanceMap.defaultReturnValue(-1);
        distanceMap.put(id, 0);
        allOrthForms = null;
    }

    /**
     * Returns the <code>WordCategory</code> that this <code>Synset</code> belongs to.
     *
     * @return the <code>WordCategory</code> that this <code>Synset</code> belongs to
     */
    public WordCategory getWordCategory() {
        return wordCategory;
    }

    /**
     * Returns the <code>WordClass</code> that this <code>Synset</code> belongs to.
     *
     * @return the <code>WordClass</code> that this <code>Synset</code> belongs to
     */
    public WordClass getWordClass() {
        return wordClass;
    }

    /**
     * Return true if this <code>Synset</code> is in <code>wordCategory</code>.
     *
     * @param wordCategory the <code>WordCategory</code> (eg. nomen, verben, adj)
     * @return true if this <code>Synset</code> is in <code>wordCategory</code>
     */
    public boolean inWordCategory(WordCategory wordCategory) {
        return this.wordCategory == wordCategory;
    }

    /**
     * Return true if this <code>Synset</code> is in <code>wordClass</code>.
     *
     * @param wordClass the <code>WordClass</code>
     * @return true if this <code>Synset</code> is in <code>wordClass</code>
     */
    public boolean inWordClass(WordClass wordClass) {
        return this.wordClass == wordClass;
    }

    /**
     * Return the number of <code>LexUnits</code> in this <code>Synset</code>.
     *
     * @return the number of <code>LexUnits</code> in this <code>Synset</code>
     */
    public int numLexUnits() {
        return lexUnits.size();
    }

    /**
     * Return the unique identifier for this <code>Synset</code>.
     *
     * @return the unique identifier for this <code>Synset</code> as it appears
     * in the data files.
     */
    public int getId() {
        return id;
    }

    /**
     * Adds a <code>LexUnit</code> to this <code>Synset</code>.
     *
     * @param lexUnit the <code>LexUnit</code> to add.
     */
    void addLexUnit(LexUnit lexUnit) {
        lexUnits.add(lexUnit);
    }

    /**
     * Sets the paraphrase of this <code>Synset</code>.
     *
     * @param paraphrase the paraphrase to set for this <code>Synset</code>
     */
    void setParaphrase(String paraphrase) {
        this.paraphrase = paraphrase;
    }

    /**
     * Sets the word class of this <code>Synset</code>.
     *
     * @param wordClass the word class to set for this <code>Synset</code>
     */
    void setWordClass(WordClass wordClass) {
        this.wordClass = wordClass;
    }

    /**
     * Trims all <code>Lists</code> to conserve memory.
     */
    void trimAll() {
        // trim set of lexunits
        lexUnits.trim();

        // trim each lexunit
        ObjectIterator<LexUnit> iterator = lexUnits.iterator();
        LexUnit lu;
        while (iterator.hasNext()) {
            lu = iterator.next();
            lu.trimAll();
        }
    }

    /**
     * Returns a <code>List</code> of this <code>Synset</code>'s
     * <code>LexUnits</code>. This <code>List</code> is never empty.
     *
     * @return a <code>List</code> of this <code>Synset</code>'s
     * <code>LexUnits</code>
     */
    public List<LexUnit> getLexUnits() {
        return new ObjectArrayList<>(lexUnits);
    }

    /**
     * Returns a <code>List</code> of all orthographic forms and variants
     * contained in all <code>LexUnits</code> of this <code>Synset</code>.
     * This <code>List</code> is never empty as the <code>List</code> of
     * <code>LexUnits</code> is never empty.
     *
     * @return a <code>List</code> of all orthographic forms contained in all
     * <code>LexUnits</code> of this <code>Synset</code>
     */
    public List<String> getAllOrthForms() {
        if (allOrthForms == null) {
            Set<String> orthForms = new ObjectOpenHashSet<>();

            Iterator<LexUnit> iterator = lexUnits.iterator();
            while (iterator.hasNext()) {
                orthForms.addAll(iterator.next().getOrthForms());
            }

            allOrthForms = new ObjectArrayList<>(orthForms);
            Collections.sort(allOrthForms);
        }
        return allOrthForms;
    }

    /**
     * Returns a non-null <code>List</code> of orthographic variants contained in all
     * <code>LexUnit</code>s of this <code>Synset</code>. May be empty if no
     * <code>LexUnit</code> contains the given <code>variant</code>.
     *
     * @param variant the <code>OrthFormVariant</code> to get
     * @return a non-null, but possibly empty <code>List</code> of orthographic <code>variant</code>s contained in all
     * <code>LexUnit</code>s of this <code>Synset</code>
     */
    public List<String> getOrthForms(OrthFormVariant variant) {
        Set<String> orthForms = new ObjectOpenHashSet<>();

        Iterator<LexUnit> iterator = lexUnits.iterator();
        while (iterator.hasNext()) {
            String orthForm = iterator.next().getOrthForm(variant);
            if (orthForm != null) {
                orthForms.add(orthForm);
            }
        }
        return new ObjectArrayList<>(orthForms);
    }

    /**
     * Returns this <code>Synset</code>'s paraphrase (can be empty). This is the
     * paraphrase that was manually added to GermaNet.
     *
     * @return this <code>Synset</code>'s paraphrase
     */
    public String getParaphrase() {
        return paraphrase;
    }

    /**
     * Returns this <code>Synset</code>'s paraphrases (can be empty). This list
     * contains all paraphrases that were harvested from Wiktionary (this
     * requires a call of <code>GermaNet.loadWiktionaryParaphrases</code>
     * before) as well as GermaNet's manually added paraphrase.
     *
     * @return this <code>Synset</code>'s paraphrases
     */
    public List<String> getParaphrases() {
        List<String> rval = new ObjectArrayList<>();
        if (paraphrase.length() != 0) {
            rval.add(paraphrase);
        }
        Iterator<LexUnit> iterator = lexUnits.iterator();
        while (iterator.hasNext()) {
            List<WiktionaryParaphrase> wphrases = iterator.next().getWiktionaryParaphrases();
            for (WiktionaryParaphrase wp : wphrases) {
                rval.add(wp.getWiktionarySense());
            }
        }
        return rval;
    }

    /**
     * If <code>direction</code> is <code>RelDirection.outgoing</code>, add an
     * outgoing relation of the specified type to this <code>Synset</code>.
     * If <code>direction</code> is <code>RelDirection.incoming</code>, add an
     * incoming relation of the specified type to this <code>Synset</code>.
     *
     * @param type   the type of relation (eg. <code>ConRel.has_hypernym</code>)
     * @param target the target <code>Synset</code>
     * @param direction the direction of the relation.
     */
    void addRelation(ConRel type, Synset target, RelDirection direction) {
        Object2ObjectMap<ConRel, ObjectSet<Synset>> relations;

        relations = (direction == RelDirection.outgoing) ? outgoingRelations : incomingRelations;

        ObjectSet<Synset> related = relations.get(type);
        if (related == null) {
            related = new ObjectOpenHashSet<>(1);
        }
        related.add(target);
        relations.put(type, related);
    }

    /**
     * Returns a <code>List</code> of <code>Synsets</code> for which this
     * <code>Synset</code> has an outgoing <code>type</code> relation.
     * Same as calling:
     * <code>getRelatedSynsets(type, RelDirection.outgoing)</code>.
     *
     * @param type type of relations to retrieve
     * @return a <code>List</code> of this <code>Synset</code>'s relations of
     * type <code>type</code>
     * For example, hypernyms of this <code>Synset</code> can be retrieved with
     * the type <code>ConRel.has_hypernym</code>
     */
    public List<Synset> getRelatedSynsets(ConRel type) {
        return getRelatedSynsets(type, RelDirection.outgoing);
    }

    /**
     * Returns a <code>List</code> of <code>Synsets</code> with a conceptual
     * relation of type <code>type</code> in the given <code>direction</code>
     * to this <code>Synset</code>.
     * Outgoing relations are from this <code>Synset</code> to another <code>Synset</code>.
     * Incoming relation are from another <code>Synset</code> to this <code>Synset</code>.
     *
     * @param type the type of relation to retrieve
     * @param direction direction of the relation (incoming or outgoing)
     * @return a <code>List</code> of <code>Synset</code>s with a conceptual
     * relation of type <code>type</code> in the given <code>direction</code>
     * to this <code>Synset</code>.
     */
    public List<Synset> getRelatedSynsets(ConRel type, RelDirection direction) {
        ObjectSet<Synset> rels;
        List<Synset> rval;

        rels = (direction == RelDirection.outgoing) ? outgoingRelations.get(type) : incomingRelations.get(type);

        if (rels == null) {
            rval = new ObjectArrayList<>(0);
        } else {
            rval = new ObjectArrayList<>(rels);
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
     * Same as calling:
     * <code>getTransRelatedSynsets(type, RelDirection.outgoing)</code>
     *
     * @param type the <code>type</code> of relation (e.g.
     *             <code>ConRel.has_hypernym</code>).
     * @return the transitive closure of all relations of type <code>type</code>
     * - a <code>List</code> of <code>Lists</code> of <code>Synsets</code>
     */
    public List<List<Synset>> getTransRelatedSynsets(ConRel type) {
        return getTransRelatedSynsets(type, RelDirection.outgoing);
    }

    /**
     * Returns the transitive closure of all relations of type <code>type</code>
     * to this <code>Synset</code>, in the given direction. The direction indicates
     * whether the relations to consider are coming into this <code>Synset</code>, or
     * going out of this <code>Synset</code>.
     * A <code>List</code> of <code>Lists</code> of
     * <code>Synsets</code> is returned, where the <code>List</code> at
     * position 0 contains this <code>Synset</code>, the <code>List</code> at
     * position 1 contains the relations at depth 1, the <code>List</code> at
     * position 2 contains the relations at depth 2, and so on up to the maximum
     * depth. The size of the <code>List</code> returned indicates the maximum
     * depth.<br>
     * Returns an empty <code>List</code> if type is not transitive.
     *
     * @param type the <code>type</code> of relation (e.g.
     *             <code>ConRel.has_hypernym</code>).
     * @param direction the direction of the relations (incoming or outgoing)
     * @return the transitive closure of all relations of type <code>type</code>
     * - a <code>List</code> of <code>Lists</code> of <code>Synsets</code>
     */
    public List<List<Synset>> getTransRelatedSynsets(ConRel type, RelDirection direction) {
        List<List<Synset>> result = new ObjectArrayList<>();
        List<Synset> resultPrevDepth = new ObjectArrayList<>();
        List<Synset> resultCurDepth;

        if (!type.isTransitive()) {
            return result;
        }
        resultPrevDepth.add(this);
        result.add(resultPrevDepth);
        while (resultPrevDepth.size() > 0) {
            resultCurDepth = new ObjectArrayList<>();
            for (Synset sset : resultPrevDepth) {
                List<Synset> ssetRels = sset.getRelatedSynsets(type, direction);
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
     * <code>Synset</code> has any outgoing relation to.
     * Same as calling:
     * <code>getRelatedSynsets(RelDirection.outgoing)</code>
     *
     * @return a <code>List</code> of all of the <code>Synsets</code> that this
     * <code>Synset</code> has any relation to
     */
    public List<Synset> getRelatedSynsets() {
        return getRelatedSynsets(RelDirection.outgoing);
    }

    /**
     * Returns a <code>List</code> of all of the <code>Synsets</code> that this
     * <code>Synset</code> has any relation to, in the given direction.
     * Outgoing relations are from this <code>Synset</code> to another <code>Synset</code>.
     * Incoming relation are from another <code>Synset</code> to this <code>Synset</code>.
     *
     * @return a <code>List</code> of all of the <code>Synsets</code> that this
     * <code>Synset</code> has any relation to, in the given direction.
     */
    public List<Synset> getRelatedSynsets(RelDirection direction) {
        List<Synset> rval = new ObjectArrayList<>();
        Object2ObjectMap<ConRel, ObjectSet<Synset>> relations;

        relations = (direction == RelDirection.outgoing) ? outgoingRelations : incomingRelations;

        for (Map.Entry<ConRel, ObjectSet<Synset>> entry : relations.entrySet()) {
            rval.addAll(entry.getValue());
        }
        return rval;
    }

    /**
     * Returns a <code>String</code> representation of this <code>Synset</code>.
     *
     * @return a <code>String</code> representation of this <code>Synset</code>
     */
    @Override
    public String toString() {
        String synsetAsString = "id: " + getId() + ", orth forms: " + getAllOrthForms().toString();
        if (!getParaphrases().isEmpty()) {
            synsetAsString += ", paraphrases: ";
            for (String para : getParaphrases()) {
                synsetAsString += para + "; ";
            }
            synsetAsString = synsetAsString.substring(0, synsetAsString.length() - 2);
        }

        return synsetAsString;
    }

    /**
     * Returns a <code>List</code> of all of the <code>IliRecords</code> that this
     * <code>Synset</code> is associated with.
     *
     * @return a <code>List</code> of all of the <code>IliRecords</code> that this
     * <code>Synset</code> is associated with
     */
    public List<IliRecord> getIliRecords() {
        List<IliRecord> iliRecords = new ArrayList<>();
        for (LexUnit unit : lexUnits) {
            for (IliRecord ili : unit.getIliRecords()) {
                iliRecords.add(ili);
            }
        }
        return iliRecords;
    }

    /**
     * Find the least common subsumer(s) of this synset and the input synset. The least common subsumer of two
     * synsets is the closest common parent, using hypernym relations only.
     *
     * @param otherSynset the other synset
     * @return a set of LeastCommonSubsumer objects, each of which contains a synset ID of a synset that is
     * a common parent of both this synset and the input synset, and which has the shortest possible distance of all common parents.
     * It is possible that multiple least common subsumers exist, in which case
     * all least common subsumers will have the same, shortest, distance. Returns null if both synsets do not belong
     * to the same WordCategory.
     */
    Set<LeastCommonSubsumer> getLeastCommonSubsumers(Synset otherSynset) {

        if (otherSynset == null || !otherSynset.inWordCategory(wordCategory)) {
            return null;
        }

        Set<LeastCommonSubsumer> rval = new HashSet<>();
        int shortestDistance = Integer.MAX_VALUE;

        // the intersection of the hypernyms are the common subsumers
        IntSet hypernymIds = getHypernymIds();
        IntIterator iter = hypernymIds.iterator();
        IntSet otherHypernymIds = otherSynset.getHypernymIds();
        int otherId = otherSynset.getId();

        // find all of the common subsumers with the shortest distance between the 2 synsets
        int hypernymID;
        while (iter.hasNext()) {
            hypernymID = iter.nextInt();
            if (otherHypernymIds.contains(hypernymID)) {
                int distance = getDistanceToHypernym(hypernymID) + otherSynset.getDistanceToHypernym(hypernymID);
                if (distance < shortestDistance) {
                    rval.clear();
                    rval.add(new LeastCommonSubsumer(hypernymID, new IntOpenHashSet(new int[]{id, otherId}), distance));
                    shortestDistance = distance;
                } else if (distance == shortestDistance) {
                    rval.add(new LeastCommonSubsumer(hypernymID, new IntOpenHashSet(new int[]{id, otherId}), distance));
                }
            }
        }
        return rval;
    }

    /**
     *  Get the shortest distance to otherSynset using hypernym / hyponym relations only. Both
     *  Synsets must belong to the same WordCategory.
     *
     * @param otherSynset the other synset
     * @return The distance to otherSynset, or null if otherSynset does not belong to the
     * same WordCategory as this synset.
     */
    Integer getDistanceToSynset(Synset otherSynset) {

        if (otherSynset == null || !otherSynset.inWordCategory(wordCategory)) {
            return null;
        }

        // all LCS's have the same distance, just get the first one
        return getLeastCommonSubsumers(otherSynset).iterator().next().getDistance();
    }

    /**
     * Add the input hypernymID and distance to the distance map (or replace the distance
     * if hypernymID is already in the map). Also adjust maxDistance (to any hypernym) and
     * depth (from ROOT), if necessary.
     * @param hypernymID synset ID of the hypernym
     * @param distance distance from this synset to the hypernym
     */
    void updateDistanceMap(int hypernymID, int distance) {
        int curDist = distanceMap.get(hypernymID);
        distanceMap.put(hypernymID, distance);
        if ((distance > maxDistance) || (curDist == maxDistance)) {
            maxDistance = distance;
        }

        if (hypernymID == GermaNet.GNROOT_ID) {
            this.depth = distance;
        }
    }

    /**
     * Return the set of all synset IDs that are on a path from this synset to ROOT,
     * using hypernym relations.
     * @return the set of all synset IDs that are on a path from this synset to ROOT,
     * using hypernym relations
     */
    IntSet getHypernymIds() {
        if (hypernymIds == null) {
            hypernymIds = new IntOpenHashSet(distanceMap.keySet());
        }
        return hypernymIds;
    }

    /**
     * Return the maximum distance between this synset and another synset on the path
     * to ROOT, using edge counting and hypernym relations. Note: the distance to ROOT
     * is not always the max distance.
     * @return the maximum distance between this synset and another synset on the path
     * to ROOT
     */
    int getMaxDistance() {
        return maxDistance;
    }

    /**
     * Return the depth of this synset. This is the length, using edge counting of hypernym relations,
     * from this synset to ROOT. If there are multiple paths to ROOT, the length of the shortest path is returned.
     * @return the depth of this synset
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Get the distance from this synset to one of the hypernyms on the path to ROOT, or -1 if
     * the hypernym is not on the path.
     *
     * @param hypernymID ID of the hypernym
     * @return the distance from this synset to one of the hypernyms on the path to ROOT, or -1 if
     * the hypernym is not on the path
     */
    int getDistanceToHypernym(int hypernymID) {
        return distanceMap.getOrDefault(hypernymID, -1);
    }

    /**
     * Return true if this <code>Synset</code> is equal to another <code>Synset</code>.
     *
     * @param o the <code>Synset</code> to compare to
     * @return true if this <code>Synset</code> is equal to another <code>Synset</code>
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Synset synset = (Synset) o;
        return id == synset.id &&
                wordCategory == synset.wordCategory &&
                wordClass == synset.wordClass;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wordCategory, wordClass);
    }

    /**
     * Return 1 if this <code>Synset</code> has a larger id than another <code>Synset</code>,
     * -1 if it has a smaller one.
     *
     * @param otherSynset the <code>Synset</code> to compare to
     * @return true if this <code>Synset</code> is equal to another <code>Synset</code>
     */
    @Override
    public int compareTo(Object otherSynset) {
        return ((Integer) this.getId()).compareTo((Integer) ((Synset) otherSynset).getId());
    }
}
