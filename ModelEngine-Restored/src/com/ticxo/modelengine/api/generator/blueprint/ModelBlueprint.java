package com.ticxo.modelengine.api.generator.blueprint;

import com.google.common.collect.ImmutableMap;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.error.ErrorIncompatibleBone;
import com.ticxo.modelengine.api.error.ErrorUnknownBoneBehavior;
import com.ticxo.modelengine.api.error.WarnIncompatibleBoneBehavior;
import com.ticxo.modelengine.api.error.WarningDuplicateBoneName;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorRegistry;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.render.DefaultRenderType;
import com.ticxo.modelengine.api.model.bone.render.IRenderType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ModelBlueprint {
   private final Map<String, BlueprintBone> bones = new LinkedHashMap<>();
   private final Map<String, BlueprintAnimation> animations = new LinkedHashMap<>();
   private final transient Set<String> animationDescendingPriority = new LinkedHashSet<>();
   private final Map<String, String> animationsPlaceholders = new LinkedHashMap<>();
   @Setter
   private String name;
   @Setter
   private Hitbox mainHitbox = new Hitbox(0.6, 1.8, 0.6, 1.44);
   @Setter
   private float shadowRadius = 0.0F;
   private transient Map<String, BlueprintBone> flatMap;
   @Setter
   private transient boolean flatViewConstructed;
   @Setter
   private transient boolean descendingAnimationConstructed;
   @Setter
   private transient boolean boneBehaviorCached;

   public void finalizeModel(ErrorCollector collector) {
      this.constructFlatBoneMap(collector);
      this.constructDescendingAnimation(collector);
      this.cacheBoneBehaviors(collector);
   }

   public void constructFlatBoneMap(ErrorCollector collector) {
      if (!this.flatViewConstructed) {
         this.flatViewConstructed = true;
         Map<String, BlueprintBone> tempMap = new LinkedHashMap<>();
         Set<BlueprintBone> bones = new LinkedHashSet<>(this.bones.values());
         Set<BlueprintBone> buffer = new LinkedHashSet<>();

         while (!bones.isEmpty()) {
            for (BlueprintBone bone : bones) {
               buffer.addAll(bone.getChildren().values());
               if (tempMap.containsKey(bone.getName())) {
                  new WarningDuplicateBoneName(bone.getName(), bone.getUuid()).log(collector);
                  bone.setName(bone.getUuid().toString());
               }

               tempMap.put(bone.getName(), bone);
            }

            bones.clear();
            bones.addAll(buffer);
            buffer.clear();
         }

         this.flatMap = ImmutableMap.copyOf(tempMap);
      }
   }

   public void constructDescendingAnimation(ErrorCollector collector) {
      if (!this.descendingAnimationConstructed) {
         this.descendingAnimationConstructed = true;
         ArrayList<String> list = new ArrayList<>();
         this.animations.keySet().forEach(s -> list.add(0, s));
         this.animationDescendingPriority.addAll(list);
      }
   }

   public void cacheBoneBehaviors(ErrorCollector collector) {
      if (!this.boneBehaviorCached) {
         this.boneBehaviorCached = true;
         assert this.flatMap != null : "Bone flat view map is null.";
         BoneBehaviorRegistry behaviorRegistry = ModelEngineAPI.getAPI().getBoneBehaviorRegistry();

         for (Entry<String, BlueprintBone> boneEntry : this.flatMap.entrySet()) {
            String name = boneEntry.getKey();
            BlueprintBone bone = boneEntry.getValue();

            for (Entry<String, Map<String, Object>> entry : bone.getBehaviors().entrySet()) {
               String bbId = entry.getKey();
               BoneBehaviorType<?> behaviorType = behaviorRegistry.getById(bbId);
               if (behaviorType == null) {
                  new ErrorUnknownBoneBehavior(name, bbId).log(collector);
               } else if (!this.canBoneUseBehavior(bone, behaviorType)) {
                  new ErrorIncompatibleBone(bone.isRenderer(), name, bbId).log(collector);
               } else {
                  if (!behaviorType.test(bone.getCachedBehaviorProvider().keySet())) {
                     new WarnIncompatibleBoneBehavior(name, bbId).log(collector);
                  }

                  behaviorType.assignCachedProvider(bone, entry.getValue());
               }
            }

            for (String id : behaviorRegistry.getIds()) {
               BoneBehaviorType<?> type = behaviorRegistry.getById(id);
               if (type != null) {
                  type.assignForcedCachedProvider(bone);
               }
            }
         }
      }
   }

   public BlueprintAnimation getAnimationOrRef(String animation) {
      if (animation.startsWith("ref[") && animation.endsWith("]")) {
         animation = animation.substring(4, animation.length() - 1);
         String[] split = animation.split("\\.", 2);
         if (split.length >= 2) {
            ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(split[0]);
            if (blueprint != null) {
               return blueprint.animations.get(split[1]);
            }
         }
      }

      return this.animations.get(animation);
   }

   private boolean canBoneUseBehavior(BlueprintBone bone, BoneBehaviorType<?> type) {
      IRenderType renderType = type.getRenderType();
      if (renderType == DefaultRenderType.ANY) {
         return true;
      } else {
         return renderType == DefaultRenderType.MODEL ? bone.isRenderer() : !bone.isRenderer();
      }
   }
}
