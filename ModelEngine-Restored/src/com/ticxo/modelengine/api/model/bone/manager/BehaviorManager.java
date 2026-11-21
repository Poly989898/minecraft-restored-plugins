package com.ticxo.modelengine.api.model.bone.manager;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;

public interface BehaviorManager<T extends BoneBehavior> extends DataIO {
   ActiveModel getActiveModel();

   BoneBehaviorType<T> getType();

   default void onCreate() {
   }

   default void onDestroy() {
   }

   default void preBoneTick() {
   }

   default void postBoneTick() {
   }

   default void preScriptTick() {
   }

   default void postScriptTick() {
   }

   default void preBoneRender() {
   }

   default void postBoneRender() {
   }

   @Override
   default void load(SavedData data) {
   }

   @Override
   default void save(SavedData data) {
   }
}
