package com.guillaumevdn.questcreator;

import com.guillaumevdn.questcreator.lib.model.ElementModel;

public class ConfigQC {
    public static final ModelRegistry models = null;

    public interface ModelRegistry {
        ElementResult getElement(String id);
    }

    public interface ElementResult {
        ElementModel orNull();
    }
}
