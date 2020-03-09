package wtf.thepalbi;

import soot.Local;
import soot.SootMethod;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.Stmt;

public class StmtRouter {
    private HeapLocationFactory heapLocationFactory;

    public StmtRouter(HeapLocationFactory heapLocationFactory) {
        this.heapLocationFactory = heapLocationFactory;
    }

    public SouffleFact routeStmtFromMethod(Stmt stmt, SootMethod method) {
        if (stmt instanceof AssignStmt) {
            AssignStmt assignStmt = (AssignStmt) stmt;
            if (assignStmt.getLeftOp() instanceof Local) {
                return this.routeLocalAssignmentFromMethod((Local) assignStmt.getLeftOp(), method);
            } else if (assignStmt.getLeftOp() instanceof FieldRef) {
                return this.routeFieldAssignmentFromMethod((FieldRef) assignStmt.getLeftOp(), method);
            }
        }
        return null;
    }

    private SouffleFact routeFieldAssignmentFromMethod(FieldRef fieldRef, SootMethod method) {
        return new StoreFact("baseVariableName", fieldRef.getField().getName(), this.getMethodIdentifier(method));
    }

    private SouffleFact routeLocalAssignmentFromMethod(Local assignee, SootMethod method) {
        // TODO: Check method name generated. It should include both signature, method name, and fully qualified name of class
        return new AllocFact(
                assignee.getName(),
                this.heapLocationFactory.generate(),
                getMethodIdentifier(method));
    }

    private String getMethodIdentifier(SootMethod method) {
        return method.getDeclaringClass().getJavaStyleName() + method.getSignature();
    }
}
