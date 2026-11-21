package com.ticxo.modelengine.api.model.render;

import com.ticxo.modelengine.api.model.bone.render.renderer.RenderQueues;
import com.ticxo.modelengine.api.utils.data.PooledCollection;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.joml.Vector3f;

public interface DisplayRenderer extends ModelRenderer, RenderQueues<DisplayBone> {
   int getTick();

   DisplayRenderer.Pivot getPivot();

   DisplayRenderer.Hitbox getHitbox();

   void pushFullUpdate(UUID uuid);

   boolean pollFullUpdate(UUID uuid);

   default boolean hasGlowAudience() {
      for (DisplayBone bone : this.getRendered().values()) {
         if (bone.getGlowAudience().get() != null) {
            return true;
         }
      }

      return false;
   }

   interface Hitbox {
      int getPivotId();

      UUID getPivotUuid();

      int getHitboxId();

      UUID getHitboxUuid();

      int getShadowId();

      UUID getShadowUuid();

      void clearDirty();

      DataTracker<Vector3f> getPosition();

      DataTracker<Float> getWidth();

      DataTracker<Float> getHeight();

      DataTracker<Float> getShadowRadius();

      DataTracker<Boolean> getHitboxVisible();

      DataTracker<Boolean> getShadowVisible();

      DataTracker<Boolean> getFireVisible();

      PooledCollection<DisplayFire> getFireDisplay();

      default boolean isHitboxDirty() {
         return this.getWidth().isDirty() || this.getHeight().isDirty();
      }

      default boolean isHitboxVisible() {
         return this.getHitboxVisible().get();
      }

      default boolean isShadowVisible() {
         return this.getShadowVisible().get();
      }

      default boolean isFireVisible() {
         return this.getFireVisible().get();
      }

      default boolean isPivotVisible() {
         return this.isHitboxVisible() || this.isShadowVisible() || this.isFireVisible();
      }

      default PositionCodec getPositionCodec() {
         return null;
      }
   }

   interface Pivot {
      int getId();

      UUID getUuid();

      void clearDirty();

      boolean isOverridden();

      DataTracker<Vector3f> getPosition();

      CollectionDataTracker<Integer> getPassengers();

      DataTracker<Float> getCameraHeight();

      default boolean isCameraPivot() {
         return this.getCameraHeight().get() != null;
      }

      default PositionCodec getPositionCodec() {
         return null;
      }
   }
}
