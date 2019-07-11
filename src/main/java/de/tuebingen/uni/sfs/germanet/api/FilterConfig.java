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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Configuration for complex filtering of <code>Synset</code> or <code>LexUnit</code> searches.</br></br>
 * Default values:</br>
 * wordCategories: ALL</br>
 * wordClasses: ALL</br>
 * orthFormVariants: ALL</br>
 * regEx: false</br>
 * ignoreCase: false</br>
 * editDistance: 0 (edit distance is ignored if using a regular expression)</br>
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.2
 */
public class FilterConfig {
    private String searchString;
    private Set<WordCategory> wordCategories;
    private Set<WordClass> wordClasses;
    private Set<OrthFormVariant> orthFormVariants;
    private boolean regEx;
    private boolean ignoreCase;
    private int editDistance;

    /**
     * Construct a new FilterConfig with the literal search term and the default values.
     * Note that no results will be returned if the search string is null or empty.
     * @param searchString
     */
    public FilterConfig(String searchString) {
        this.searchString = searchString;
        this.wordCategories = EnumSet.allOf(WordCategory.class);
        this.wordClasses = EnumSet.allOf(WordClass.class);
        this.orthFormVariants = EnumSet.allOf(OrthFormVariant.class);
        this.regEx = false;
        this.ignoreCase = false;
        this.editDistance = 0;
    }

    /**
     * Returns the search string for this configuration
     * @return the search string
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * Set the search string for this configuration
     * @param searchString
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * Get the Word Categories for this configuration
     * @return the WordCategories for this configuration
     */
    public Set<WordCategory> getWordCategories() {
        return wordCategories;
    }

    /**
     * Set the Word Categories to be included in searches
     * @param categories one or more Word Categories to be included in searches
     */
    public void setWordCategories(WordCategory... categories) {
        wordCategories.clear();
        wordCategories.addAll(Arrays.asList(categories));
    }

    /**
     * Add one or more Word Categories to this configuration
     * @param categories one or more Word Categories to add
     */
    public void addWordCategories(WordCategory... categories) {
        wordCategories.addAll(Arrays.asList(categories));
    }

    /**
     * Add all Word Categories, which is the default configuration
     */
    public void addAllWordCategories() {
        wordCategories = EnumSet.allOf(WordCategory.class);
    }

    /**
     * Remove one or more Word Categories from this search configuration
     * @param categories Word Categories to remove
     */
    public void removeWordCategories(WordCategory... categories) {
        for (WordCategory cat : categories) {
            wordCategories.remove(cat);
        }
    }

    /**
     * Clear all Word Categories. Note that this method is included for convenience,
     * but no results will be returned unless at least one Word Category is specified.
     */
    public void clearWordCategories() {
        wordCategories.clear();
    }

    /**
     * Get the Word Classes for this configuration
     * @return the Word Classes for this configuration
     */
    public Set<WordClass> getWordClasses() {
        return wordClasses;
    }

    /**
     * Set the Word Classes for this configuration
     * @param classes one or more Word Classes to be included in searches
     */
    public void setWordClasses(WordClass... classes) {
        wordClasses.clear();
        wordClasses.addAll(Arrays.asList(classes));
    }

    /**
     * Add one or more Word Classes to this configuration
     * @param classes one or more Word Classes to add
     */
    public void addWordClasses(WordClass... classes) {
        wordClasses.addAll(Arrays.asList(classes));
    }

    /**
     * Add all Word Classes, which is the default configuration
     */
    public void addAllWordClasses() {
        wordClasses = EnumSet.allOf(WordClass.class);
    }

    /**
     * Remove one or more Word Classes from this configuration
     * @param classes one or more Word Classes to remove
     */
    public void removeWordClasses(WordClass... classes) {
        for (WordClass wClass : classes) {
            wordClasses.remove(wClass);
        }
    }

    /**
     * Clear all Word Classes. Note that this method is included for convenience,
     * but no results will be returned unless at least one Word Class is specified.
     */
    public void clearWordClasses() {
        wordClasses.clear();
    }

    /**
     * Get OrthFormVariants for this configuration
     * @return the OrthFormVariants for this configuration
     */
    public Set<OrthFormVariant> getOrthFormVariants() {
        return orthFormVariants;
    }

    /**
     * Set the OrthFormVariants for this configuration
     * @param variants one or more OrthFormVariants to be included in searches
     */
    public void setOrthFormVariants(OrthFormVariant... variants) {
        orthFormVariants.clear();
        addOrthFormVariants(variants);
    }

    /**
     * Add one or more OrthFormVariants to this configuration
     * @param variants one or more OrthFormVariants to add
     */
    public void addOrthFormVariants(OrthFormVariant... variants) {
        for (OrthFormVariant variant : variants) {
            orthFormVariants.add(variant);
        }
    }

    /**
     * Remove one or more OrthFormVariants from this configuration
     * @param variants one or more OrthFormVariants to remove
     */
    public void removeOrthFormVariants(OrthFormVariant... variants) {
        for (OrthFormVariant variant : variants) {
            orthFormVariants.remove(variants);
        }
    }

    /**
     * Clear all OrthFormVariants. Note that this method is included for convenience,
     * but no results will be returned unless at least one OrthFormVariant is specified.
     */
    public void clearOrthFormVariants() {
        orthFormVariants.clear();
    }

    /**
     * Return true if the searchString is interpreted as a regular expression, otherwise false.
     * @return true if the searchString is interpreted as a regular expression, otherwise false
     */
    public boolean isRegEx() {
        return regEx;
    }

    /**
     * Set the regular expression flag to true (regular expression) or false (literal).
     * Note that if the searchString is a regular expression, editDistance will be ignored.
     * @param regEx true if searchString is a regular expression, false if it is a literal
     */
    public void setRegEx(boolean regEx) {
        this.regEx = regEx;
    }

    /**
     * Return true if case is ignored, false for case sensitive searching
     * @return true if case is ignored, false for case sensitive searching
     */
    public boolean isIgnoreCase() { return ignoreCase; }

    /**
     * Set the ignoreCase flag (true to ignore case, false for case sensitive)
     * @param ignoreCase true to ignore case, false for case sensitive
     */
    public void setIgnoreCase(boolean ignoreCase) { this.ignoreCase = ignoreCase; }

    /**
     * Get the edit distance for this configuration.
     * @return the edit distance for this configuration
     */
    public int getEditDistance() {
        return editDistance;
    }

    /**
     * Set the edit distance for this configuration.
     * Note that edit distance will be ignored if the searchString is a regular expression.
     * @param editDistance the edit distance for this configuration
     */
    public void setEditDistance(int editDistance) {
        this.editDistance = editDistance;
    }

    @Override
    public String toString() {
        return "FilterConfig{" +
                "searchString='" + searchString + '\'' +
                ", regEx=" + regEx +
                ", ignoreCase=" + ignoreCase +
                ", wordCategories=" + ((wordCategories.size() == WordCategory.values().length) ? "ALL" : wordCategories) +
                ", orthFormVariants=" + ((orthFormVariants.size() == OrthFormVariant.values().length) ? "ALL" : orthFormVariants) +
                ", editDistance=" + editDistance +
                ", wordClasses=" + ((wordClasses.size() == WordClass.values().length) ? "ALL" : wordClasses) +
                '}';
    }
}
