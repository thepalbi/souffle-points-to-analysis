package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;
import wtf.thepalbi.TypeFact;

/**
 * Matches a heap object to its type.
 */
public class HeapTypeFact implements SouffleFact, TypeFact {
    private final String heapLocation;

    private final String type;

    public HeapTypeFact(String heapLocation, String type) {
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

    @Override
    public String getType() {
        return type;
    }
}
