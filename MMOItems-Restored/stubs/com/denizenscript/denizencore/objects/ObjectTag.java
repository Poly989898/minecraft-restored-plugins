package com.denizenscript.denizencore.objects;

import com.denizenscript.denizencore.tags.Attribute;

public interface ObjectTag {
    String getPrefix();
    ObjectTag setPrefix(String prefix);
    boolean isUnique();
    String getObjectType();
    String identify();
    String identifySimple();
    ObjectTag getObjectAttribute(Attribute attribute);
}
