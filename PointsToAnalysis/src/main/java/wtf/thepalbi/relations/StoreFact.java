package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

/**
 * Represents an assigment from a variable to an object field.
 */
public class StoreFact implements SouffleFact {

    public StoreFact(String baseLocalName, String fieldName, String fromLocal) {
    }

    @Override
    public String getRelationName() {
        return "Store";
    }

    @Override
    public String toIODirective() {
        return null;
    }
}
