package com.ticxo.modelengine.api.model.bone;

import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.OffsetMode;
import com.ticxo.modelengine.api.utils.StepFlag;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.api.utils.math.Transform;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ModelBone extends DataIO {
   String getUniqueBoneId();

   String getBoneId();

   String getCustomId();

   void setCustomId(String customId);

   ActiveModel getActiveModel();

   BlueprintBone getBlueprintBone();

   @Nullable
   ModelBone getParent();

   @Nullable
   ModelBone getPivot();

   void setParent(@Nullable ModelBone bone);

   void setPivot(@Nullable ModelBone pivot);

   boolean isPivotOverride();

   void setPivotLocation(Location pivotLocation);

   Location getPivotLocation();

   Location calculatePivotLocation();

   Location getBaseLocation();

   Map<String, ModelBone> getChildren();

   boolean isRenderer();

   void setRenderer(boolean isRenderer);

   void addTickDependencies(String... boneIds);

   void removeTickDependencies(String... boneIds);

   void clearTickDependencies();

   Set<String> getTickDependencies();

   void tick();

   void lazyTick();

   boolean isMarkedDestroy();

   void destroy();

   float getYaw();

   void setYaw(float yaw);

   void setModelScale(int scale);

   void calculateGlobalTransform();

   void setManualAnimator(ManualAnimator manualAnimator);

   ManualAnimator getManualAnimator();

   SafeTransform getLocalTransform();

   SafeTransform getGlobalTransform();

   Matrix4f getTransformMatrix();

   boolean hasGlobalRotation();

   void setHasGlobalRotation(boolean hasGlobalRotation);

   @Deprecated
   ItemStack getModel();

   BoneItems getModels();

   void setModel(BlueprintBone bone);

   void setModel(ItemStack stack);

   void clearModel();

   DataTracker<BoneItems> getModelTracker();

   Color getDefaultTint();

   void setDefaultTint(Color defaultTint);

   Color getDamageTint();

   void setDamageTint(Color damageTint);

   boolean isEnchanted();

   void setEnchanted(boolean enchanted);

   boolean isVisible();

   void setVisible(boolean visible);

   boolean isScaleHidden();

   boolean isGlowing();

   void setGlowing(@Nullable Boolean glowing);

   int getGlowColor();

   void setGlowColor(@Nullable Integer glowColor);

   @Nullable
   Set<UUID> getGlowAudience();

   void setGlowAudience(@Nullable Set<UUID> glowAudience);

   int getBlockLight();

   void setBlockLight(int blockLight);

   int getSkyLight();

   void setSkyLight(int skyLight);

   Billboard getBillboard();

   void setBillboard(Billboard billboard);

   default int getBrightness() {
      int blockLight = this.getBlockLight();
      int skyLight = this.getSkyLight();
      if (blockLight < 0 && skyLight < 0) {
         return -1;
      }

      blockLight = Math.max(blockLight, 0);
      skyLight = Math.max(skyLight, 0);
      return blockLight << 4 | skyLight << 20;
   }

   boolean isEffectivelyInvisible();

   boolean shouldStep();

   void markStep(StepFlag stepFlag, String animation, float key);

   int getSubTickSamples();

   void setSubTickSamples(int samples);

   @Nullable
   Transform getSubTickTransform(int index);

   @Nullable
   Matrix4f getSubTickMatrix(int index);

   @Nullable
   Location getSubTickLocation(int index);

   @Nullable
   Location getSubTickLocation(int index, OffsetMode mode, Vector3f offset, boolean scale);

   boolean isSubTickStep(int index);

   boolean pollModelScaleChanged();

   boolean isRootBone();

   void setBoneOnGround(boolean isBoneOnGround);

   Location getLocationUnsafe();

   Location getLocation();

   default Location getLocation(OffsetMode mode, Vector3f offset, boolean scale) {
      return this.getLocation(mode, offset, scale, true);
   }

   Location getLocation(OffsetMode mode, Vector3f offset, boolean scale, boolean safe);

   void addBoneBehavior(BoneBehavior boneBehavior);

   boolean hasBoneBehavior(BoneBehaviorType<?> type);

   <T extends BoneBehavior> Optional<T> getBoneBehavior(BoneBehaviorType<T> type);

   <T extends BoneBehavior> Optional<T> removeBoneBehavior(BoneBehaviorType<T> type);

   Map<BoneBehaviorType<?>, BoneBehavior> getImmutableBoneBehaviors();

   void forBehaviors(Consumer<BoneBehavior> consumer);

   default boolean hasBoneBehaviors() {
      return !this.getImmutableBoneBehaviors().isEmpty();
   }
}
