package wtf.thepalbi;

import java.util.*;

public class HeapObject {

    private final String heapLocation;
    private final String type;
    private final Map<String, List<HeapObject>> fieldPointsTo;

    public HeapObject(String heapLocation, String type) {
        this.heapLocation = heapLocation;
        this.type = type;
        this.fieldPointsTo = new HashMap<>();
    }

    protected void setFieldPointsTo(String fieldSignature, HeapObject target) {
        this.fieldPointsTo
                .computeIfAbsent(fieldSignature, f -> new LinkedList<>())
                .add(target);
    }

    public String getHeapLocation() {
        return heapLocation;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeapObject that = (HeapObject) o;
        // NOTE: Not including fields pointed to here. The only inconsistency is having two different types at some heap location.
        return Objects.equals(heapLocation, that.heapLocation) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heapLocation, type);
    }

    public List<HeapObject> fieldPointsTo(String fieldSignature) {
        return fieldPointsTo.computeIfAbsent(fieldSignature, f -> new LinkedList<>());
    }
}
