## Souffle + Soot based points-to analysis

Implementation of a points to analysis based on the [Pointer Analysis Tutorial](https://yanniss.github.io/points-to-tutorial15.pdf)
by Yannis Smaragdakis and George Balatsouras. This implementations leverages [Soot's](https://github.com/Sable/soot
) Jimple intermediate representation to convert the JVM bytecode into points-to facts, and [Souffle](Souffle-lang.github.io), a datalog flavour developed to rapidly prototype static analysis.
 
## Analysis Features
- Handles instance and static method calls
- Handles calls to methods [without a body available for analysis](#Missing-method-body-calls) 
- On the fly Call-Graph generation
- Context-Insensitive
 
## Project Structure
The analysis is composed of three main parts: the translator, the points-to ruleset, and the analysis orchestrator
. Each of them are described in depth below.

### Translator
Since the core-analysis is based in an [Andersen Points-To analysis](http://www.cs.cornell.edu/courses/cs711/2005fa/papers/andersen-thesis94.pdf),
this is a flow-insensitive analysis. This means the order of the statements does not change the output. In our case,
this means we don't need to keep track of which fact was generated first.

The translation is performed walking the statements in the control-flow graph generated for a method's body. Each
statements is mapped to Souffle facts, which are described by their following [Java counterparts](https://github.com/thepalbi/souffle-points-to-analysis/tree/master/src/main/java/wtf/thepalbi/relations).

The translator is implemented [here](https://github.com/thepalbi/souffle-points-to-analysis/blob/master/src/main/java/wtf/thepalbi/StmtToSouffleFactTranslator.java).

### Ruleset
This is the central part of the points-to. The core-analysis is implemented on top of Souffle
framework, hence the ruleset is written as datalog's logic rules. They are described in [here](https://github.com/thepalbi/souffle-points-to-analysis/blob/master/src/main/resources/vanilla-andersen.dl).

The implementations is based in the first iteration of the tutorial mentioned above, but was tuned to handle some
 additional Java and Soot specifics, like static method calls and body-less methods.

### Orchestrator
The analysis pipeline can be decomposed in several stages:
1. Extract the points-to facts from the Jimple IR (which is our version of the Java code).
2. Generate Souffle fact files to be consumed by the core-analysis.
3. Run the core-analysis.
4. Parse the output generated and convert that into a [results object](https://github.com/thepalbi/souffle-points-to-analysis/blob/master/src/main/java/wtf/thepalbi/PointsToResult.java),
to be queried by the user.

This is implemented [here](https://github.com/thepalbi/souffle-points-to-analysis/blob/master/src/main/java/wtf/thepalbi/PointToAnalysis.java.)

## Missing method body calls
By default, Soot doesn't generate a Jimple body for certain classes (for example JVM base classes, eg. `java.lang.String`). To cope with this, the analysis uses the signature of the called method, and *fakes* a method body which
 does the following:
 
 1. Allocates a new object of the type returned by the method
 2. Returns it
 
 By doing this, the analysis considers this call as an actual method invocation, returning the newly (but fake) allocated heap object.
 
 Soot can be configured to generate Jimple bodies for all JVM libraries (read all contained in `rt.jar`), but
this creates some challenges when implementing both the `Jimple -> Souffle Fact` translation, and the underlying
points-to rules. Some of them are:
- Multiple language features have to be handled to have a loss-less translation between `Jimple` and the
points-to input facts. This kind of challenges are explored in depth [here](https://github.com/thepalbi/soot-dataflow-analysis/tree/points-to-integration/sensible-data-leak-detector#inter-procedural-implementation-details).
- This additional features are reflected in the implementation of the analysis itself. In the Smaragdakis-Balatsouras
 tutorial mentioned above, a simplification of a program model is used, which supports a limited number of features
 . To be able to handle real implementations (like Java Runtime libraries), a number of other things which have
  impact in the points-to graph generated have be supported (arrays, a complex inheritance model, etc).
