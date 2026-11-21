package com.ticxo.modelengine.api.generator.blueprint;

import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class BlueprintBone {
   private final Map<String, BlueprintBone> children = new LinkedHashMap<>();
   private final Map<String, Map<String, Object>> behaviors = new LinkedHashMap<>();
   private final transient Map<BoneBehaviorType<?>, BoneBehaviorType.CachedProvider<?>> cachedBehaviorProvider = new LinkedHashMap<>();
   @Setter
   private String name;
   @Setter
   private UUID uuid;
   @Setter
   private ModelBlueprint modelBlueprint;
   @Setter
   private boolean isRenderer;
   @Setter
   private int scale = 1;
   @Setter
   @Deprecated
   private BaseItemEnum baseItem;
   @Setter
   @Deprecated
   private int dataId;
   private final ItemModelData modelData = new ItemModelData();
   @Setter
   private Vector3f localPosition;
   @Setter
   private Vector3f localRotation;
   @Setter
   private Quaternionf localQuaternion = new Quaternionf();
   @Setter
   private Vector3f globalPosition;
   @Setter
   private Vector3f rotatedGlobalPosition;
   @Setter
   private Vector3f globalRotation;
   private Quaternionf globalQuaternion = new Quaternionf();
   private Quaternionf invGlobalQuaternion = new Quaternionf();
   @Setter
   private BlueprintBone parent;
   @Setter
   private Vector3f modelScale = new Vector3f(1.0F);
   @Setter
   private BlueprintBone dupeTarget;
   @Setter
   private boolean renderByDefault = true;

   public void setGlobalQuaternion(Quaternionf globalQuaternion) {
      this.globalQuaternion.set(globalQuaternion);
      this.invGlobalQuaternion.set(globalQuaternion.invert());
   }
}
