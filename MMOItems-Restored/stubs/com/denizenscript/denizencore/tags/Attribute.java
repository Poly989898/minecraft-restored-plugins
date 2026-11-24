package com.denizenscript.denizencore.tags;

import com.denizenscript.denizencore.objects.ObjectTag;

public class Attribute {
    public boolean hasContext(int i) { return false; }
    public <T extends ObjectTag> T contextAsType(int i, Class<T> type) { return null; }
    public void echoError(String message) { }
}
