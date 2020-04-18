package wtf.thepalbi;

import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.Value;
import soot.jimple.*;
import wtf.thepalbi.relations.*;
import wtf.thepalbi.utils.FeatureNotImplementedException;
import wtf.thepalbi.utils.HeapLocationFactory;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static wtf.thepalbi.relations.FactWriter.writeMethod;

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
                SouffleFact allocFact = new AllocFact(uniqueLocalName(toLocal, method), newHeapLocation, writeMethod(method));

                collectedFacts.add(allocFact);
                collectedFacts.add(heapTypeFact);
            } else if (toValue instanceof Local && fromValue instanceof Local) {
                // Move
                Local toLocal = (Local) toValue;
                Local fromLocal = (Local) fromValue;
                collectedFacts.add(new MoveFact(uniqueLocalName(toLocal, method), uniqueLocalName(fromLocal, method)));
            } else if (toValue instanceof Local && fromValue instanceof InvokeExpr) {
                // TODO: Implement Invoke (instance or other types) to Local assignment
                Local toLocal = (Local) toValue;

                if (!(fromValue instanceof InstanceInvokeExpr)) {
                    // TODO: Log something, not currently handling this
                    // Skipping
                    return;
                }

                InstanceInvokeExpr invokeExpr = (InstanceInvokeExpr) fromValue;

                if (!(invokeExpr.getBase() instanceof Local)) {
                    throw new FeatureNotImplementedException("'VCall' does not support non-local bases");
                }

                Local callBase = (Local) invokeExpr.getBase();

                // Generate invocation site
                // NOTE: Characterizing an invocation site with methodsSignature and the line # inside the Java source
                String invocationSite = String.format("%s:%d", writeMethod(method), stmt.getJavaSourceStartLineNumber());

                // Called method signature, prepared for lookup. SubSignature is the signature of the method without the owning class.
                String calledMethodSignature = invokeExpr.getMethodRef().getSubSignature().getString();

                // VCall
                collectedFacts.add(new VCallFact(
                        uniqueLocalName(callBase, method),
                        calledMethodSignature,
                        invocationSite,
                        method));

                // ActualArg
                for (int i = 0; i < invokeExpr.getArgCount(); i++) {
                    Value ithParameterValue = invokeExpr.getArg(i);

                    if (!(ithParameterValue instanceof Local)) {
                        // TODO: Log something about a non-local argument in a instance virtual call
                        continue;
                    }

                    Local ithParameterLocal = (Local) ithParameterValue;
                    collectedFacts.add(new ActualArgFact(invocationSite, i, uniqueLocalName(ithParameterLocal, method)));
                }

                // ActualReturn
                collectedFacts.add(new ActualReturnFact(invocationSite, uniqueLocalName(toLocal, method)));


            } else if (toValue instanceof InstanceFieldRef && fromValue instanceof Local) {
                // NOTE: Static fields not handled. I think they are not involved in Points-To resolution?
                // Store
                InstanceFieldRef toField = (InstanceFieldRef) toValue;

                if (!(toField.getBase() instanceof Local)) {
                    throw new FeatureNotImplementedException("'Store' facts to non-locals bases");
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
                    throw new FeatureNotImplementedException("'Load' facts from non-locals bases");
                }

                Local fromFieldBase = (Local) fromField.getBase();

                Local toLocal = (Local) toValue;
                SouffleFact loadFact = new LoadFact(
                        uniqueLocalName(toLocal, method),
                        uniqueLocalName(fromFieldBase, method),
                        fromField.getField().getSignature());
                collectedFacts.add(loadFact);
            }
        } else if (stmt instanceof ReturnStmt) {

            ReturnStmt returnStmt = (ReturnStmt) stmt;
            if (!(returnStmt.getOp() instanceof Local)) {
                throw new FeatureNotImplementedException("'FormalReturn' with non-local return op");
            }

            // FormalReturn
            collectedFacts.add(new FormalReturnFact(method, uniqueLocalName((Local) returnStmt.getOp(), method)));
        }
    }

    private String uniqueLocalName(Local local, SootMethod method) {
        return writeMethod(method) + local.getName();
    }

    public Set<SouffleFact> translateMethodBody(Body body) {
        collectedFacts = new HashSet<>();

        // VarType
        // All locals can be collected from the supplied method body
        body.getLocals().stream().forEach(local -> {
            SouffleFact typeFact = new VarTypeFact(uniqueLocalName(local, body.getMethod()), local.getType().toString());
            collectedFacts.add(typeFact);
        });

        // ThisVar
        Local thisLocal = body.getThisLocal();
        SouffleFact thisVarFact = new ThisVarFact(uniqueLocalName(thisLocal, body.getMethod()), body.getMethod());
        collectedFacts.add(thisVarFact);

        // FormalArg
        for (int i = 0; i < body.getMethod().getParameterCount(); i++) {
            Local localForIthParameter = body.getParameterLocal(i);
            collectedFacts.add(new FormalArgFact(body.getMethod(), i, uniqueLocalName(localForIthParameter, body.getMethod())));
        }

        for (Stmt codeStmt : body.getUnits().stream().map((unit -> (Stmt) unit)).collect(toList())) {
            this.translateStmtFromMethod(codeStmt, body.getMethod());
        }

        return collectedFacts;
    }
}
