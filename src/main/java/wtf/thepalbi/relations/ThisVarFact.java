package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThisVarFact that = (ThisVarFact) o;
        return Objects.equals(localName, that.localName) &&
                Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localName, method);
    }
}
