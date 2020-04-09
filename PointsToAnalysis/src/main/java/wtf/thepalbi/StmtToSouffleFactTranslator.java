package wtf.thepalbi;

import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.NewExpr;
import soot.jimple.Stmt;
import wtf.thepalbi.relations.AllocFact;
import wtf.thepalbi.relations.SouffleFact;
import wtf.thepalbi.relations.StoreFact;
import wtf.thepalbi.relations.VarTypeFact;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class StmtToSouffleFactTranslator {
    private HeapLocationFactory heapLocationFactory;
    private HashSet<SouffleFact> collectedFacts;

    public StmtToSouffleFactTranslator(HeapLocationFactory heapLocationFactory) {
        this.heapLocationFactory = heapLocationFactory;
    }

    public void translateStmtFromMethod(Stmt stmt, SootMethod method) {
        if (stmt instanceof AssignStmt) {
            AssignStmt assignStmt = (AssignStmt) stmt;

            // First handle right-box. This will contained the element assigned to the left-box
            Value fromValue = assignStmt.getRightOp();
            Value toValue = assignStmt.getLeftOp();


            if (toValue instanceof Local && fromValue instanceof NewExpr) {
                // There's a new allocation in this statement, and it's being assigned to a local variable
                // TODO: Add missing generated facts here (VarType, etc).
                Local toLocal = (Local) toValue;
                SouffleFact typeFact = new VarTypeFact(uniqueLocalName(toLocal, method), toValue.getType().toString());
                collectedFacts.add(typeFact);

                SouffleFact allocFact = new AllocFact(uniqueLocalName(toLocal, method), heapLocationFactory.generate(), getMethodIdentifier(method));
                collectedFacts.add(allocFact);
            }


            if (assignStmt.getLeftOp() instanceof Local) {
            } else if (assignStmt.getLeftOp() instanceof FieldRef) {
                collectedFacts.add(this.routeFieldAssignmentFromMethod((FieldRef) assignStmt.getLeftOp(), method));
            }
        }
    }

    private SouffleFact routeFieldAssignmentFromMethod(FieldRef fieldRef, SootMethod method) {
        return new StoreFact("baseVariableName", fieldRef.getField().getName(), this.getMethodIdentifier(method));
    }

    private String getMethodIdentifier(SootMethod method) {
        return method.getSignature();
    }

    private String uniqueLocalName(Local local, SootMethod method) {
        return method.getSignature() + local.getName();
    }

    public Set<SouffleFact> translateMethodBody(Body body) {
        collectedFacts = new HashSet<>();

        for (Stmt codeStmt : body.getUnits().stream().map((unit -> (Stmt) unit)).collect(toList())) {
            this.translateStmtFromMethod(codeStmt, body.getMethod());
        }

        return collectedFacts;
    }
}
