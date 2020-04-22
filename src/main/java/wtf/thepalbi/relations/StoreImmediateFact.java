package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

import java.util.Objects;

public class StoreImmediateFact implements SouffleFact {
    private final String baseLocalName;
    private final String fieldSignature;
    private final String heapLoc;

    public StoreImmediateFact(String baseLocalName, String fieldSignature, String heapLoc) {
        this.baseLocalName = baseLocalName;
        this.fieldSignature = fieldSignature;
        this.heapLoc = heapLoc;
    }

    @Override
    public String getRelationName() {
        return "StoreImmediate";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(baseLocalName, fieldSignature, heapLoc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreImmediateFact that = (StoreImmediateFact) o;
        return Objects.equals(baseLocalName, that.baseLocalName) &&
                Objects.equals(fieldSignature, that.fieldSignature) &&
                Objects.equals(heapLoc, that.heapLoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseLocalName, fieldSignature, heapLoc);
    }
}
