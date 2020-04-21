## Souffle + Soot based points-to analysis

Implementation of a points to analysis based on the [Pointer Analysis Tutorial](https://yanniss.github.io/points-to-tutorial15.pdf)
by Yannis Smaragdakis and George Balatsouras. This implementations leverages [Soot's](https://github.com/Sable/soot
) Jimple intermediate representation to convert the JVM bytecode into points-to facts.

## Analysis Features
- Handles instance and static method calls
- Handles calls to methods [without a body available for analysis](Missing-method-body-calls) 
- On the fly Call-Graph generation
- Context-Insensitive

## Missing method body calls
By default, Soot doesn't generate a Jimple body for certain classes (for example JVM base classes, eg. `java.lang
.String`). To cope with this, the analysis uses the signature of the called method, and *fakes* a method body which
 does the following:
 
 1. Allocates a new object of the type returned by the method
 2. Returns it
 
 By doing this, the analysis considers this call as an actual method invocation, returning the newly (but fake
 ) allocated heap object.
