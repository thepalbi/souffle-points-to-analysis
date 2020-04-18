package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

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
}
