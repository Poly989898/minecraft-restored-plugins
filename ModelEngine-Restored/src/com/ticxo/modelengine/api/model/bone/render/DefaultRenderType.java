package com.ticxo.modelengine.api.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import org.jetbrains.annotations.Nullable;

public enum DefaultRenderType implements IRenderType {
   ANY,
   NONE,
   MODEL;

   @Nullable
   @Override
   public BehaviorRenderer createBehaviorRenderer(ActiveModel activeModel) {
      return null;
   }
}
