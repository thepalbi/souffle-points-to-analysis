package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

import static java.lang.String.valueOf;

public class ActualArgFact implements SouffleFact {
    private final String invocationSite;
    private final int parameterNumber;
    private final String assigned;

    public ActualArgFact(String invocationSite, int parameterNumber, String assignedLocalName) {
        this.invocationSite = invocationSite;
        this.parameterNumber = parameterNumber;
        this.assigned = assignedLocalName;
    }

    @Override
    public String getRelationName() {
        return "ActualArg";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(invocationSite, valueOf(parameterNumber), assigned);
    }
}
