package com.ticxo.modelengine.api.model.render;

import com.ticxo.modelengine.api.lod.BoneSnapshotHandler;
import com.ticxo.modelengine.api.utils.data.UpdateScheme;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface DisplayBone {
   default boolean isDirty() {
      return this.isTransformDirty() || this.isRenderDirty() || this.isModelDirty();
   }

   default boolean isSkippable() {
      return this.isTransformDirty() && !this.isRenderDirty() && !this.isModelDirty();
   }

   default boolean isTransformDirty() {
      return this.getPosition().isDirty() || this.getLeftRotation().isDirty() || this.getScale().isDirty() || this.getRightRotation().isDirty();
   }

   default boolean isRenderDirty() {
      return this.getBillboard().isDirty()
         || this.getDisplay().isDirty()
         || this.getVisibility().isDirty()
         || this.getGlowing().isDirty()
         || this.getGlowColor().isDirty()
         || this.getBrightness().isDirty()
         || this.getStep().isDirty();
   }

   default boolean isModelDirty() {
      return this.getModelUpdateScheme().hasUpdate();
   }

   default void clearDirty() {
      this.getStep().clearDirty();
      this.getPosition().clearDirty();
      this.getLeftRotation().clearDirty();
      this.getScale().clearDirty();
      this.getRightRotation().clearDirty();
      this.getBillboard().clearDirty();
      this.getModelUpdateScheme().reset();
      this.getDisplay().clearDirty();
      this.getVisibility().clearDirty();
      this.getGlowing().clearDirty();
      this.getGlowColor().clearDirty();
      this.getBrightness().clearDirty();
   }

   default boolean isAudienceGlowDirty() {
      return this.getGlowAudience().isDirty() || this.getAudienceGlowing().isDirty() || this.getAudienceGlowColor().isDirty();
   }

   default void clearAudienceGlowDirty() {
      this.getGlowAudience().clearDirty();
      this.getAudienceGlowing().clearDirty();
      this.getAudienceGlowColor().clearDirty();
   }

   String getBoneId();

   BoneSnapshotHandler getSnapshotHandler();

   DataTracker<Boolean> getRender();

   DataTracker<Boolean> getStep();

   DataTracker<Vector3f> getPosition();

   DataTracker<Quaternionf> getLeftRotation();

   DataTracker<Vector3f> getScale();

   DataTracker<Quaternionf> getRightRotation();

   DataTracker<Billboard> getBillboard();

   Map<Integer, DisplayBone.BoneData> getModel();

   UpdateScheme<DisplayBone.BoneData> getModelUpdateScheme();

   DataTracker<ItemDisplayTransform> getDisplay();

   DataTracker<Boolean> getVisibility();

   DataTracker<Boolean> getGlowing();

   DataTracker<Integer> getGlowColor();

   DataTracker<Integer> getBrightness();

   DataTracker<Set<UUID>> getGlowAudience();

   DataTracker<Boolean> getAudienceGlowing();

   DataTracker<Integer> getAudienceGlowColor();

   interface BoneData {
      int getId();

      UUID getUuid();

      DisplayBone getBone();

      DataTracker<ItemStack> getModel();

      int getModelHash();
   }
}
