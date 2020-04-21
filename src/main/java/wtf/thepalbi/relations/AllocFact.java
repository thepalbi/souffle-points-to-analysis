package wtf.thepalbi.relations;

import soot.SootMethodInterface;
import wtf.thepalbi.SouffleFact;

import java.util.Objects;

import static wtf.thepalbi.relations.FactWriter.writeMethod;

/**
 * Represents an instruction that allocates a new Heap object.
 */
public class AllocFact implements SouffleFact {
    private String variableName;
    private String heapLocation;
    private SootMethodInterface inMethod;

    public AllocFact(String variableName, String heapLocation, SootMethodInterface inMethod) {
        this.variableName = variableName;
        this.heapLocation = heapLocation;
        this.inMethod = inMethod;
    }

    @Override
    public String getRelationName() {
        return "Alloc";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(variableName, heapLocation, writeMethod(inMethod));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocFact allocFact = (AllocFact) o;
        return Objects.equals(variableName, allocFact.variableName) &&
                Objects.equals(heapLocation, allocFact.heapLocation) &&
                Objects.equals(inMethod, allocFact.inMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableName, heapLocation, inMethod);
    }
}
