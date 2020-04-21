package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

import java.util.Objects;

public class ActualReturnFact implements SouffleFact {
    private final String invocationSite;
    private final String assignedLocal;

    public ActualReturnFact(String invocationSite, String localName) {
        this.invocationSite = invocationSite;
        this.assignedLocal = localName;
    }

    @Override
    public String getRelationName() {
        return "ActualReturn";
    }

    @Override
    public String toIODirective() {
        return FactWriter.twoParameters(invocationSite, assignedLocal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActualReturnFact that = (ActualReturnFact) o;
        return Objects.equals(invocationSite, that.invocationSite) &&
                Objects.equals(assignedLocal, that.assignedLocal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invocationSite, assignedLocal);
    }
}
