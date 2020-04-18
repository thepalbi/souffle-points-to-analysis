package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

public class ActualReturnFact implements SouffleFact {
    private final String invocationSite;
    private final String assignedLocal;

    public ActualReturnFact(String invocationSite, String localName) {
        this.invocationSite = invocationSite;
        this.assignedLocal = localName;
    }

    @Override
    public String getRelationName() {
        return "ActualReturn";
    }

    @Override
    public String toIODirective() {
        return FactWriter.twoParameters(invocationSite, assignedLocal);
    }
}
