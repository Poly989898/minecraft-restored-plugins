package com.ticxo.modelengine.core.model.bone.behavior;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.utils.config.DebugToggle;
import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class SubHitboxImpl extends AbstractBoneBehavior<SubHitboxImpl> implements SubHitbox {
   private final boolean isOBB;
   private final Vector3f fixedDimension = new Vector3f();
   private final Vector3f origin = new Vector3f();
   private final Vector3f cubeRotation = new Vector3f();
   private final Vector3f cubeOrigin = new Vector3f();
   private final Vector3f scaledOrigin = new Vector3f();
   private final int hitboxId;
   private final Vector3f dimension = new Vector3f();
   private final Vector3f location = new Vector3f();
   private final Quaternionf rotation = new Quaternionf();
   private final Map<UUID, Entity> boundEntities = Maps.newConcurrentMap();
   private float yaw;
   private HitboxEntity hitboxEntity;
   @Setter
   private float damageMultiplier = 1.0F;
   @Setter
   private boolean canBeHitByProjectile = true;

   public SubHitboxImpl(ModelBone bone, BoneBehaviorType<SubHitboxImpl> type, BoneBehaviorData data) {
      super(bone, type, data);
      Hitbox hitbox = data.get("dimension");
      if (hitbox == null) {
         throw new RuntimeException("Unable to retrieve sub-hitbox dimension of bone " + bone.getUniqueBoneId());
      }

      this.hitboxId = ModelEngineAPI.getEntityHandler().getNextEntityId();
      this.isOBB = data.get("obb", false);
      Vector3f bonePosition = bone.getBlueprintBone().getGlobalPosition();
      this.origin.set(data.get("origin", bonePosition)).sub(bonePosition);
      this.cubeRotation.set(data.get("rotation", this.cubeRotation));
      this.cubeOrigin.set(data.get("cube_origin", bonePosition)).sub(bonePosition);
      this.origin.sub(this.cubeOrigin).rotateX(this.cubeRotation.x).rotateY(this.cubeRotation.y).rotateZ(this.cubeRotation.z).add(this.cubeOrigin);
      if (this.isOBB) {
         this.fixedDimension.set(hitbox.getWidth(), hitbox.getHeight(), hitbox.getDepth());
      } else {
         this.fixedDimension.set(hitbox.getMaxWidth(), hitbox.getHeight(), hitbox.getMaxWidth());
      }
   }

   @Override
   public void onApply() {
      Location baseLocation = this.bone.calculatePivotLocation();
      this.removeOld();
      this.hitboxEntity = ModelEngineAPI.getEntityHandler().createHitbox(baseLocation, this.bone, this);
      this.bone
         .getBlueprintBone()
         .getLocalPosition()
         .rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0), this.location)
         .add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
      ModelEngineAPI.getInteractionTracker().setEntityRelay(this.hitboxId, this.hitboxEntity.getEntityId());
   }

   @Override
   public void onRemove() {
      ModelEngineAPI.getInteractionTracker().removeEntityRelay(this.hitboxId);
      this.removeOld();
      DualTicker.queueSyncTask(() -> this.boundEntities.forEach((uuid, entity) -> entity.remove()));
   }

   @Override
   public void postAnimation() {
      Vector3f localScale = this.bone.getLocalTransform().getScale();
      this.scaledOrigin.set(this.origin).mul(localScale);
      this.bone.getLocalTransform().mutatePosition(vector3f -> vector3f.add(this.scaledOrigin));
      this.bone.getLocalTransform().mutateLeftQuaternion(quaternionf -> quaternionf.mul(TMath.toQuaternion(this.cubeRotation)));
   }

   @Override
   public void onFinalize() {
      SafeTransform transform = this.bone.getGlobalTransform();
      this.location.set(transform.getPosition()).rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0));
      this.rotation.set(transform.getLeftQuaternion()).mul(transform.getRightQuaternion()).rotateLocalY(-this.bone.getYaw() * (float) (Math.PI / 180.0));
      transform.getScale().mul(this.fixedDimension, this.dimension);
      this.dimension.mul(this.bone.getBlueprintBone().getScale()).mul(this.bone.getActiveModel().getScale());
      Location baseLocation = this.bone.calculatePivotLocation();
      this.location.add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
      if (!this.isOBB) {
         this.location.sub(0.0F, this.dimension.y * 0.5F, 0.0F);
      }

      if (this.hitboxEntity != null && !this.hitboxEntity.isValid()) {
         this.hitboxEntity = null;
      }

      if (this.hitboxEntity == null) {
         this.hitboxEntity = ModelEngineAPI.getEntityHandler().createHitbox(baseLocation, this.bone, this);
         if (this.hitboxEntity == null) {
            return;
         }

         ModelEngineAPI.getInteractionTracker().setEntityRelay(this.hitboxId, this.hitboxEntity.getEntityId());
      }

      this.hitboxEntity.queueLocation(new Vector3f(this.location));
      if (DebugToggle.isDebugging(DebugToggle.SHOW_OBB) && this.isOBB) {
         OrientedBoundingBox obb = this.hitboxEntity.getOrientedBoundingBox();
         if (obb != null) {
            obb.visualize(baseLocation.getWorld());
         }
      }
   }

   @Override
   public void addBoundEntity(Entity entity) {
      this.getBoundEntities().put(entity.getUniqueId(), entity);
      ModelEngineAPI.getEntityHandler().forceDespawn(entity);
      ModelEngineAPI.setRenderCanceled(entity.getEntityId(), true);
   }

   @Override
   public void removeBoundEntity(Entity entity) {
      this.getBoundEntities().remove(entity.getUniqueId(), entity);
      ModelEngineAPI.setRenderCanceled(entity.getEntityId(), false);
      ModelEngineAPI.getEntityHandler().forceSpawn(entity);
   }

   private void removeOld() {
      if (this.hitboxEntity != null) {
         this.hitboxEntity.markRemoved();
      }
   }
}
