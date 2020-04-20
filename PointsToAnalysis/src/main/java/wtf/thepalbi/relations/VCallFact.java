package wtf.thepalbi.relations;

import soot.SootMethod;
import soot.SootMethodRef;
import wtf.thepalbi.SouffleFact;

import java.util.Objects;

import static wtf.thepalbi.relations.FactWriter.writeMethod;

public class VCallFact implements SouffleFact {
    private final String baseLocalName;
    private final String calledMethodSignature;
    private final String invocationSite;
    private final SootMethod method;
    private SootMethodRef calledMethodRef;

    public VCallFact(String baseLocalName, String calledMethodSignature, String invocationSite, SootMethod method, SootMethodRef calledMethodRef) {
        this.baseLocalName = baseLocalName;
        this.calledMethodSignature = calledMethodSignature;
        this.invocationSite = invocationSite;
        this.method = method;
        this.calledMethodRef = calledMethodRef;
    }

    @Override
    public String getRelationName() {
        return "VCall";
    }

    @Override
    public String toIODirective() {
        return FactWriter.fourParameters(baseLocalName, calledMethodSignature, invocationSite, writeMethod(method));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VCallFact vCallFact = (VCallFact) o;
        return Objects.equals(baseLocalName, vCallFact.baseLocalName) &&
                Objects.equals(calledMethodSignature, vCallFact.calledMethodSignature) &&
                Objects.equals(invocationSite, vCallFact.invocationSite) &&
                Objects.equals(method, vCallFact.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseLocalName, calledMethodSignature, invocationSite, method);
    }

    public SootMethodRef getCalledMethodRef() {
        return calledMethodRef;
    }
}
