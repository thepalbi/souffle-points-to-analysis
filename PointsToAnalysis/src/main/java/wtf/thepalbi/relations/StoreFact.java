package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

public class StoreFact implements SouffleFact {
    private String baseVariableName;
    private String fieldName;
    private String owningClass;

    public StoreFact(String baseVariableName, String fieldName, String owningClass) {
        this.baseVariableName = baseVariableName;
        this.fieldName = fieldName;
        this.owningClass = owningClass;
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
