package com.denizenscript.denizencore.objects.properties;

import com.denizenscript.denizencore.objects.Mechanism;

public interface Property {
    String getPropertyString();
    String getPropertyId();
    void adjust(Mechanism mechanism);
}
