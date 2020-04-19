package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocFact allocFact = (AllocFact) o;
        return Objects.equals(variableName, allocFact.variableName) &&
                Objects.equals(heapLocation, allocFact.heapLocation) &&
                Objects.equals(owningMethod, allocFact.owningMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableName, heapLocation, owningMethod);
    }
}
