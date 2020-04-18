package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

import static wtf.thepalbi.relations.FactWriter.writeMethod;

public class VCallFact implements SouffleFact {
    private final String baseLocalName;
    private final String calledMethodSignature;
    private final String invocationSite;
    private final SootMethod method;

    public VCallFact(String baseLocalName, String calledMethodSignature, String invocationSite, SootMethod method) {
        this.baseLocalName = baseLocalName;
        this.calledMethodSignature = calledMethodSignature;
        this.invocationSite = invocationSite;
        this.method = method;
    }

    @Override
    public String getRelationName() {
        return "VCall";
    }

    @Override
    public String toIODirective() {
        return FactWriter.fourParameters(baseLocalName, calledMethodSignature, invocationSite, writeMethod(method));
    }
}
