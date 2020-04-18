package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

import static wtf.thepalbi.relations.FactWriter.writeMethod;

/**
 * Represents the local variable in a method that refers to object the methods belongs to.
 */
public class ThisVarFact implements SouffleFact {
    private final String localName;
    private final SootMethod method;

    public ThisVarFact(String localName, SootMethod method) {
        this.localName = localName;
        this.method = method;
    }

    @Override
    public String getRelationName() {
        return "ThisVar";
    }

    @Override
    public String toIODirective() {
        return FactWriter.twoParameters(writeMethod(method), localName);
    }
}
