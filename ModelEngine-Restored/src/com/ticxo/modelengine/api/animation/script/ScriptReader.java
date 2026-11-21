package com.ticxo.modelengine.api.animation.script;

import com.ticxo.modelengine.api.animation.property.IAnimationProperty;

public interface ScriptReader {
   void read(IAnimationProperty property, String script);
}
