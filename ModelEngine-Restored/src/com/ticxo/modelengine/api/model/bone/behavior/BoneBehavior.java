package com.ticxo.modelengine.api.model.bone.behavior;

import com.ticxo.modelengine.api.events.BoneTransformReadEvent;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import org.jetbrains.annotations.Nullable;

public interface BoneBehavior extends DataIO {
   ModelBone getBone();

   BoneBehaviorType<?> getType();

   BoneBehaviorData getData();

   default void onApply() {
   }

   default void onRemove() {
   }

   default void onModelInitialized() {
   }

   default void onParentSwap(@Nullable ModelBone parent) {
   }

   default void preAnimation() {
   }

   default void onAnimation() {
   }

   default void postAnimation() {
   }

   default void onGlobalCalculation() {
   }

   default void preGlobalCalculation() {
   }

   default void preGlobalTranslate() {
   }

   default void postGlobalTranslate() {
   }

   default void preGlobalRotate() {
   }

   default void postGlobalRotate() {
   }

   default void preGlobalScale() {
   }

   default void postGlobalScale() {
   }

   default void preGlobalShear() {
   }

   default void postGlobalShear() {
   }

   default void postGlobalCalculation() {
   }

   default void preTransformDecompose() {
   }

   default void postTransformDecompose() {
   }

   default void postTransformRecompose() {
   }

   default float onUpdateYaw(float yaw) {
      return yaw;
   }

   @Deprecated
   default void preChildCalculation() {
   }

   @Deprecated
   default void postChildCalculation() {
   }

   default void onFinalize() {
   }

   default void modifyTransform(BoneTransformReadEvent event) {
   }

   default void preRender() {
   }

   default void onRender() {
   }

   default void postRender() {
   }

   default boolean isHidden() {
      return false;
   }

   @Override
   default void save(SavedData data) {
   }

   @Override
   default void load(SavedData data) {
   }
}
