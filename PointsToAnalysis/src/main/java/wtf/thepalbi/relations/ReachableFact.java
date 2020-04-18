package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

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
}
