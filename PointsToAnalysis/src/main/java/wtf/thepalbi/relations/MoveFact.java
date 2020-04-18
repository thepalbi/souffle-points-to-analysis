package wtf.thepalbi.relations;

import wtf.thepalbi.SouffleFact;

public class MoveFact implements SouffleFact {
    private final String toLocal;
    private final String fromLocal;

    public MoveFact(String toLocal, String fromLocal) {
        this.toLocal = toLocal;
        this.fromLocal = fromLocal;
    }

    @Override
    public String getRelationName() {
        return "Move";
    }

    @Override
    public String toIODirective() {
        return FactWriter.twoParameters(toLocal, fromLocal);
    }
}
