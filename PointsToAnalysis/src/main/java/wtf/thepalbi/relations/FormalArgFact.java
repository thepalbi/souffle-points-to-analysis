package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

import static wtf.thepalbi.relations.FactWriter.writeMethod;

public class FormalArgFact implements SouffleFact {
    private final SootMethod method;
    private final int parameterNumber;
    private final String localName;

    public FormalArgFact(SootMethod method, int parameterNumber, String localName) {
        this.method = method;
        this.parameterNumber = parameterNumber;
        this.localName = localName;
    }

    @Override
    public String getRelationName() {
        return "FormalArg";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(writeMethod(method), String.valueOf(parameterNumber), localName);
    }
}
