package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

import java.util.Objects;

public class MoveFact implements SouffleFact {
    private final String toLocal;
    private final String fromLocal;

    public MoveFact(String toLocal, String fromLocal) {
        this.toLocal = toLocal;
        this.fromLocal = fromLocal;
    }

    @Override
    public String getRelationName() {
        return "Move";
    }

    @Override
    public String toIODirective() {
        return FactWriter.twoParameters(toLocal, fromLocal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveFact moveFact = (MoveFact) o;
        return Objects.equals(toLocal, moveFact.toLocal) &&
                Objects.equals(fromLocal, moveFact.fromLocal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toLocal, fromLocal);
    }
}
