package wtf.thepalbi;

import soot.Body;
import wtf.thepalbi.utils.UUIDHeapLocationFactory;

import java.util.Collection;

public class PointsToFactGenerator {
    public Collection<SouffleFact> fromMethodBody(Body body) {
        StmtToSouffleFactTranslator translator = new StmtToSouffleFactTranslator(new UUIDHeapLocationFactory());
        return translator.translateMethodBody(body);
    }
}
