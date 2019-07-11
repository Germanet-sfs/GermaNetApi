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
 * Configuration for complex filtering of <code>Synset</code> or <code>LexUnit</code> searches.
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
     *
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

    public Set<WordCategory> getWordCategories() {
        return wordCategories;
    }

    public void setWordCategories(WordCategory... categories) {
        wordCategories.clear();
        wordCategories.addAll(Arrays.asList(categories));
    }

    public void addWordCategories(WordCategory... categories) {
        wordCategories.addAll(Arrays.asList(categories));
    }

    public void addAllWordCategories() {
        wordCategories = EnumSet.allOf(WordCategory.class);
    }

    public void removeWordCategories(WordCategory... categories) {
        for (WordCategory cat : categories) {
            wordCategories.remove(cat);
        }
    }

    public void clearWordCategories() {
        wordCategories.clear();
    }

    public Set<WordClass> getWordClasses() {
        return wordClasses;
    }

    public void setWordClasses(WordClass... classes) {
        wordClasses.clear();
        wordClasses.addAll(Arrays.asList(classes));
    }

    public void addWordClasses(WordClass... classes) {
        wordClasses.addAll(Arrays.asList(classes));
    }

    public void addAllWordClasses() {
        wordClasses = EnumSet.allOf(WordClass.class);
    }

    public void removeWordClasses(WordClass... classes) {
        for (WordClass wClass : classes) {
            wordClasses.remove(wClass);
        }
    }

    public void clearWordClasses() {
        wordClasses.clear();
    }

    public Set<OrthFormVariant> getOrthFormVariants() {
        return orthFormVariants;
    }

    public void setOrthFormVariants(OrthFormVariant... variants) {
        orthFormVariants.clear();
        addOrthFormVariants(variants);
    }

    public void addOrthFormVariants(OrthFormVariant... variants) {
        for (OrthFormVariant variant : variants) {
            orthFormVariants.add(variant);
        }
    }

    public void removeOrthFormVariants(OrthFormVariant... variants) {
        for (OrthFormVariant variant : variants) {
            orthFormVariants.remove(variants);
        }
    }

    public void clearOrthFormVariants() {
        orthFormVariants.clear();
    }

    public boolean isRegEx() {
        return regEx;
    }

    public void setRegEx(boolean regEx) {
        this.regEx = regEx;
    }

    public boolean isIgnoreCase() { return ignoreCase; }

    public void setIgnoreCase(boolean ignoreCase) { this.ignoreCase = ignoreCase; }

    public int getEditDistance() {
        return editDistance;
    }

    public void setEditDistance(int editDistance) {
        this.editDistance = editDistance;
    }

    @Override
    public String toString() {
        return "FilterConfig{" +
                "searchString='" + searchString + '\'' +
                ", regEx=" + regEx +
                ", ignoreCase=" + ignoreCase +
                ", wordCategories=" + wordCategories +
                ", orthFormVariants=" + orthFormVariants +
                ", editDistance=" + editDistance +
                ", wordClasses=" + wordClasses +
                '}';
    }
}
