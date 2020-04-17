package wtf.thepalbi;

import soot.*;
import soot.jimple.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import wtf.thepalbi.relations.*;
import wtf.thepalbi.utils.FeatureNotImplementedException;
import wtf.thepalbi.utils.HeapLocationFactory;

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
            Value toValue = assignStmt.getLeftOp();
            Value fromValue = assignStmt.getRightOp();

            if (toValue instanceof Local && fromValue instanceof NewExpr) {
                // There's a new allocation in this statement, and it's being assigned to a local variable
                Local toLocal = (Local) toValue;

                // HeapType
                String newHeapLocation = heapLocationFactory.generate();
                SouffleFact heapTypeFact = new HeapType(newHeapLocation, fromValue.getType().toString());

                // Alloc
                SouffleFact allocFact = new AllocFact(uniqueLocalName(toLocal, method), newHeapLocation, getMethodIdentifier(method));

                collectedFacts.add(allocFact);
                collectedFacts.add(heapTypeFact);
            } else if (toValue instanceof Local && fromValue instanceof Local) {
                // Move
                Local toLocal = (Local) toValue;
                Local fromLocal = (Local) fromValue;
                collectedFacts.add(new MoveFact(uniqueLocalName(toLocal, method), uniqueLocalName(fromLocal, method)));
            } else if (toValue instanceof Local && fromValue instanceof InvokeExpr) {
                // TODO: Implement Invoke (instance or other types) to Local assignment
            } else if (toValue instanceof InstanceFieldRef && fromValue instanceof Local) {
                // NOTE: Static fields not handled. I think they are not involved in Points-To resolution?
                // Store
                InstanceFieldRef toField = (InstanceFieldRef) toValue;

                if (!(toField.getBase() instanceof Local)) {
                    throw new FeatureNotImplementedException("STORE facts to non-locals bases");
                }

                Local toFieldBase = (Local) toField.getBase();
                Local fromLocal = (Local) fromValue;
                SouffleFact storeFact = new StoreFact(
                        uniqueLocalName(toFieldBase, method),
                        toField.getField().getSignature(),
                        uniqueLocalName(fromLocal, method));
                collectedFacts.add(storeFact);
            } else if (toValue instanceof Local && fromValue instanceof InstanceFieldRef) {
                // NOTE: Static fields not handled. Maybe in here they are necessary.
                // Load
                InstanceFieldRef fromField = (InstanceFieldRef) fromValue;

                if (!(fromField.getBase() instanceof Local)) {
                    throw new FeatureNotImplementedException("LOAD facts from non-locals bases");
                }

                Local fromFieldBase = (Local) fromField.getBase();

                Local toLocal = (Local) toValue;
                SouffleFact loadFact = new LoadFact(
                        uniqueLocalName(toLocal, method),
                        uniqueLocalName(fromFieldBase, method),
                        fromField.getField().getSignature());
                collectedFacts.add(loadFact);
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

        // VarType
        // All locals can be collected from the supplied method body
        body.getLocals().stream().forEach(local -> {
            SouffleFact typeFact = new VarTypeFact(uniqueLocalName(local, body.getMethod()), local.getType().toString());
            collectedFacts.add(typeFact);
        });

        Local thisLocal = body.getThisLocal();
        SouffleFact thisVarFact = new ThisVarFact(uniqueLocalName(thisLocal, body.getMethod()), body.getMethod());
        collectedFacts.add(thisVarFact);

        for (Stmt codeStmt : body.getUnits().stream().map((unit -> (Stmt) unit)).collect(toList())) {
            this.translateStmtFromMethod(codeStmt, body.getMethod());
        }

        return collectedFacts;
    }
}
