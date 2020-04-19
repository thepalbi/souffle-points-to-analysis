package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

import java.util.Objects;

import static java.lang.String.valueOf;

public class ActualArgFact implements SouffleFact {
    private final String invocationSite;
    private final int parameterNumber;
    private final String assigned;

    public ActualArgFact(String invocationSite, int parameterNumber, String assignedLocalName) {
        this.invocationSite = invocationSite;
        this.parameterNumber = parameterNumber;
        this.assigned = assignedLocalName;
    }

    @Override
    public String getRelationName() {
        return "ActualArg";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(invocationSite, valueOf(parameterNumber), assigned);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActualArgFact that = (ActualArgFact) o;
        return parameterNumber == that.parameterNumber &&
                Objects.equals(invocationSite, that.invocationSite) &&
                Objects.equals(assigned, that.assigned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invocationSite, parameterNumber, assigned);
    }
}
