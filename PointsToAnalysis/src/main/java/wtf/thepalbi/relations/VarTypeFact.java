package wtf.thepalbi.relations;

import soot.Type;
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
    private final Type type;

    public VarTypeFact(String localName, Type type) {
        this.localName = localName;
        this.type = type;
    }

    @Override
    public String getRelationName() {
        return "VarType";
    }

    @Override
    public String toIODirective() {
        return FactWriter.twoParameters(localName, type.toString());
    }

    @Override
    public Type getType() {
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
