Introduction
============

This tutorial is about the Java-API[^1] to GermaNet. After an
introduction of GermaNet and the API, there is a short overview of the
GermaNet XML files (all in subsections of this chapter). The Java-API to GermaNet is
 firstly introduced by an example tutorial. Afterwards further methods are explained to finally give a complete
overview of the API.

You can download the pdf version of this tutorial [here](https://github.com/Germanet-sfs/GermaNetApi/raw/master/documentation/tutorial/GermaNetTutorial9.0.pdf).

The code snippet is [here](https://github.com/Germanet-sfs/GermaNetApi/blob/master/src/test/java/HypernymGraph.java).

Basics About GermaNet
---------------------

GermaNet[^2] is a lexical semantic network that partitions the lexical
space in a set of concepts that are interlinked with semantic relations.
A semantic concept is modeled by a synset (short for *synonymy set*) in
GermaNet. A synset is a set of words (called *lexical units*) where all
the words are taken to have (almost) the same meaning. Thus a synset is
a set-representation of the semantic relation of synonymy.

There are two types of semantic relations in GermaNet: conceptual
relations and lexical relations. Conceptual relations hold between two
semantic concepts or synsets. They include relations such as hypernymy,
part-whole relations, entailment, or causation. Lexical relations hold
between two individual lexical units. Antonymy, a pair of opposites, is
an example of a lexical relation.

Java-API to GermaNet
--------------------

The Java-API to GermaNet represents a programming interface, which means
that it provides several methods for accessing GermaNet data. The API is
located in package `de.tuebingen.uni.sfs.germanet.api`.

The main class named `GermaNet` serves as a starting point to the API.
When a `GermaNet` object is constructed, data is loaded from the
GermaNet XML sources. All synsets (class `Synset`) and lexical units
(class `LexUnit`) can be obtained through this object, which in turn can
be used to examine attributes or find semantic relations, among other
things.

This API specifies high-level look-up access to GermaNet data. As it is
intended to be a read-only resource, no methods to extend or modify data
are provided. All classes and methods are described in the enclosed Java
API documentation.

Within the Java-API, there is a GermaNet class that is a collection of
German lexical units (`LexUnit`) organized into synsets (`Synset`). A
`GermaNet` object provides methods for retrieving lists of `Synsets` or
`LexUnits`, which can be filtered by word category, orthographic form,
or some combination.

A `Synset` has a `WordCategory` (adj, nomen, verben), a `WordClass`
(Substanz, Relation, Kontakt, etc.), and consists of one or more
`LexUnits` and a paraphrase (represented as `Strings`). The list of
`LexUnits` for a `Synset` is never empty. A `Synset` object provides
methods for retrieving the word category, the paraphrase, and all
lexical units as well as methods for retrieving lists of conceptually
related synsets.

A LexUnit consists of an orthographical form (`orthForms`, represented
as a `String`) and has optionally an orthographical variant (`orthVar`),
an old orthographical form (`oldOrthForm`) and an old orthographical
variant (`oldOrthVar`). Furthermore, a `LexUnit` object can have
`Examples` and `Frames`, and it has the following attributes:
`sense (int)`, `source (String)`, `styleMarking (boolean)`,
`artificial (boolean)`, and `namedEntity (boolean)`. A `LexUnit` object
provides methods for retrieving any of its properties, as well as
methods for retrieving lists of other `LexUnits` lexically related to
it.

A `Frame` is simply a container for frame data (`String`).

An `Example` consists of text (`String`) and zero or more `Frames`.

A `ConRel` is a set of possible conceptual relations between `Synsets`
(represented as an enum type). A `ConRel` object provides methods for
checking if a particular String is a valid conceptual relation, and for
determining if a relation is transitive or not. The set consists of the
following transitive and non-transitive relations:

1.  transitive relations: hypernymy, hyponymy, meronymy, holonymy;

2.  non-transitive relations: entailment, entailed, causation, caused,
    association.

A `LexRel` is a set of possible lexical relations between `LexUnits`
(represented as an enum type). A `LexRel` object provides a method for
checking if a particular String is a valid lexical relation. Since there
is only one transitive lexical relation (synonymy), and no special
processing is required by the API to retrieve synonyms, there is no
distinction made between transitive and non-transitive lexical
relations. The set consists of the following relations:

1.  synonymy;

2.  antonymy;

3.  pertainymy.

A `WordCategory` is a set of possible word categories (represented as an
enum type) and contains the values: adj, nomen, verben.

A `WordClass` is a set of possible word classes (represented as an enum
type) and contains 38 values: Substanz, Relation, Kontakt, etc.

GermaNet XML Files
------------------

The XML files represent the GermaNet data. There are two types of XML
files. One type represents all synsets with their lexical units and all
other properties. The other type represents all relations, both
conceptual and lexical relations.

### Synset Files

The XML files contatin all synsets in separated files. These files are
named `wordcategory.wordclass.xml`, e.g. `adj.Allgemein.xml`. Each
synset starts with a synset tag and contatins at least one lexical unit
(encoded with the tag `lexUnit`) with its properties and frames and
examples.

    <synsets>
      <synset id="s[0-9]" wordCategory="{adj|nomen|verben}">
        <lexUnit id="l[0-9]" sense="[0-9]" source="STRING"
         namedEntity="{yes|no}" artificial="{yes|no}" styleMarking="{yes|no}">
          <orthForm>STRING</orthForm>
          <orthVar>STRING</orthVar>
          <oldOrthForm>STRING</oldOrthForm>
          <oldOrthVar>STRING</oldOrthVar>
          <example><text>STRING</text><exframe>STRING</exframe></example>
          <frame>STRING</frame>
        </lexUnit>
        <paraphrase>STRING</paraphrase>
      </synset>
      ...
    </synsets>

### Relation Files

The relations are stored within a separate XML file. Both kinds of
relations are encoded: conceptual (tag `con_rel`) and lexical (tag
`lex_rel`) relations.

    <relations>
      <con_rel dir="{one|both|revert}" from="s[0-9]" to="s[0-9]"
           name="{has_hypernym|has_component_meronym|causes|is_related_to...}"
           inv="{has_hyponym|is_entailed_by|has_member_meronym...}"/>
      <lex_rel dir="{one|both|revert}" from="l[0-9]" to="l[0-9]"
           name="{has_antonym|has_pertainym|has_participle}"/>
      ...
    </relations>

Tutorial
========

In this tutorial, we will develop a Java program that makes use of the
most important part of the GermaNet-API. Once it is finished, your
program will even be useful – it generates a description of a graph that
shows a concept and all its hypernyms and hyponyms up to a certain
distance from the concept, which is specified by the user. The file
[*HypernymGraph.java*](https://github.com/Germanet-sfs/GermaNetApi/blob/master/src/test/java/HypernymGraph.java) contains the source code for this tutorial, and is
included in the GermaNet distribution.

The final output of the tutorial program will look somewhat like the
graph in [autograph].

![Output of this tutorial](https://raw.githubusercontent.com/Germanet-sfs/GermaNetApi/master/documentation/tutorial/auto_graph.png)

Before You Start 
----------------

If you haven’t done so already, you will need to obtain:

1.  The GermaNet data (either archived or unpacked to a directory
    typically named *GN\_Vxx/GN\_Vxx\_XML*).

2.  The GermaNet Java library, called *GermaNetApi9.0.jar*.

3.  In order to turn the graph description into an actual image, you
    will need the GraphViz Tools[^3]. Now would be a good time to
    download and install them.

All of the classes described previously are defined in the package\
`de.tuebingen.uni.sfs.germanet.api` within the *GermaNetApi9.0.jar*
file. You do not need to unpack the jar file.

### Classpath

If you are working from the command line, you will need to add\
*GermaNetApi9.0.jar* to your CLASSPATH environment variable[^4].

If you are working within an IDE (such as NetBeans or Eclipse), add
*GermaNetApi9.0.jar* to the classpath for any project which uses
GermaNet.

### Important Note on Memory

Loading GermaNet requires more memory than the JVM allocates by default.
Any application that loads GermaNet will most likely need to be run with
JVM options that increase the memory allocated, like this:

`java -Xms256m -Xmx256m MyApplication`

These options can be added to your IDE’s VM options so that they will be
used automatically when your application is run from within the IDE.

Depending on the memory needs of the application itself, the 128’s may
need to be changed to a higher number. Be careful not to allocate too
much memory for the JVM, though, as this may cause other running
programs (like your windowing environment) to crash.

Step 1: Importing Libraries
---------------------------

Before we can create a `GermaNet` object, which loads the XML data and
provides methods for looking up synsets and lexical units, we need to
import the germanet library and several other necessary libraries.

The box below shows the first lines of the program. If you plan to type
the program yourself along with the tutorial, create a file called
*HypernymGraph.java*.

    import de.tuebingen.uni.sfs.germanet.api.*;
    import java.io.*;
    import java.util.*;
    public class HypernymGraph {
      public static void main(String[] args) {
        // to be filled in...
      }
    }

Step 2: Getting User Input
--------------------------

The program needs some information to do its job that the user must
supply:

1.  The word (i.e. orthographic form that represents a lexical unit)
    whose hypernyms and hyponyms should be displayed (to be accurate, it
    is not a lexical unit whose relations are to be displayed, but
    rather the synset that the lexical unit is a member of). In fact, a
    lexical unit could be a member of more than one synset if it is
    ambiguous, in which case the program will print the hypernyms and
    hyponyms for all of the synsets.

2.  The maximum distance up to which hypernyms and hyponyms are to be
    displayed.

3.  The name of the file to write the output to.

<!-- -->

    import de.tuebingen.uni.sfs.germanet.api.*;
    import java.io.*;
    import java.util.*;
    public class HypernymGraph {
      public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        String destName;
        File gnetDir;
        String word;
        int depth;
        Writer dest;
        System.out.println("HypernymGraph creates a GraphViz graph " +
                "description of hypernyms and hyponyms of a GermaNet" +
                    "concept up to a given depth.");
        System.out.println("Enter <word> <depth> <outputFile> " +
                    "[eg: Automobil 2 auto.dot]: ");
        word = keyboard.next();
        depth = keyboard.nextInt();
        destName = keyboard.nextLine().trim();
        // to be continued...
      }
    }

Step 3: Creating a GermaNet Object
----------------------------------

To construct a `GermaNet` object, provide the location of the GermaNet
data. This can be done with a `String` representing the path to the
directory containing the data, or with a `File` object. Generally
speaking, file locations should never be hardcoded, but for the sake of
simplicity, this code assumes that the GermaNet data files are in a
directory called\
*/germanet/GN\_V80/GN\_V80\_XML*. Please change the line:

    gnetDir = new File("/germanet/GN_V80/GN_V80_XML");}

to reflect the actual location of the GermaNet data files on your
computer.

    import de.tuebingen.uni.sfs.germanet.api.*;
    import java.io.*;
    import java.util.*;
    public class HypernymGraph {
      public static void main(String[] args) {
        try {
          Scanner keyboard = new Scanner(System.in);
          String destName;
          String word;
          int depth;
          Writer dest;
          System.out.println("HypernymGraph creates a GraphViz graph " +
                      "description of hypernyms and hyponyms of a GermaNet" +
                      "concept up to a given depth.");
          System.out.println("Enter <word> <depth> <outputFile> " +
                      "[eg: Automobil 2 auto.dot]: ");
          word = keyboard.next();
          depth = keyboard.nextInt();
          destName = keyboard.nextLine().trim();
          gnetDir = new File("/germanet/GN_V80/GN_V80_XML");
          GermaNet gnet = new GermaNet(gnetDir);
        // to be continued...
        } catch (Exception ex) {
           ex.printStackTrace();
           System.exit(0);
        }
      }
    }

Notice that we need to enclose the call to the constructor in a
try/catch block. This is because the `GermaNet` object cannot be created
if the data files are not found or are corrupted. If something goes
wrong, an exception is thrown. We just print the stack trace and exit if
this happens.

Step 4: Finding All Synsets
---------------------------

We can now find all the synsets in GermaNet that the word `orthForm` is
a member of. Recall that words may be ambiguous, which means that a word
(or lexical unit) may occur in more than one synset.

          List<Synset> synsets;
          synsets = gnet.getSynsets(word);
          if (synsets.size() == 0) {
            System.out.println(word + " not found in GermaNet");
            System.exit(0);
          }
          // to be continued...

The method `getSynsets(orthForm)`, which is defined in the class
`GermaNet`, returns a `List` containing all of the `Synsets` that the
word occurs in. If the size of this list is zero, then no synsets were
found with a lexical unit containing the orthographic form `orthForm`,
and we exit the program.

Each element of the `List synsets` is a `Synset` object. A `Synset`
object has methods to retrieve all the lexical units that are members of
the synset, and to find out about what other synsets are related to it
with respect to a specific kind of conceptual relation. We will use some
of the methods that are implemented in the `Synset` class in the next
step.

Step 5: Generating the Hypernym Graph
-------------------------------------

We are now ready to generate the output, which is first stored in a
`String` called `dotCode`, then written to the output file. As mentioned
before, our program does not directly create images, but rather textual
descriptions of graphs in the GraphViz graph definition language. These
can later be turned into images using the GraphViz tools.

          String dotCode = "";
          dotCode += "graph G {\n";
          dotCode += "overlap=false\n";
          dotCode += "splines=true\n";
          dotCode += "orientation=landscape\n";
          dotCode += "size=\"13,15\"\n";
          HashSet<Synset> visited = new HashSet<Synset>();
          for (Synset syn : synsets) {
            dotCode += printHypernyms(syn, depth, visited);
          }
          dotCode += "}";
          dest = new BufferedWriter(new OutputStreamWriter(
                          new FileOutputStream(new File(destName)), "UTF-8"));
          dest.write(dotCode);
          dest.close();

The first line of the `dotCode String` opens a GraphViz graph-statement.
The following four lines then define the basic layout of the graph.
Please refer to the GraphViz manual if you want to find out what exactly
these statements do.

The algorithm that traverses the network to find the hypernyms and
hyponyms is not very complicated. It works as follows:

1.  Start with a `Synset` that the lexical unit the user requested is a
    member of (called `Synset syn`). This becomes the center node of the
    graph.

2.  Look up all hypernyms of `syn` and add them to the graph as neighbor
    nodes of `syn`.

3.  Look up all hyponyms of `syn` and add them to the graph also.

4.  For each hypernym and hyponym found, recursively find and add their
    hypernyms and hyponyms to the graph, up to the maximum distance to
    the center node, as specified by the user.

To sum up, the algorithm finds all hypernyms and hyponyms of a given
`Synset syn`, adds them to the graph, and then in turn does exactly the
same it did with syn with all of its hypernyms and hyponyms.

There is one catch, however, that we must pay attention to: Assume the
algorithm looks at some `Synset s`. It finds all hypernyms of `s` and
adds them to the graph. Then it recursively repeats all its steps for
each hypernym `h` it found: That is, it first finds all hypernyms of
`s`, then it finds all hyponyms of `h`. At this point, we must be
careful, since the `Synset s` the algorithm looked at in the previous
recursive step is, of course, a hyponym of `h`! We must make sure that
the algorithm does not consider `Synsets` it already looked at over and
over again. In our program, we use the `HashSet visited` for this: For
each `Synset` the algorithm finds, we add the `Synset` to the visited
set. Any `Synset` that is in the visited set is not considered any
further by the algorithm in subsequent recursive steps.

The program proceeds by calling the static `printHypernyms()` method for
each `Synset` in the `synsets` list. In the next step, we will turn to
`printHypernyms()`, which is the implementation of the recursive
algorithm sketched above.

We then finish up by adding a closing brace to the GraphViz description,
write the code to the output file, and close the file.

Step 6: Recursively Printing Hypernyms and Hyponyms
---------------------------------------------------

The `printHypernyms()` method, which recursively adds all hypernyms and
hyponyms of a synset to the hypernym graph, expects three arguments:

1.  The synset whose hypernyms and hyponyms are to be added next (the
    argument `synset`)

2.  The remaining distance from the center node of the graph to the last
    hypernym or hyponym to be added (argument `depth`)

3.  The set of synsets already visited (argument `visited`)

<!-- -->

    static String printHypernyms(Synset synset, int depth,
                                    HashSet<Synset> visited) {

Now declare the variables we will need later:

        String rval = "";
        List<LexUnit> lexUnits;
        String orthForm = "";
        List<Synset> hypernyms = new ArrayList<Synset>();
        List<Synset> relations;
        String hypOrthForm;
        visited.add(synset);
        // to be continued...
      }

The synset is added to the `visited` set (to make sure the algorithm
does not run in an infinite loop; see step 4).

We have already seen that the GermaNet-API contains a special class,
`Synset`, that represents the properties of a synset. There is also a
class `LexUnit` that represents the properties of a lexical unit. Both
classes provide methods to obtain information about other objects in
GermaNet the synset or lexical unit is related to. A lexical unit may
contain multiple orthographic forms (i.e. `orthForm` (main orthographic
form), `orthVar` (a variant of the main form), `oldOrthForm` (main
orthographic form in the old German orthography), and `oldOrthVar` (a
variant of the old form)), which represent different spellings of the
same word. If there are several spellings of a word, for example
*Schloß* and *Schloss* in the old and new German spelling, *Schloss*
represents `orthForm` and *Schloß* represents `oldOrthForm`.

We will use the main orthographical form of the `LexUnit` that is first
returned by `synset.getLexUnits()` as a representative for the concept
the `Synset` represents. So we must first retrieve all lexical units
that are a member of the synset:

        lexUnits = synset.getLexUnits();

As you can see, this works very much the same as retrieving all lexical
units in `GermaNet.getLexUnits()`: this method of the `Synset` class
also returns a `List` of `LexUnit` objects.

We now fetch the first orthographic form of the first `LexUnit` and add
it to the graph description, along with some formatting information:

          orthForm = lexUnits.get(0).getOrthForm();
          rval += "\"" + orthForm + "\" [fontname=Helvetica,fontsize=10]\n";

Again, you can see that the way orthographic forms are retrieved is
extremely similar to the way synsets and lexical units are accessed. Of
course, since orthographic forms are plain strings, the `List` returned
is of type `String`.

It is now time to collect all hypernyms and hyponyms and add them to the
graph. Since we will make no difference in the graphical output between
hypernyms and hyponyms we will store them (a little sloppily) in one
list called hypernyms.

         relations = synset.getRelatedSynsets(ConRel.has_hypernym);
         hypernyms.addAll(relations);
         relations = synset.getRelatedSynsets(ConRel.has_hyponym);
         hypernyms.addAll(relations);

`ConRel` is an `enum` class defined in GermaNet. Enums are special
constructs in Java for storing constants. The `ConRel` class provides a
way of telling the `getRelatedSynsets(conRel)` method which relation is
being requested so that an invalid relation cannot be requested.

`ConRel.has_hypernym` and `ConRel.has_hyponym` are conceptual relations
that apply between synsets. The complete list of conceptual realations
are: hypernymy, hyponymy, meronymy, holonymy, entailment, entailed,
causation, caused, and association.

Similarly, the `LexUnit` class contains a `getRelatedLexUnits(lexRel)`
method which accepts a `LexRel` object as a parameter.

    01     for (Synset syn : hypernyms) { 
    02       if (!visited.contains(syn)) { 
    03         hypOrthForm = syn.getLexUnits().get(0).getOrthForm(); 
    04         rval += "\"" + orthForm + "\" -- \"" + hypOrthForm + "\";\n"; 
    05 
    06         if (depth > 1) { 
    07           rval += printHypernyms(syn, depth - 1, visited); 
    08         } else { 
    09           rval += "\"" + hypOrthForm + 
    10                   "\"[fontname=Helvetica,fontsize=8]\n"; 
    11         } 
    12       } 
    13     } 
    14     // return the graph string generated 
    15     return rval;

For each hypernym and hyponym we found, we first check if we have
visited it before (line 2). If so, we skip it. Otherwise, we fetch the
first orthographic form of the first lexical unit (line 3) and use it in
line 4 to add an edge to the graph description between the node that
represents the current synset and the node that represents the hypernym
or hyponym (edges in GraphViz syntax are expressed by two node labels
that are separated by –).

If the maximum distance to the center node has not yet been reached
(line 6), we add the hypernyms and hyponyms of the current hypernym or
hyponym by recursively calling `printHypernyms()` with a decremented
depth. Otherwise, we add some formatting information for the hypernym or
hyponym node.

Step 7: Trying It Out 
---------------------

This is it! We are now ready to test our program. Compile the source
code using Java JDK 6.0 or above:

`javac HypernymGraph.java`

Then run the program:

`java -Xms256m -Xmx256m HypernymGraph`

Let’s create a graph that shows the concept *Automobil* in the center
and the hypernyms and hyponyms up to a distance of two. When asked to
enter the data, type: `Automobil 2 auto.dot`

*HypernymGraph* creates a GraphViz graph description of hypernyms and
hyponyms of a GermaNet concept up to a given depth.

`Enter <word> <depth> <outputFile> [eg: Automobil 2 auto.dot]: Automobil 2 auto.dot`

This creates the graph description file *auto.dot* in the current
working directory. The first few lines should look like this:

    graph G {
    overlap=false
    splines=true
    orientation=landscape
    size="13,15"
    "Automobil" [fontname=Helvetica,fontsize=10]
    "Automobil" -- "Muldenkipper";
    "Muldenkipper" [fontname=Helvetica,fontsize=10]
    "Muldenkipper" -- "Bauwerkzeug";
    "Bauwerkzeug" [fontname=Helvetica,fontsize=8]
    "Automobil" -- "Bagger";
    ...

We can now use one of the GraphViz tools to create a visual
representation of the graph from the graph description file in a PNG
file called *auto.png*:

`neato -Tpng auto.dot -o auto.png`

The GraphViz tools provide many more output formats and ways of
influencing the layout of the graph, which are described in the GraphViz
manuals[^5].

This finishes the tutorial. Please see the GermaNet javadoc
documentation, viewable in your web browser, for a complete list of
methods, including descriptions, available for each class within the
germanet package.

Code Snippets and Samples
=========================

This chapter contains code snippets and samples that demonstrate how to
use the GermaNet library objects and their methods.

Creating a GermaNet Object 
--------------------------

Before you can construct a `GermaNet` object, you need to make sure that
the *GermaNetApi9.0.jar* file is on your classpath, then import the
library:

    import de.tuebingen.uni.sfs.germanet.api.*;

When a `GermaNet` object is created, it needs to know where to find the
XML-formatted GermaNet data files. The location of the directory (or a
.zip/.jar archive) containing the data files is sent as a parameter to
the `GermaNet` constructor either as a `String` object:

    GermaNet gnet = new GermaNet("/germanet/GN_V80/GN_V80_XML/");
    GermaNet gnet = new GermaNet("/germanet/GN_V80/GN_V80_XML.zip");

or a `File` object:

    File gnetDir = new File("/germanet/GN_V80/GN_V80_XML");
    GermaNet gnet = new GermaNet(gnetDir);

To ignore case when getting `Synsets` and `LexUnits`, set the
`ignoreCase` flag in the constructor:

    GermaNet gnet = new GermaNet("/germanet/GN_V80/GN_V80_XML/", true);

or:

    File gnetDir = new File("/germanet/GN_V80/GN_V80_XML");
    GermaNet gnet = new GermaNet(gnetDir, true);

Unless otherwise stated in the javadoc documentation, all methods in all
objects will return an empty `List` rather than `null` to indicate that
no objects exist for a given request.

Getting Synsets from a GermaNet Object
--------------------------------------

A `Synset` has a `WordCategory` (i.e. adj, nomen, verben), a `WordClass`
(Substanz, Relation, Kontakt, etc.), a paraphrase (represented as a
`String`), and a `List` of `LexUnits`. The `List` of `LexUnits` for a
`Synset` is never empty. A `Synset` object provides methods for
retrieving any of its properties as well as methods for retrieving
`Lists` of other `Synsets` conceptually related to it. Once you have
constructed a `GermaNet` object (called `gnet` in the examples below),
you can retrieve `Lists` of `Synsets`, using orthographical form or word
category filtering, if desired. Get a `List` of all `Synsets`:

    List<Synset> allSynsets = gnet.getSynsets();

Get a `List` of all `Synsets` containing a lexical unit with `orthForm`
*Bank* (Note: if `gnet` was constructed with the `ignoreCase` flag set,
then the following method call will return the same list with parameters
such as *bank, BANK* or *BaNK*):

    List<Synset> synList = gnet.getSynsets("Bank");

Get a `List` of all `Synsets` which are adjectives (`WordCategory.adj`,
other options are `WordCategory.nomen` and `WordCategory.verben`):

    List<Synset> adjSynsets = gnet.getSynsets(WordCategory.adj);

Get a `List` of all `Synsets` with word class Menge (`WordClass.Menge`):

    List<Synset> adjSynsets = gnet.getSynsets(WordClass.Menge);

Working with Synsets
--------------------

Once you have obtained a `List` of `Synsets`, you can start processing
them. A `Synset` object has methods for retrieving its word category,
word class, lexical units (or just the orthographic forms of the lexical
units), and paraphrases, as well as methods for retrieving synsets that
are related to it.

To get a synset’s word category and do further processing in case of an
adjective:

    WordCategory wCat = aSynset.getWordCategory();
    if (wCat == WordCategory.adj) {
        // do something
    }

Or, similarly, check whether a synset belongs to a particular word
class:

    WordClass wClass = aSynset.getWordClass();
    if (wClass == WordClass.Allgemein) {
        // do something
    }

Retrieving the paraphrase is done in a similar way:

    String paraphrase = aSynset.getParaphrase();

To get a synset’s orthographic forms (retrieves a `List` of all
orthographic forms in all the `LexUnits` that belong to this `Synset`):

    List<String> orthForms = aSynset.getAllOrthForms();

To get a list of all lexical units of a synset and iterate through them:

    List<LexUnit> lexList = aSynset.getLexUnits();
    for (LexUnit lu : lexList) {
        // process lexical unit
    }

Suppose you want to find all of the member meronyms of a synset:

    List<Synset> meronyms =
        aSynset.getRelatedSynsets(ConRel.has_member_meronym);

Sometimes you may have a conceptual relationship represented as a
`String`. The following code can be used to validate the `String` and
retrieve the relations:

    String aRel = "has_hypernym";
    List<Synset> relList;
    if (ConRel.isRel(aRel)) {// make sure aRel is a valid conceptual relation
        relList = aSynset.getRelatedSynsets(ConRel.valueOf(aRel));
    }

The following are all valid calls to `getRelatedSynsets()`:

    aSynset.getRelatedSynsets(ConRel.has_hypernym);
    aSynset.getRelatedSynsets(ConRel.has_hyponym);
    aSynset.getRelatedSynsets(ConRel.has_component_meronymy);
    aSynset.getRelatedSynsets(ConRel.has_component_holonymy);
    aSynset.getRelatedSynsets(ConRel.has_member_meronymy);
    aSynset.getRelatedSynsets(ConRel.has_member_holonymy);
    aSynset.getRelatedSynsets(ConRel.has_substance_meronymy);
    aSynset.getRelatedSynsets(ConRel.has_substance_holonymy);
    aSynset.getRelatedSynsets(ConRel.has_portion_meronymy);
    aSynset.getRelatedSynsets(ConRel.has_portion_holonymy);
    aSynset.getRelatedSynsets(ConRel.is_related_to;
    aSynset.getRelatedSynsets(ConRel.causes);
    aSynset.getRelatedSynsets(ConRel.entails);
    aSynset.getRelatedSynsets(ConRel.is_entailed_by); // and so on...

Suppose you are not interested in any particular relation, but want a
`List` of all `Synsets` that are related to `aSynset` in any way:

    List<Synset> allRelations = aSynset.getRelatedSynsets();

For transitive relations (hypernymy, hyponymy, meronymy, holonymy),
there is a method that retrieves a `List` of `Lists` of `Synsets`, where
the `List` at position 0 contains the originating `Synset`, the `List`
at position 1 contains the relations at depth 1, the `List` at position
2 contains the relations at depth 2, and so on up to the maximum depth.
Using this data structure, some information cannot be included – namely,
for any synset at depth n, you cannot determine which synset at depth
n-1 it is a relation of. Nonetheless, you may find the method useful.

The following code prints the orthographic forms of each synset at every
depth of the hyponyms of *Decke*:

    List<List<Synset>> transHyponyms;
    synList = gnet.getSynsets("Decke");
    String spaces;
    for (Synset s : synList) {
        spaces = "";
        transHyponyms = s.getTransRelatatedSynsets(ConRel.has_hyponym);
        for (List<Synset> listAtDepth : transHyponyms) {
            for (Synset synAtDepth : listAtDepth) {
                System.out.println(spaces + synAtDepth.getAllOrthForms());
            }
            spaces += "     ";
        }
    }

Two `Synsets` are found containing the `orthForm` Decke. For each of
them, we retrieve the hyponyms using the `getTransRelatedSynsets()`
method, store the result in the `List` of `Lists` of `Synsets` called
`transHyponyms`, and then print `transHyponyms`. The output looks like
this:

    [Decke]
         [Bettdecke]
         [Wolldecke]
         [Kuscheldecke]
         [Altardecke]
         [Satteldecke]
         [Plane]
         [Loeschdecke]
              [Plastikplane]
    [Decke, Zimmerdecke]
         [Kuppel]
         [Beleuchtungsdecke]
         [Haengedecke]
         [Stuckdecke]
              [Zirkuskuppel]

Getting LexUnits from a GermaNet Object
---------------------------------------

A `LexUnit` may consist of multiple orthographic forms (stored as
`Strings`), which represent different spellings of the same word:

1.  The main orthographic form `orthForm` (always set).

2.  A variant of the main form `orthVar` (optional).

3.  The main orthographic form in the old German orthography
    `oldOrthForm` (optional).

4.  A variant of the old form `oldOrthVar` (optional).

If there are several spellings of a word, for example *Schloß* and
*Schloss* in the old and new German spelling, *Schloss* represents
`orthForm` and *Schloß* represents `oldOrthForm`.

Lexical units can have `Frames` and `Examples`. Further attributes of a
`LexUnit` are the following:
`styleMarking (boolean), sense (int), orthVar (boolean), artificial (boolean), namedEntity (boolean)`,
and `source (String)`. A `LexUnit` object provides methods for
retrieving any of its properties, as well as methods for retrieving
`Lists` of other `LexUnits` lexically related to it. Once you have
constructed a `GermaNet` object (called `gnet` in the examples below),
you can retrieve `Lists` of `LexUnits`, using orthographic form or word
category filtering, if desired.

Get a List of all `LexUnits`:

    List<LexUnit> allLexUnits = gnet.getLexUnits();

Get a `List` of all `LexUnits` with `orthForm` *Bank* (Note: if `gnet`
was constructed with the `ignoreCase` flag set, then the following
method call will return the same list with parameters such as *bank,
BANK* or *BaNK*):

    List<LexUnit> lexList = gnet.getLexUnits("Bank");

Get a `List` of all `LexUnits` which are nouns (`WordCategory.nomen`,
other options are `WordCategory.adj` and `WordCategory.verben`):

    List<LexUnit> nomLexUnits = gnet.getLexUnits(WordCategory.nomen);

Working with LexUnits
---------------------

Once you have obtained a `List` of `LexUnits`, you can start processing
them. A `LexUnit` object has methods for retrieving its
`WordCategory, Synset`, orthographic forms
(`orthForm, orthVar, oldOrthForm`, and `oldOrthVar`), and further
attributes including word sense number
`(sense), source, namedEntity, artificial`, and `styleMarking`, as well
as methods for retrieving `LexUnits` that are lexically related to it.

To get the word category of a lexical unit and do further processing in
case of a verb:

    WordCategory wCat = aLexUnit.getWordCategory();
    if (wCat == WordCategory.verben) {
        // do something
    }

To do the same for word class:

    WordClass wClass = aLexUnit.getWordClass();
    if (wClass == WordClass.Geist) {
        // do something
    }

To get the orthographic forms of a lexical unit:

    List<String> orthForms = aLexUnit.getOrthForms();

You may prefer to retrieve the main orthographic form:

    String orthForm = aLexUnit.getOrthForm();

You may prefer to just retrieve the variant of the main orthographic
form:

    String orthVar = aLexUnit.getOrthVar();

To get the main orthographic form in the old German orthography:

    String oldOrthForm = aLexUnit.getOldOrthForm();

Retrieving the variant of the old orthographic form:

    String oldOrthVar = aLexUnit.getOldOrthVar();

Suppose you want to generate a `List` of `LexUnits` with word category
*nomen*, but you are not interested in named entities or artificial
nouns. You could generate such a `List` with the following code (note
that we use a real `Iterator` object here instead of just a simple
for-loop because it is the only safe way to remove elements from a
`List` while iterating):

    List<LexUnit> lexList = gnet.getLexUnits(WordCategory.nomen);
    LexUnit aLexUnit;
    Iterator<LexUnit> iter = lexList.iterator();
    while (iter.hasNext()) {
        aLexUnit = iter.next();
        if (aLexUnit.isNamedEntity() || aLexUnit.isArtificial()) {
            iter.remove();
        }
    }
    // ... process lexList ...

Suppose you want to find all of the antonyms of a `LexUnit`:

    List<LexUnit> antonyms = aLexUnit.getRelatedLexUnits(LexRel.has_antonym);

Sometimes you may have a lexical relationship represented as a `String`.
The following code can be used to validate the `String` and retrieve the
relations:

    String aRel = ”has_antonym”;
    List<LexUnit> relList;
    if (LexRel.isRel(aRel)) {// make sure aRel is a valid lexical relation
        relList = aLexUnit.getRelatedLexUnits(LexRel.valueOf(aRel));
    }

The following are all valid calls to `getRelatedLexUnits()`:

    aLexUnit.getRelatedLexUnits(LexRel.has_synonym);
    aLexUnit.getRelatedLexUnits(LexRel.has_antonym);
    aLexUnit.getRelatedLexUnits(LexRel.has_pertainym); // and so on ...

Suppose you are not interested in any particular relation, but want a
`List` of all `LexUnits` that are related to `aLexUnit` in any way:

    List<LexUnit> allRelations = aLexUnit.getRelatedLexUnits();

Finding the `Examples` and `Frames` is done as follows:

    List<Example> exList = aSynset.getExamples();
    List<Frame> frameList = aSynset.getFrames();

Working with Frames and Examples
--------------------------------

A `Frame` is simply a container for frame data, which can be retrieved
with the `getData()` method. Frames occur in two contexts within
GermaNet:

1.  A `List` of `Frames` may be present within a `Synset` object. You
    could print the `orthForms` of verb `Synsets` containing a `Frame`
    that begins with *NN* like this:

        synList = gnet.getSynsets(WordClass.verben);
        List<Frame> frameList;
        boolean printIt;
        for (Synset syn : synList) {
          printIt = false;
          frameList = syn.getFrames();
          for (Frame f : frameList) {
            if (f.getData().startsWith("NN")) {
              printIt = true;
            }
          }
          if (printIt) {
            System.out.println(syn.getAllOrthForms());
          }
        }

2.  A `List` of `Frames` may be present within an `Example` (which in
    turn is part of a `Synset`). We could print the `Examples` with
    `Frames` containing the substring *AN* of verb `Synsets` with the
    following code:

        synList = gnet.getSynsets(WordClass.verben);
        List<Example> exList;
        List<Frame> frameList;
        for (Synset syn : synList) {
          exList = syn.getExamples();
          for (Example ex : exList) {
            frameList = ex.getFrames();
            for (Frame f : frameList) {
              if (f.getData().contains("AN")) {
                System.out.println(f.getData() + " : " +ex.getText());
              }
            }
          }
        }

Working with Interlingual Index (ILI) Data
------------------------------------------

GermaNet-API 9.0 allows for working with the Interlingual Index data
[^6], distributed with the GermaNet XML files. ILI links GermaNet
synsets to English ones (originally, to Princeton WordNet 2.0) via
several types of relations, such as synonymy, holonymy, meronymy etc.

ILI data is automatically loaded along with the GermaNet XML data. You
can then load `IliRecords` into a `List` by calling `getIliRecords()`
metod on either a `LexUnit` or a `Synset`.

    List<LexUnit> units = gnet.getLexUnits("abblocken");
    for (LexUnit unit : units) {
      List<IliRecord> ili = unit.getIliRecords();
    }
    Synset ss = gnet.getSynsetByID(5711);
    List<IliRecord> ili2 = ss.getIliRecords();

You can also get the complete `List` of all available `IliRecords` by
calling `getIliRecords()` on the `GermaNet` object itself:

    List<IliRecord> allIlis = gnet.getIliRecords();

`IliRecord` object has methods for retrieving such attributes as the
relation it has with a certain Princeton WordNet (PWN) synset
(`ewnRelation`), the English word representing the PWN synset
(`pwnWord`), as well as a list of all the synonyms belonging to it
(`englishSynonyms`) and the corresponding English paraphrase
(`pwn20paraphrase`), both from PWN 2.0. IDs from PWN 2.0 and PWN 3.0 are
also available for most entries (`pwn20Id` and `pwn30Id`, respectively).

To get the relation of an `IliRecord` to the corresponding English
synset:

    String ewnRelation = anIliRecord.getEwnRelation();

To retrieve the English word the index points to:

    String engWord = anIliRecord.getPwnWord();

Bear in mind that not all `IliRecords` have disambiguated links to
particular English words, thus for some of them `getPwnWord()` method
will return `null` values. All such `IliRecords` will have multiple
synonymous words on the English side, which can be retrieved as follows:

    List<String> englishSynonyms = anIliRecord.getEnglishSynonyms();

Whenever English synset has only one word in it, the returned `List`
will be empty, and the word in question will be retrievable by means of
the `getPwnWord()` method.

To get English paraphrase for this `IliRecord`, use the following
method:

    String paraphrase = anIliRecord.getPwn20paraphrase();

You may access the ID of the corresponding English synset from Princeton
WordNet 2.0, or the ID from Princeton WordNet 3.0, if it’s available (if
it is not, ID is not `null` but rather represented as zeroes):

    String pwn20Id = anIliRecord.getPwn20Id();
    String pwn30Id = anIliRecord.getPwn30Id();

Finally, if you’re interested where a certain `IliRrecord` comes from
(the original list developed in the framework of the EuroWordNet
project, or one of the extensions created at the Tübingen University),
try the following:

    String source = anIliRecord.getSource();

Working with Wiktionary Paraphrases 
-----------------------------------

Another extention[^7] to GermaNet data (distributed alongside with the
GermaNet XMLs), contains paraphrases, or definitions, for a part of the
GermaNet `LexUnits` extracted from the German version of the free online
lexicographical resource Wiktionary[^8].

You can retrieve a `List` of all the `WiktionaryParaphrases` for your
`LexUnit` object:

    LexUnit unit = gnet.getLexUnitByID(3533);
    List<WiktionaryParaphrase> wikis = unit.getWiktionaryParaphrases();

You can also get the complete `List` of all available
`WiktionaryParaphrases` by calling `getWiktionaryParaphrases()` on the
`GermaNet` object itself:

    List<WiktionaryParaphrase> wikis = gnet.getWiktionaryParaphrases();

A `WiktionaryParaphrase` have methods for accessing its ID
(`wiktionaryId`), the number of the Wiktionary sense
(`wiktionarySenseId`), the paraphrase itself (`wiktionarySense`) and a
boolean value indicating whether or not the paraphrase was taken from
Wiktionary as is, or edited (`edited`).

To get the Wiktionary ID and sense ID, use:

    int wikiId = aWikiParaphrase.getWiktionaryId();
    int senseId = aWikiParaphrase.getWiktionarySenseId();

You can retrieve the paraphrase itself:

    String wikiParaphrase =  aWikiParaphrase.getWiktionarySense();

Finally, to process the paraphrase only if it was edited, use the
following:

    if (aWikiParaphrase.hasBeenEdited()) {
        // do something 
    }

Working with Compounds
----------------------

GermaNet contains information about nominal compound splitting [^9].
Compounds are represented as modifier-head pairs, and some additional
information can accompany the constituent parts.

To access the available information on splitting of a compound, you can
call `getCompoundInfo()` method on a `LexUnit`:

    LexUnit unit = gnet.getLexUnitByID(9559);
    CompoundInfo compoundInfo =  unit.getCompoundInfo();

If the `LexUnit` in question is not a compound, or GermaNet has no
information on the splitting, the `getCompoundInfo()` method will return
`null`.

The `CompoundInfo` object gives you access to the information about
compound modifier(s) and head. A compound has either one or two
modifiers, the second option coming into play when it is unclear from
which of the two words the compound has been derived.

    String modifier1 = compoundInfo.getModifier1();
    String modifier2 = compoundInfo.getModifier2();
    String head = compoundInfo.getHead();

Whereas heads of nominal compounds are always nouns, there is a great
variation in modifier categories: adjectives
(`CompoundCategory.Adjektiv`), nouns (`CompoundCategory.Nomen`), verbs
(`CompoundCategory.Verb`), adverbs (`CompoundCategory.Adverb`), pronouns
(`CompoundCategory.Pronomen`), particles (`CompoundCategory.Partikel`),
and finally, prepositions\
(`CompoundCategory.Präposition`) can all be compound modifiers. You may
choose to work only with compounds which have adjectival modifiers:

    CompoundCategory modifier1Category = compoundInfo.getModifier1Category();
    CompoundCategory modifier2Category = compoundInfo.getModifier2Category();
    if (modifier1Category == CompoundCategory.Adjektiv ||
                  modifier2Category == CompoundCategory.Adjektiv) {
        // do something
    }

Both modifiers and heads can have certain properties associated with
them. Modifier can be an abbreviation (`CompoundProperty.Abkürzung`), an
affixoid (`CompoundProperty.Affixoid`), a konfix
(`CompoundProperty.Konfix`), a foreign word
(`CompoundProperty.Fremdwort`), an opaque morpheme\
(`CompoundProperty.opaquesMorphem`), a personal name
(`CompoundProperty. Eigenname`), or a word group
(`CompoundProperty.Wortgruppe`). When a compound has two alternative
modifiers, modifier property is `null`. Head can be an abbreviation, an
affixoid, a foreign word, a konfix, an opaque morpheme, or a result of
virtual formation (`CompoundProperty.virtuelleBildung`). To only work
with compounds which include foreign words as modifiers or heads, you
can use the following code:

    CompoundProperty modifierProperty = compoundInfo.getModifierProperty();
    CompoundProperty headProperty = compoundInfo.getHeadProperty();
    if (modifierProperty == CompoundProperty.Fremdwort ||
                  headProperty == CompoundProperty.Fremdwort) {
        // do something
    }

[^1]: Acknowledgements go to Marie Hinrichs and Holger Wunsch for their
    valuable input on both the features and usability of this API.

[^2]: See [the GermaNet
    homepage](http://www.sfs.uni-tuebingen.de/GermaNet/)

[^3]: The GraphViz Tools are freely available from
    [www.graphviz.org](www.graphviz.org)

[^4]: See <http://faq.javaranch.com/java/HowToSetTheClasspath> for help
    with setting your classpath on various operating systems.

[^5]: See [www.graphviz.org](www.graphviz.org)

[^6]: <http://www.sfs.uni-tuebingen.de/lsd/ili.shtml>

[^7]: <http://www.sfs.uni-tuebingen.de/lsd/wiktionary.shtml>

[^8]: See <http://de.wiktionary.org>

[^9]: <http://www.sfs.uni-tuebingen.de/lsd/compounds.shtml>

