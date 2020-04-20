package wtf.thepalbi;

public class HeapObject {

    private final String heapLocation;
    private final String type;

    public HeapObject(String heapLocation, String type) {
        this.heapLocation = heapLocation;
        this.type = type;
    }
}
