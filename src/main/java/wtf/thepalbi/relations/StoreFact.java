package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

import java.util.Objects;

/**
 * Represents an assigment from a variable to an object field.
 */
public class StoreFact implements SouffleFact {

    private final String base;
    private final String fieldName;
    private final String fromLocal;

    public StoreFact(String base, String fieldName, String fromLocal) {
        this.base = base;
        this.fieldName = fieldName;
        this.fromLocal = fromLocal;
    }

    @Override
    public String getRelationName() {
        return "Store";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(base, fieldName, fromLocal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreFact storeFact = (StoreFact) o;
        return Objects.equals(base, storeFact.base) &&
                Objects.equals(fieldName, storeFact.fieldName) &&
                Objects.equals(fromLocal, storeFact.fromLocal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, fieldName, fromLocal);
    }
}
