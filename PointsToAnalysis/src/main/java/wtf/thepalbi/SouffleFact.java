package wtf.thepalbi;

public interface SouffleFact {
    /**
     * @return the name of the Souffle relation, as if written in Souffle script.
     */
    String getRelationName();

    /**
     * @return the formatted text-file IO directive for consuming from the execution of a Souffle script expecting this
     * relation as facts.
     */
    String toIODirective();
}
