package wtf.thepalbi;

import java.util.UUID;

public class UUIDHeapLocationFactory implements HeapLocationFactory
{
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
