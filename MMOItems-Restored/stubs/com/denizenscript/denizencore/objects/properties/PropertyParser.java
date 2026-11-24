package com.denizenscript.denizencore.objects.properties;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;

public class PropertyParser {
    public interface PropertyTag<T extends Property> { ObjectTag run(Attribute attribute, T property); }
    public static void registerProperty(Class<? extends Property> property, Class<? extends ObjectTag> target) { }
    public static <T extends Property> void registerTag(String name, PropertyTag<T> runnable, String... variants) { }
}
