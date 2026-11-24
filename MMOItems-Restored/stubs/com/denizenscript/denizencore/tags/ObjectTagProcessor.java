package com.denizenscript.denizencore.tags;

import com.denizenscript.denizencore.objects.ObjectTag;

public class ObjectTagProcessor<T extends ObjectTag> {
    public void registerTag(String name, TagRunnable.ObjectInterface<T> runnable, String... variants) { }
    public ObjectTag getObjectAttribute(T object, Attribute attribute) { return null; }
}
