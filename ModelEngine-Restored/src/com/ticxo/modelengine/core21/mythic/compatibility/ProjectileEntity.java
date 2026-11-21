package com.ticxo.modelengine.core21.mythic.compatibility;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.impl.EmptyBodyRotationController;
import com.ticxo.modelengine.api.nms.impl.EmptyLookController;
import com.ticxo.modelengine.api.nms.impl.EmptyMoveController;
import com.ticxo.modelengine.core21.Java21Helper;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.skills.projectiles.Projectile.ProjectileTracker;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

@Setter
public class ProjectileEntity<T extends ProjectileBulletableTracker & IParentSkill> implements BaseEntity<T> {
   @Getter
   private final T original;
   @Getter
   private final ProjectileData data;
   @Getter
   private final int entityId;
   private final UUID uuid;
   @Getter
   private final BodyRotationController bodyRotationController = new EmptyBodyRotationController();
   @Getter
   private final MoveController moveController = new EmptyMoveController();
   @Getter
   private final LookController lookController = new EmptyLookController();
   @Getter
   private int renderRadius = 32;

   public ProjectileEntity(T original) {
      this.original = original;
      this.entityId = ModelEngineAPI.getEntityHandler().getNextEntityId();
      this.uuid = UUID.randomUUID();
      this.data = new ProjectileData(this);
      Java21Helper.getMythicCompatibility().getMythicSupport().getTrackers().put(original, this);
   }

   @Override
   public void registerData() {
      ModelEngineAPI.getAPI().getDataTrackers().execute(this.getUUID(), (uuid, tracker) -> tracker.putEntityData(uuid, this.data));
   }

   @Override
   public boolean isVisible() {
      return true;
   }

   @Override
   public void setVisible(boolean flag) {
   }

   @Override
   public boolean isRemoved() {
      return !this.data.isDataValid();
   }

   @Override
   public boolean isAlive() {
      return this.data.isDataValid();
   }

   @Override
   public boolean isForcedAlive() {
      return false;
   }

   @Override
   public void setForcedAlive(boolean flag) {
   }

   @Override
   public UUID getUUID() {
      return this.uuid;
   }

   @Override
   public double getMaxStepHeight() {
      return 0.0;
   }

   @Override
   public void setMaxStepHeight(double stepHeight) {
   }

   @Override
   public void setCollidableWith(Entity entity, boolean flag) {
   }

   @Override
   public boolean isGlowing() {
      return false;
   }

   @Override
   public int getGlowColor() {
      return -1;
   }

   @Override
   public boolean isOnFire() {
      return false;
   }

   @Override
   public boolean hurt(@Nullable HumanEntity player, Object nmsDamageCause, float damage) {
      return false;
   }

   @Override
   public boolean hurt(HitboxEntity hitbox, @Nullable HumanEntity player, Object nmsDamageCause, float damage) {
      return false;
   }

   @Override
   public EntityHandler.InteractionResult interact(HumanEntity player, EquipmentSlot slot) {
      return null;
   }

   @Override
   public EntityHandler.InteractionResult interact(HitboxEntity hitbox, HumanEntity player, EquipmentSlot slot) {
      return null;
   }

   @Override
   public float getYRot() {
      AbstractVector vel = this.original.getCurrentVelocity();
      return vel == null ? 0.0F : (float)Math.toDegrees(Math.atan2(-vel.getX(), vel.getZ()));
   }

   @Override
   public float getYHeadRot() {
      return this.getYRot();
   }

   @Override
   public float getXHeadRot() {
      AbstractVector vel = this.original.getCurrentVelocity();
      if (vel == null) {
         return 0.0F;
      }

      double hMag = Math.sqrt(vel.getX() * vel.getX() + vel.getZ() * vel.getZ());
      return (float)Math.toDegrees(-Math.atan2(vel.getY(), hMag));
   }

   @Override
   public float getYBodyRot() {
      return this.getYRot();
   }

   @Override
   public boolean isWalking() {
      return false;
   }

   @Override
   public boolean isStrafing() {
      return false;
   }

   @Override
   public boolean isJumping() {
      return false;
   }

   @Override
   public boolean isFlying() {
      return false;
   }

   @Override
   public float getHealth() {
      return 0.0F;
   }

   @Override
   public float getMaxHealth() {
      return 0.0F;
   }

   @Override
   public BoundingBox getBoundingBox() {
      return this.original instanceof ProjectileTracker tracker ? tracker.getHitbox() : new BoundingBox();
   }

   public void removeSelf() {
      Java21Helper.getMythicCompatibility().getMythicSupport().getTrackers().remove(this.original, this);
   }
}
