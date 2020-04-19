package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;
import wtf.thepalbi.TypeFact;

import java.util.Objects;

/**
 * Represents the type of a local variable.
 *
 * NOTE: This is not used by vanilla andersen analysis
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarTypeFact that = (VarTypeFact) o;
        return Objects.equals(localName, that.localName) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localName, type);
    }
}
