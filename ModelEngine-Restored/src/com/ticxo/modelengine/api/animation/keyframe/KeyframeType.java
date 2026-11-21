package com.ticxo.modelengine.api.animation.keyframe;

import com.ticxo.modelengine.api.animation.Timeline;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IPriorityHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.interpolator.KeyframeInterpolator;
import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class KeyframeType<KEY extends AbstractKeyframe<DATA>, DATA> {
   private final String id;
   private final Supplier<KEY> keyframeSupplier;
   private final Function<Timeline, KeyframeInterpolator<KEY, DATA>> interpolatorSupplier;
   private final Map<Class<?>, KeyframeType.ModelUpdater> modelUpdaters;
   private final Map<Class<?>, KeyframeType.BoneUpdater> boneUpdaters;
   private final boolean global;
   private final KeyframeType.ModelUpdater priorityModelUpdater;
   private final KeyframeType.ModelUpdater stateMachineModelUpdater;
   private final KeyframeType.BoneUpdater priorityBoneUpdater;
   private final KeyframeType.BoneUpdater stateMachineBoneUpdater;

   protected KeyframeType(
      String id,
      Supplier<KEY> keyframeSupplier,
      Function<Timeline, KeyframeInterpolator<KEY, DATA>> interpolatorSupplier,
      Map<Class<?>, KeyframeType.ModelUpdater> modelUpdaters,
      Map<Class<?>, KeyframeType.BoneUpdater> boneUpdaters,
      boolean global
   ) {
      this.id = id;
      this.keyframeSupplier = keyframeSupplier;
      this.interpolatorSupplier = interpolatorSupplier;
      this.modelUpdaters = modelUpdaters;
      this.boneUpdaters = boneUpdaters;
      this.global = global;
      this.priorityModelUpdater = modelUpdaters.get(IPriorityHandler.class);
      this.stateMachineModelUpdater = modelUpdaters.get(IStateMachineHandler.class);
      this.priorityBoneUpdater = boneUpdaters.get(IPriorityHandler.class);
      this.stateMachineBoneUpdater = boneUpdaters.get(IStateMachineHandler.class);
   }

   public KeyframeInterpolator<KEY, DATA> createInterpolator(Timeline animation) {
      return this.interpolatorSupplier.apply(animation);
   }

   public KEY createKeyframe() {
      return this.keyframeSupplier.get();
   }

   public void updateModel(Class<?> handlerClass, AnimationHandler handler, Object... data) {
      KeyframeType.ModelUpdater updater;
      if (handlerClass == IPriorityHandler.class) {
         updater = this.priorityModelUpdater;
      } else if (handlerClass == IStateMachineHandler.class) {
         updater = this.stateMachineModelUpdater;
      } else {
         updater = this.modelUpdaters.get(handlerClass);
      }

      if (updater != null) {
         updater.update(handler, handler.getActiveModel(), data);
      }
   }

   public void updateBone(Class<?> handlerClass, AnimationHandler handler, ModelBone bone, Object... data) {
      KeyframeType.BoneUpdater updater;
      if (handlerClass == IPriorityHandler.class) {
         updater = this.priorityBoneUpdater;
      } else if (handlerClass == IStateMachineHandler.class) {
         updater = this.stateMachineBoneUpdater;
      } else {
         updater = this.boneUpdaters.get(handlerClass);
      }

      if (updater != null) {
         updater.update(handler, bone, data);
      }
   }

   @FunctionalInterface
   public interface BoneUpdater {
      void update(AnimationHandler animationHandler, ModelBone modelBone, Object... objs);
   }

   @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
   public static class Builder<KEY extends AbstractKeyframe<DATA>, DATA> {
      private final String id;
      private final Supplier<KEY> keyframeSupplier;
      private final Map<Class<?>, KeyframeType.ModelUpdater> modelUpdaters = new HashMap<>();
      private final Map<Class<?>, KeyframeType.BoneUpdater> boneUpdaters = new HashMap<>();
      private Function<Timeline, KeyframeInterpolator<KEY, DATA>> interpolatorSupplier = blueprintAnimation -> new KeyframeInterpolator<>();
      private boolean global;

      public static <KEY extends AbstractKeyframe<DATA>, DATA> KeyframeType.Builder<KEY, DATA> of(String id, Supplier<KEY> keyframeSupplier) {
         return new KeyframeType.Builder<>(id, keyframeSupplier);
      }

      public KeyframeType.Builder<KEY, DATA> interpolator(Function<Timeline, KeyframeInterpolator<KEY, DATA>> interpolatorSupplier) {
         this.interpolatorSupplier = interpolatorSupplier;
         return this;
      }

      public KeyframeType.Builder<KEY, DATA> registerModelUpdater(Class<?> handlerClass, KeyframeType.ModelUpdater updater) {
         this.modelUpdaters.put(handlerClass, updater);
         return this;
      }

      public KeyframeType.Builder<KEY, DATA> registerBoneUpdater(Class<?> handlerClass, KeyframeType.BoneUpdater updater) {
         this.boneUpdaters.put(handlerClass, updater);
         return this;
      }

      public KeyframeType.Builder<KEY, DATA> global() {
         this.global = true;
         return this;
      }

      public KeyframeType<KEY, DATA> build() {
         return new KeyframeType<>(this.id, this.keyframeSupplier, this.interpolatorSupplier, this.modelUpdaters, this.boneUpdaters, this.global);
      }
   }

   @FunctionalInterface
   public interface ModelUpdater {
      void update(AnimationHandler animationHandler, ActiveModel activeModel, Object... objs);
   }
}
