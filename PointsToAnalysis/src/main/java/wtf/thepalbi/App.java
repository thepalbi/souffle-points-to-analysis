package wtf.thepalbi;

import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.Stmt;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public void convertMethodBody(Body body) {
        StmtRouter router = new StmtRouter(new UUIDHeapLocationFactory());
        Collection<SouffleFact> factCollection = new HashSet<>();

        for (Stmt codeStmt : body.getUnits().stream().map((unit -> (Stmt) unit)).collect(Collectors.toList())) {
            factCollection.add(router.routeStmtFromMethod(codeStmt, body.getMethod()));
            // Going through each code statement, and converting to the Souffle fact when necessary
            // TODO: Add here some type of dispatching to the corresponding convert
        }
    }

    public class StmtRouter {
        private HeapLocationFactory heapLocationFactory;

        public StmtRouter(HeapLocationFactory heapLocationFactory) {
            this.heapLocationFactory = heapLocationFactory;
        }

        public SouffleFact routeStmtFromMethod(Stmt stmt, SootMethod method) {
            if (stmt instanceof AssignStmt) {
                AssignStmt assignStmt = (AssignStmt) stmt;
                if (assignStmt.getLeftOp() instanceof Local) {
                    this.routeLocalAssignmentFromMethod((Local) assignStmt.getLeftOp(), method);
                } else if (assignStmt.getLeftOp() instanceof FieldRef) {
                    this.routeFieldAssignmentFromMethod((FieldRef) assignStmt.getLeftOp(), method);
                }

            }
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

    public class AllocFact implements SouffleFact {
        private String FACT_FORMAT_STRING = "Alloc(%s, %s, %s).";

        private String variableName;

        // TODO: Define some heap location abstraction later
        private String heapLocation;

        private String owningMethod;

        public AllocFact(String variableName, String heapLocation, String parentMethod) {
            this.variableName = variableName;
            this.heapLocation = heapLocation;
            this.owningMethod = parentMethod;
        }

        @Override
        public String translateToDatalog() {
            return String.format(FACT_FORMAT_STRING, this.variableName, this.heapLocation, this.owningMethod);
        }
    }
}
