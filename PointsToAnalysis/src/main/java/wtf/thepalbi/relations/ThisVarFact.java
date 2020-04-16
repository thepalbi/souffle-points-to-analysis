package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

/**
 * Represents the local variable in a method that refers to object the methods belongs to.
 */
public class ThisVarFact implements SouffleFact {
    public ThisVarFact(String localName, SootMethod owner) {
    }

    @Override
    public String getRelationName() {
        return null;
    }

    @Override
    public String toIODirective() {
        return null;
    }
}
