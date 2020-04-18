package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

public class FormalReturnFact implements SouffleFact {
    public FormalReturnFact(SootMethod method, String localName) {
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
