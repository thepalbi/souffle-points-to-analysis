package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

public class AllocFact implements SouffleFact {
    private String FACT_FORMAT_STRING = "Alloc(%s, %s, %s).";

    private String variableName;

    // TODO: Define some heap location abstraction later
    private String heapLocation;

    private String owningMethod;

    public AllocFact(String variableName, String heapLocation, String parentMethod) {
        this.variableName = variableName;
        this.heapLocation = heapLocation;
        this.owningMethod = parentMethod;
    }

    @Override
    public String getRelationName() {
        return "Alloc";
    }

    @Override
    public String toIODirective() {
        return String.format(FACT_FORMAT_STRING, this.variableName, this.heapLocation, this.owningMethod);
    }
}
