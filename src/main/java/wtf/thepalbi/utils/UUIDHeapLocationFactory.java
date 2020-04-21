package wtf.thepalbi.utils;

import java.util.UUID;

public class UUIDHeapLocationFactory implements HeapLocationFactory {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
