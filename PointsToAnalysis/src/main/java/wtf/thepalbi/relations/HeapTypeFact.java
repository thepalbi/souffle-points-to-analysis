package wtf.thepalbi.relations;

import soot.Type;
import wtf.thepalbi.SouffleFact;
import wtf.thepalbi.TypeFact;

import java.util.Objects;

/**
 * Matches a heap object to its type.
 */
public class HeapTypeFact implements SouffleFact, TypeFact {
    private final String heapLocation;
    private final Type type;

    public HeapTypeFact(String heapLocation, Type type) {
        this.heapLocation = heapLocation;
        this.type = type;
    }

    @Override
    public String getRelationName() {
        return "HeapType";
    }

    @Override
    public String toIODirective() {
        return FactWriter.twoParameters(heapLocation, type.toString());
    }

    @Override
    public Type getType() {
        return type;
    }

    public String getHeapLocation() {
        return heapLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeapTypeFact that = (HeapTypeFact) o;
        return Objects.equals(heapLocation, that.heapLocation) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heapLocation, type);
    }
}
