package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

public class LookupFact implements SouffleFact {
    private final String type;
    private final String signature;
    private final String method;

    public LookupFact(String type, String signature, String method) {
        this.type = type;
        this.signature = signature;
        this.method = method;
    }

    @Override
    public String getRelationName() {
        return "Lookup";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(type, signature, method);
    }
}
