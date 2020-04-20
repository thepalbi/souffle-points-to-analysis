package wtf.thepalbi;

import java.util.List;
import java.util.Map;

public class PointsToResult {
    private Map<String, List<HeapObject>> localToHeapObject;

    public PointsToResult(Map<String, List<HeapObject>> localToHeapObject) {
        this.localToHeapObject = localToHeapObject;
    }
}
