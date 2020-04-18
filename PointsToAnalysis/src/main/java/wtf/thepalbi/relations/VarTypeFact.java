package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;
import wtf.thepalbi.TypeFact;

/**
 * Represents the type of a local variable.
 */
public class VarTypeFact implements SouffleFact, TypeFact {
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
        return FactWriter.twoParameters(localName, type);
    }

    @Override
    public String getType() {
        return type;
    }
}
