package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

/**
 * Matches a heap object to its type.
 */
public class HeapType implements SouffleFact {
    private final String heapLocation;
    private final String type;

    public HeapType(String heapLocation, String type) {
        this.heapLocation = heapLocation;
        this.type = type;
    }

    @Override
    public String getRelationName() {
        return "HeapType";
    }

    @Override
    public String toIODirective() {
        return FactWriter.twoParameters(heapLocation, type);
    }
}
