Java GermaNet API
============

[GermaNet](https://uni-tuebingen.de/de/142806) is a lexical semantic network that partitions the lexical
space in a set of concepts that are interlinked with semantic relations.
A semantic concept is modeled by a synset (short for *synonymy set*) in
GermaNet. A synset is a set of words (called *lexical units*) where all
the words are taken to have (almost) the same meaning.

This repository contains the Java API to access the GermaNet data. The main class named `GermaNet` serves as a starting point to the API.
When a `GermaNet` object is constructed, data is loaded from the
GermaNet XML sources.

Setup
--------------------

The API is located in the package `de.tuebingen.uni.sfs.germanet.api`.

To use the API you can either download the [jar](https://search.maven.org/artifact/de.tuebingen.uni.sfs.germanet/germanet-api) or, if you are using Maven, add the following dependency to your pom file:
```
<dependency>
    <groupId>de.tuebingen.uni.sfs.germanet</groupId>
    <artifactId>germanet-api</artifactId>
    <version>13.3.0</version>
</dependency>
```
The latest version of the API works for GermaNet realeases starting from 13.0. 

Usage
------------
For a short introduction into the latest version API and some code examples of how to use it, you can have a look at the [java notebook](https://github.com/Germanet-sfs/germanetTutorials/blob/master/javaAPI/tutorial_R15.ipynb) in this [repository](https://github.com/Germanet-sfs/germanetTutorials). You can either look at the code or download it as an interactive jupyter notebook if you follow the instructions in the readme.

For a more extensive introduction into the linguistic concepts related to GermaNet and more sophisticated examples of how to use the API, have a look at this [PDF](https://github.com/Germanet-sfs/GermaNetApi/blob/master/documentation/tutorial/GermaNetTutorial13.0.pdf) and the following [code](https://github.com/Germanet-sfs/GermaNetApi/blob/master/src/test/java/HypernymGraph.java). This tutorial is based on an older version of the API but can also be used with the current version.

Contact
---------
If problems or further questions arise, simply contact us by e-mail: germanet-feedback@sfs.uni-tuebingen.de
