package wtf.thepalbi;

import soot.Body;
import soot.jimple.Stmt;
import wtf.thepalbi.relations.SouffleFact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PointsToFactGenerator {
    public Collection<SouffleFact> fromMethodBody(Body body) {
        StmtToSouffleFactTranslator translator = new StmtToSouffleFactTranslator(new UUIDHeapLocationFactory());
        return translator.translateMethodBody(body);
    }
}
