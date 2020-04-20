package wtf.thepalbi.relations;

import soot.SootMethodRef;
import wtf.thepalbi.SouffleFact;

import java.util.Objects;

public class StaticVCallFact implements SouffleFact {
    private final String invocationSite;
    private final String callee;
    private final String calledMethod;
    private final SootMethodRef calledMethodRef;

    public StaticVCallFact(String invocationSite, String callee, String calledMethod, SootMethodRef calledMethodRef) {
        this.invocationSite = invocationSite;
        this.callee = callee;
        this.calledMethod = calledMethod;
        this.calledMethodRef = calledMethodRef;
    }

    @Override
    public String getRelationName() {
        return "StaticVCall";
    }

    @Override
    public String toIODirective() {
        return FactWriter.threeParameters(invocationSite, callee, calledMethod);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticVCallFact that = (StaticVCallFact) o;
        return Objects.equals(invocationSite, that.invocationSite) &&
                Objects.equals(callee, that.callee) &&
                Objects.equals(calledMethod, that.calledMethod) &&
                Objects.equals(calledMethodRef, that.calledMethodRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invocationSite, callee, calledMethod, calledMethodRef);
    }
}
