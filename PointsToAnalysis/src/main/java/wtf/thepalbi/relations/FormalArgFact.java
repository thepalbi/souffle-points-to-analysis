package wtf.thepalbi.relations;

import soot.SootMethod;
import wtf.thepalbi.SouffleFact;

import java.util.Objects;

import static wtf.thepalbi.relations.FactWriter.writeMethod;

public class FormalArgFact implements SouffleFact {
    private final SootMethod method;
    private final int parameterNumber;
    private final String localName;

    public FormalArgFact(SootMethod method, int parameterNumber, String localName) {
        this.method = method;
        this.parameterNumber = parameterNumber;
        this.localName = localName;
    }

    @Override
    public String getRelationName() {
        return "FormalArg";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(writeMethod(method), String.valueOf(parameterNumber), localName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormalArgFact that = (FormalArgFact) o;
        return parameterNumber == that.parameterNumber &&
                Objects.equals(method, that.method) &&
                Objects.equals(localName, that.localName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, parameterNumber, localName);
    }
}
