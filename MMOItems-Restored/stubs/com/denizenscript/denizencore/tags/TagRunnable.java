package com.denizenscript.denizencore.tags;

import com.denizenscript.denizencore.objects.ObjectTag;

public class TagRunnable {
    public interface BaseInterface { ObjectTag run(Attribute attribute); }
    public interface ObjectInterface<T extends ObjectTag> { ObjectTag run(Attribute attribute, T object); }
}
