package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

/**
 * Represents an instruction that allocates a new Heap object.
 */
public class AllocFact implements SouffleFact {
    private String variableName;
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
        return FactWriter.threeParameters(variableName, heapLocation, owningMethod);
    }
}
