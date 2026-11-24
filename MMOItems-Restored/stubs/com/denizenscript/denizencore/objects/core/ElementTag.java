package com.denizenscript.denizencore.objects.core;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;

public class ElementTag implements ObjectTag {
    public String getPrefix() { return null; }
    public ObjectTag setPrefix(String prefix) { return this; }
    public boolean isUnique() { return false; }
    public String getObjectType() { return null; }
    public String identify() { return null; }
    public String identifySimple() { return null; }
    public ObjectTag getObjectAttribute(Attribute attribute) { return null; }
    public ElementTag(String s) { }
    public ElementTag(boolean b) { }
}
