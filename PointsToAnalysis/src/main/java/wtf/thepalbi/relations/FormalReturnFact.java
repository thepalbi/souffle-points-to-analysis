package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

import static wtf.thepalbi.relations.FactWriter.writeMethod;

public class FormalReturnFact implements SouffleFact {
    private final SootMethod method;
    private final String localName;

    public FormalReturnFact(SootMethod method, String localName) {
        this.method = method;
        this.localName = localName;
    }

    @Override
    public String getRelationName() {
        return "FormalReturn";
    }

    @Override
    public String toIODirective() {
        return FactWriter.twoParameters(writeMethod(method), localName);
    }
}
