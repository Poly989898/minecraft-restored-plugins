package com.denizenscript.denizencore.objects;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;

public class ObjectFetcher {
    public static void registerWithObjectFetcher(Class<? extends ObjectTag> type, ObjectTagProcessor<?> processor) { }
}
