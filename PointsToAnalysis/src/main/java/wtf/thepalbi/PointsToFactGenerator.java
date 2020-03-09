package wtf.thepalbi;

import soot.Body;
import soot.jimple.Stmt;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class PointsToFactGenerator {
    public Collection<SouffleFact> fromMethodBody(Body body) {
        StmtRouter router = new StmtRouter(new UUIDHeapLocationFactory());
        Collection<SouffleFact> factCollection = new HashSet<>();

        for (Stmt codeStmt : body.getUnits().stream().map((unit -> (Stmt) unit)).collect(Collectors.toList())) {
            factCollection.add(router.routeStmtFromMethod(codeStmt, body.getMethod()));
            // Going through each code statement, and converting to the Souffle fact when necessary
            // TODO: Add here some type of dispatching to the corresponding convert
        }
        return factCollection;
    }
}
