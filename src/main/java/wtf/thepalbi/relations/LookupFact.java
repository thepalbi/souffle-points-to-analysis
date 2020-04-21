package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

import java.util.Objects;

public class LookupFact implements SouffleFact {
    private final String type;
    private final String signature;
    private final String method;

    public LookupFact(String type, String signature, String method) {
        this.type = type;
        this.signature = signature;
        this.method = method;
    }

    @Override
    public String getRelationName() {
        return "Lookup";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(type, signature, method);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LookupFact that = (LookupFact) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(signature, that.signature) &&
                Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, signature, method);
    }
}
