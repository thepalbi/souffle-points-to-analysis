package wtf.thepalbi;

import soot.SootMethod;

import java.util.List;
import java.util.Map;

/**
 * Result of a {@link PointToAnalysis}.
 */
public class PointsToResult {
    // TODO: Implement Soot interface for PointsToAnalysis
    // Doing this would involve maintaining a relation between serialized Locals and Locals instances in Soot.
    // Maybe add an additional test fact to track serializedLocal -> Local instance mapping, and make
    // some facts just internal.
    private Map<String, List<HeapObject>> localToHeapObject;

    protected PointsToResult(Map<String, List<HeapObject>> localToHeapObject) {
        this.localToHeapObject = localToHeapObject;
    }

    /**
     * Check to which objects a Local variable might point to.
     *
     * @param method    A method in which to look for a local
     * @param localName The name of the local
     * @return A list of {@link HeapObject}s to which the local might point to.
     */
    public List<HeapObject> localPointsTo(SootMethod method, String localName) {
        return localToHeapObject.get(method.getSignature() + localName);
    }
}
