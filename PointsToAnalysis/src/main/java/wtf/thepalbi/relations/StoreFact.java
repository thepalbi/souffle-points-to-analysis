package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

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
}
