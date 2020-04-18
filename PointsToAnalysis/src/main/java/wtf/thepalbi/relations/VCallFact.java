package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

public class VCallFact implements SouffleFact {
    public VCallFact(String baseLocalName, String calledMethodSignature, String invocationSite, SootMethod method) {
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
