package com.ticxo.modelengine.api.model.bone.behavior;

import com.google.gson.JsonDeserializer;
import com.ticxo.modelengine.api.error.ErrorMissingBoneBehaviorData;
import com.ticxo.modelengine.api.error.ErrorWrongBoneBehaviorDataType;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.render.DefaultRenderType;
import com.ticxo.modelengine.api.model.bone.render.IRenderType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BoneBehaviorType<T extends BoneBehavior> {
   private final BoneBehaviorType.BehaviorProvider<T> behaviorProvider;
   private final BoneBehaviorType.BehaviorManagerProvider<T> behaviorManagerProvider;
   private final String id;
   private final Map<String, Class<?>> requiredArguments;
   private final Map<String, Class<?>> optionalArguments;
   private final Map<Class<?>, JsonDeserializer<?>> dataDeserializer;
   private final IRenderType renderType;
   private final Set<ProceduralType> proceduralTypes;
   private final Predicate<Set<BoneBehaviorType<?>>> predicate;
   private final BoneBehaviorType.BehaviorProvider<T> forcedBehaviorProvider;
   private final boolean ignoreCubes;
   private final boolean pivot;

   public void assignCachedProvider(BlueprintBone bone, Map<String, Object> data) {
      Map<String, Object> verifiedData = new HashMap<>();

      for (Entry<String, Class<?>> entry : this.requiredArguments.entrySet()) {
         String key = entry.getKey();
         Class<?> clazz = entry.getValue();
         Object value = data.get(key);
         if (value == null) {
            new ErrorMissingBoneBehaviorData(bone.getName(), this, key);
            return;
         }

         if (!clazz.isAssignableFrom(value.getClass())) {
            new ErrorWrongBoneBehaviorDataType(bone.getName(), this, key, clazz, value.getClass());
            return;
         }

         verifiedData.put(key, value);
      }

      for (Entry<String, Class<?>> entry : this.optionalArguments.entrySet()) {
         String key = entry.getKey();
         Class<?> clazz = entry.getValue();
         Object value = data.get(key);
         if (value != null) {
            if (!clazz.isAssignableFrom(value.getClass())) {
               new ErrorWrongBoneBehaviorDataType(bone.getName(), this, key, clazz, value.getClass());
               return;
            }

            verifiedData.put(key, value);
         }
      }

      bone.getCachedBehaviorProvider().put(this, new BoneBehaviorType.CachedProvider<>(this.behaviorProvider, this, new BoneBehaviorData(verifiedData)));
   }

   public void assignForcedCachedProvider(BlueprintBone bone) {
      if (this.forcedBehaviorProvider != null) {
         if (!bone.getCachedBehaviorProvider().containsKey(this)) {
            bone.getCachedBehaviorProvider()
               .put(this, new BoneBehaviorType.CachedProvider<>(this.forcedBehaviorProvider, this, new BoneBehaviorData(new HashMap<>())));
         }
      }
   }

   public boolean test(Set<BoneBehaviorType<?>> types) {
      return this.predicate.test(types);
   }

   public static boolean noProcedural(Set<BoneBehaviorType<?>> types) {
      for (BoneBehaviorType<?> type : types) {
         if (!type.getProceduralTypes().isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @FunctionalInterface
   public interface BehaviorManagerProvider<T extends BoneBehavior> {
      BehaviorManager<T> create(ActiveModel activeModel, BoneBehaviorType<T> boneBehaviorType);
   }

   @FunctionalInterface
   public interface BehaviorProvider<T extends BoneBehavior> {
      T create(ModelBone modelBone, BoneBehaviorType<T> boneBehaviorType, BoneBehaviorData boneBehaviorData);
   }

   @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
   public static class Builder<T extends BoneBehavior> {
      private final BoneBehaviorType.BehaviorProvider<T> behaviorProvider;
      private final BoneBehaviorType.BehaviorManagerProvider<T> behaviorManagerProvider;
      private final String id;
      private final Map<String, Class<?>> requiredArguments = new HashMap<>();
      private final Map<String, Class<?>> optionalArguments = new HashMap<>();
      private final Map<Class<?>, JsonDeserializer<?>> dataDeserializer = new HashMap<>();
      private final Set<ProceduralType> proceduralTypes = new HashSet<>();
      private IRenderType renderType = DefaultRenderType.ANY;
      private Predicate<Set<BoneBehaviorType<?>>> predicate = boneBehaviorTypes -> true;
      private BoneBehaviorType.BehaviorProvider<T> forcedBehaviorProvider;
      private boolean ignoreCubes;
      private boolean pivot;

      public static <T extends BoneBehavior> BoneBehaviorType.Builder<T> of(
         BoneBehaviorType.BehaviorProvider<T> behaviorProvider, @Nullable BoneBehaviorType.BehaviorManagerProvider<T> behaviorManagerProvider, String id
      ) {
         return new BoneBehaviorType.Builder<>(behaviorProvider, behaviorManagerProvider, id);
      }

      public BoneBehaviorType.Builder<T> required(String key, Class<?> type) {
         this.requiredArguments.put(key, type);
         return this;
      }

      public <S> BoneBehaviorType.Builder<T> required(String key, Class<S> type, JsonDeserializer<S> deserializer) {
         this.requiredArguments.put(key, type);
         this.dataDeserializer.put(type, deserializer);
         return this;
      }

      public BoneBehaviorType.Builder<T> optional(String key, Class<?> type) {
         this.optionalArguments.put(key, type);
         return this;
      }

      public <S> BoneBehaviorType.Builder<T> optional(String key, Class<S> type, JsonDeserializer<S> deserializer) {
         this.optionalArguments.put(key, type);
         this.dataDeserializer.put(type, deserializer);
         return this;
      }

      public BoneBehaviorType.Builder<T> renderType(IRenderType type) {
         this.renderType = type;
         return this;
      }

      public BoneBehaviorType.Builder<T> procedural(ProceduralType... types) {
         this.proceduralTypes.addAll(Arrays.asList(types));
         return this;
      }

      public BoneBehaviorType.Builder<T> predicate(Predicate<Set<BoneBehaviorType<?>>> predicate) {
         this.predicate = predicate;
         return this;
      }

      public BoneBehaviorType.Builder<T> forced(BoneBehaviorType.BehaviorProvider<T> forcedBehaviorProvider) {
         this.forcedBehaviorProvider = forcedBehaviorProvider;
         return this;
      }

      public BoneBehaviorType.Builder<T> ignoreCubes() {
         this.ignoreCubes = true;
         return this;
      }

      public BoneBehaviorType.Builder<T> pivot() {
         this.pivot = true;
         return this;
      }

      public BoneBehaviorType<T> build() {
         return new BoneBehaviorType<>(
            this.behaviorProvider,
            this.behaviorManagerProvider,
            this.id,
            this.requiredArguments,
            this.optionalArguments,
            this.dataDeserializer,
            this.renderType,
            this.proceduralTypes,
            this.predicate,
            this.forcedBehaviorProvider,
            this.ignoreCubes,
            this.pivot
         );
      }
   }

   @AllArgsConstructor
   public static class CachedProvider<T extends BoneBehavior> {
      private final BoneBehaviorType.BehaviorProvider<T> behaviorProvider;
      @Getter
      private final BoneBehaviorType<T> type;
      @Getter
      private final BoneBehaviorData data;

      public T create(ModelBone bone) {
         return this.behaviorProvider.create(bone, this.type, this.data);
      }
   }
}
