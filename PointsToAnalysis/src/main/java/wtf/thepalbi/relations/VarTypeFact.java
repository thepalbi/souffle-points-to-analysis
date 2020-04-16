package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

/**
 * Represents the type of a local variable.
 */
public class VarTypeFact implements SouffleFact {
    private final String localName;
    private final String type;

    public VarTypeFact(String localName, String typeName) {
        this.localName = localName;
        this.type = typeName;
    }

    @Override
    public String getRelationName() {
        return "VarType";
    }

    @Override
    public String toIODirective() {
        return null;
    }
}
