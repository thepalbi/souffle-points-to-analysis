package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

import java.util.Objects;

public class LoadFact implements SouffleFact {
    private final String toLocal;
    private final String base;
    private final String fieldName;

    public LoadFact(String toLocal, String fromFieldBase, String fieldName) {
        this.toLocal = toLocal;
        this.base = fromFieldBase;
        this.fieldName = fieldName;
    }

    @Override
    public String getRelationName() {
        return "Load";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(toLocal, base, fieldName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadFact loadFact = (LoadFact) o;
        return Objects.equals(toLocal, loadFact.toLocal) &&
                Objects.equals(base, loadFact.base) &&
                Objects.equals(fieldName, loadFact.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toLocal, base, fieldName);
    }
}
