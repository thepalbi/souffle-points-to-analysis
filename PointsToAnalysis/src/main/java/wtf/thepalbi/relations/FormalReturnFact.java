package wtf.thepalbi.relations;

import soot.SootMethod;
import soot.SootMethodInterface;
import wtf.thepalbi.SouffleFact;

import java.util.Objects;

import static wtf.thepalbi.relations.FactWriter.writeMethod;

public class FormalReturnFact implements SouffleFact {
    private final SootMethodInterface method;
    private final String localName;

    public FormalReturnFact(SootMethodInterface method, String localName) {
        this.method = method;
        this.localName = localName;
    }

    @Override
    public String getRelationName() {
        return "FormalReturn";
    }

    @Override
    public String toIODirective() {
        return FactWriter.twoParameters(writeMethod(method), localName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormalReturnFact that = (FormalReturnFact) o;
        return Objects.equals(method, that.method) &&
                Objects.equals(localName, that.localName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, localName);
    }
}
