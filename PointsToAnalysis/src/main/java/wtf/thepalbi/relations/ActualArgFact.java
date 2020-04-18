package wtf.thepalbi.relations;

import soot.Local;
import wtf.thepalbi.SouffleFact;

public class ActualArgFact implements SouffleFact {
    public ActualArgFact(String invocationSite, int parameterNumber, String assignedLocalName) {
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
