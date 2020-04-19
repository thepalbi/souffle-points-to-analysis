package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

import java.util.Objects;

import static wtf.thepalbi.relations.FactWriter.writeMethod;

public class ReachableFact implements SouffleFact {
    private SootMethod method;

    public ReachableFact(SootMethod method) {
        this.method = method;
    }

    @Override
    public String getRelationName() {
        return "Reachable";
    }

    @Override
    public String toIODirective() {
        return FactWriter.oneParameter(writeMethod(method));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReachableFact that = (ReachableFact) o;
        return Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }
}
