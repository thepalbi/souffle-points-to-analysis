package wtf.thepalbi;

import soot.*;
import soot.jimple.*;
import wtf.thepalbi.relations.*;
import wtf.thepalbi.utils.FeatureNotImplementedException;
import wtf.thepalbi.utils.HeapLocationFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static wtf.thepalbi.relations.FactWriter.writeMethod;

public class StmtToSouffleFactTranslator {
    private HeapLocationFactory heapLocationFactory;
    private HashSet<SouffleFact> collectedFacts;
    private Map<String, Integer> nextInvocationSiteUID = new HashMap<>();

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
                SouffleFact heapTypeFact = new HeapTypeFact(newHeapLocation, fromValue.getType());

                // Alloc
                SouffleFact allocFact = new AllocFact(uniqueLocalName(toLocal, method), newHeapLocation, method);

                collectedFacts.add(allocFact);
                collectedFacts.add(heapTypeFact);
            } else if (toValue instanceof Local && fromValue instanceof Local) {
                // Move
                Local toLocal = (Local) toValue;
                Local fromLocal = (Local) fromValue;
                collectedFacts.add(new MoveFact(uniqueLocalName(toLocal, method), uniqueLocalName(fromLocal, method)));
            } else if (toValue instanceof Local && fromValue instanceof InvokeExpr) {
                Local toLocal = (Local) toValue;
                String invocationSite = getInvocationSite(method, stmt.getJavaSourceStartLineNumber());
                translateMethodInvocation(invocationSite, method, (InvokeExpr) fromValue);
                // ActualReturn
                collectedFacts.add(new ActualReturnFact(invocationSite, uniqueLocalName(toLocal, method)));
            } else if (toValue instanceof InstanceFieldRef) {
                // NOTE: Static fields not handled. I think they are not involved in Points-To resolution?
                // Store
                InstanceFieldRef toField = (InstanceFieldRef) toValue;

                if (!(toField.getBase() instanceof Local)) {
                    throw new FeatureNotImplementedException("'Store' facts to non-locals bases");
                }

                Local toFieldBase = (Local) toField.getBase();
                if (fromValue instanceof Local) {
                    SouffleFact storeFact = new StoreFact(
                            uniqueLocalName(toFieldBase, method),
                            toField.getField().getSignature(),
                            uniqueLocalName((Local) fromValue, method));
                    collectedFacts.add(storeFact);
                } else if (fromValue instanceof Immediate) {
                    String immediateHeapLoc = heapLocationFactory.generate();
                    collectedFacts.add(new HeapTypeFact(immediateHeapLoc, fromValue.getType()));
                    collectedFacts.add(new StoreImmediateFact(
                            uniqueLocalName(toFieldBase, method),
                            toField.getField().getSignature(),
                            immediateHeapLoc));
                } else {
                    System.out.format("Assigning field from %s not supported\n", fromValue.getClass());
                }
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
            } else {
                // TODO: Improve logging here
                System.out.format("Assign statements with ops: %s - %s not supported\n",
                        assignStmt.getLeftOp().getClass(),
                        assignStmt.getRightOp().getClass());
            }
        } else if (stmt instanceof ReturnStmt) {

            // FormalReturn
            ReturnStmt returnStmt = (ReturnStmt) stmt;
            if (returnStmt.getOp() instanceof Local) {
                collectedFacts.add(new FormalReturnFact(method, uniqueLocalName((Local) returnStmt.getOp(), method)));
            } else if (returnStmt.getOp() instanceof Immediate) {

                // The returned object by the method is an immediate, so should simulate an allocate and heap type here
                String heapLocation = heapLocationFactory.generate();
                String variableName = writeMethod(method) + "return_immediate";
                collectedFacts.add(new AllocFact(variableName, heapLocation, method));
                collectedFacts.add(new HeapTypeFact(heapLocation, returnStmt.getOp().getType()));
                collectedFacts.add(new FormalReturnFact(method, variableName));
            } else {
                throw new FeatureNotImplementedException("'FormalReturn' not implemented for " + returnStmt.getOp().getClass().getName());
            }
        } else if (stmt instanceof InvokeStmt) {
            String invocationSite = getInvocationSite(method, stmt.getJavaSourceStartLineNumber());
            translateMethodInvocation(invocationSite, method, stmt.getInvokeExpr());
        }
    }

    private String getInvocationSite(SootMethod method, int inMethodIdentifier) {
        String invocationSiteName = String.format("%s:%d", writeMethod(method), inMethodIdentifier);
        int invocationSiteUID = nextInvocationSiteUID.getOrDefault(invocationSiteName, 0);
        String uniqueInvocationSite = invocationSiteName + ":" + invocationSiteUID;
        nextInvocationSiteUID.put(invocationSiteName, invocationSiteUID + 1);
        return uniqueInvocationSite;
    }

    private void translateMethodInvocation(String invocationSite, SootMethod method, InvokeExpr invocation) {
        // invocation is either an instance or static invocation
        if (!(invocation instanceof InstanceInvokeExpr) && !(invocation instanceof StaticInvokeExpr)) {
            // TODO: Log something, not currently handling this
            // Skipping
            return;
        }

        if (invocation instanceof InstanceInvokeExpr) {
            InstanceInvokeExpr invokeExpr = (InstanceInvokeExpr) invocation;
            if (!(invokeExpr.getBase() instanceof Local)) {
                throw new FeatureNotImplementedException("'VCall' does not support non-local bases");
            }

            Local callBase = (Local) invokeExpr.getBase();
            // Called method signature, prepared for lookup. SubSignature is the signature of the method without the owning class.
            String calledMethodSignature = invokeExpr.getMethodRef().getSubSignature().getString();
            // NOTE: Saving Soot called method ref to handle fact post-processing
            // VCall
            // FIXME: Case when a constructor call the constructor of the Super class. Both are resolved as the same method.
            //  See ClassUnderTest3 call graph.
            collectedFacts.add(new VCallFact(
                    uniqueLocalName(callBase, method),
                    calledMethodSignature,
                    invocationSite,
                    method,
                    invokeExpr.getMethodRef()));
        } else if (invocation instanceof StaticInvokeExpr) {
            StaticInvokeExpr staticInvokeExpr = (StaticInvokeExpr) invocation;

            collectedFacts.add(new StaticVCallFact(
                    invocationSite,
                    FactWriter.writeMethod(method),
                    FactWriter.writeMethod(staticInvokeExpr.getMethodRef()),
                    staticInvokeExpr.getMethodRef()));
        }

        // ActualArg
        for (int i = 0; i < invocation.getArgCount(); i++) {
            Value ithParameterValue = invocation.getArg(i);

            if (!(ithParameterValue instanceof Local)) {
                // TODO: Log something about a non-local argument in a instance virtual call
                continue;
            }

            Local ithParameterLocal = (Local) ithParameterValue;
            collectedFacts.add(new ActualArgFact(invocationSite, i, uniqueLocalName(ithParameterLocal, method)));
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
            SouffleFact typeFact = new VarTypeFact(uniqueLocalName(local, body.getMethod()), local.getType());
            // NOTE: VarType facts not used in current points-to implementation
            // collectedFacts.add(typeFact);
        });

        // ThisVar
        // Check if it's static prior to obtain this var.
        if (!body.getMethod().isStatic()) {
            Local thisLocal = body.getThisLocal();
            SouffleFact thisVarFact = new ThisVarFact(uniqueLocalName(thisLocal, body.getMethod()), body.getMethod());
            collectedFacts.add(thisVarFact);
        }

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
