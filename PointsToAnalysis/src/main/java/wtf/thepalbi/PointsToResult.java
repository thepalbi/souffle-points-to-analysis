package wtf.thepalbi;

import soot.SootMethod;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PointsToResult {
    private Map<String, List<HeapObject>> localToHeapObject;

    public PointsToResult(Map<String, List<HeapObject>> localToHeapObject) {
        this.localToHeapObject = localToHeapObject;
    }

    public List<HeapObject> localPointsTo(SootMethod method, String localName) {
        return localToHeapObject.get(method.getSignature() + localName);
    }
}
