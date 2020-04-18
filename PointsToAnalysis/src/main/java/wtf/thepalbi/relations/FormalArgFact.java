package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

public class FormalArgFact implements SouffleFact {
    public FormalArgFact(SootMethod method, int parameterNumber, String localName) {
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
