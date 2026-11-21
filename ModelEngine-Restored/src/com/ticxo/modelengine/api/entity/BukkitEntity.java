package com.ticxo.modelengine.api.entity;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.impl.DefaultBodyRotationController;
import com.ticxo.modelengine.api.nms.impl.EmptyLookController;
import com.ticxo.modelengine.api.nms.impl.EmptyMoveController;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class BukkitEntity implements BaseEntity<Entity> {
   protected final EntityHandler entityHandler;
   @Getter
   protected final Entity original;
   @Getter
   protected final BukkitEntityData data;
   @Getter
   protected final BodyRotationController bodyRotationController;
   @Getter
   protected final MoveController moveController;
   @Getter
   protected final LookController lookController;
   @Getter
   protected boolean isVisible = true;
   protected int entityId;

   public BukkitEntity(Entity original) {
      this.entityHandler = ModelEngineAPI.getNMSHandler().getEntityHandler();
      this.original = original;
      this.entityId = this.entityHandler.getEntityId(original);
      this.data = this.createEntityData(original);
      this.bodyRotationController = this.entityHandler.wrapBodyRotationControl(original, () -> new DefaultBodyRotationController(this));
      this.moveController = this.entityHandler.wrapMoveController(original, EmptyMoveController::new);
      this.lookController = this.entityHandler.wrapLookController(original, EmptyLookController::new);
      this.entityHandler.wrapNavigation(original);
   }

   protected BukkitEntityData createEntityData(Entity original) {
      return new BukkitEntityData(original, this);
   }

   @Override
   public void registerData() {
      ModelEngineAPI.getAPI().getDataTrackers().execute(this.getUUID(), (uuid, tracker) -> tracker.putEntityData(uuid, this.data));
   }

   @Override
   public void setVisible(boolean flag) {
      if (this.isVisible != flag) {
         this.isVisible = flag;
         if (this.isVisible) {
            for (UUID player : this.data.getTracking().keySet()) {
               if (!player.equals(this.original.getUniqueId())) {
                  this.entityHandler.forceSpawn(this, Bukkit.getPlayer(player));
               }
            }
         } else if (ModelEngineAPI.getPivotOverrideRegistry().get(this.original.getUniqueId()).isEmpty()) {
            for (UUID player : this.data.getTracking().keySet()) {
               if (!player.equals(this.original.getUniqueId())) {
                  this.entityHandler.forceDespawn(this, Bukkit.getPlayer(player));
               }
            }
         }
      }
   }

   public void onWorldChanged() {
      int oldId = this.entityId;
      int newId = this.entityHandler.getEntityId(this.original);
      if (newId != oldId) {
         this.entityId = newId;
         ModelEngineAPI.getAPI().getModelUpdaters().rekeyEntityId(this.getUUID(), oldId, newId);
      }

      if (!this.isVisible && ModelEngineAPI.getPivotOverrideRegistry().get(this.original.getUniqueId()).isEmpty()) {
         for (UUID player : this.data.getTracked().getTrackedPlayer()) {
            if (!player.equals(this.original.getUniqueId())) {
               this.entityHandler.forceDespawn(this, Bukkit.getPlayer(player));
            }
         }
      }
   }

   @Override
   public boolean isRemoved() {
      return this.data.isRemoved();
   }

   @Override
   public boolean isAlive() {
      return this.data.isEntityValid();
   }

   @Override
   public boolean isForcedAlive() {
      return this.data.isForcedAlive();
   }

   @Override
   public void setForcedAlive(boolean flag) {
      this.data.setForcedAlive(flag);
   }

   @Override
   public int getEntityId() {
      return this.entityId;
   }

   @Override
   public UUID getUUID() {
      return this.original.getUniqueId();
   }

   @Override
   public double getMaxStepHeight() {
      return this.entityHandler.getStepHeight(this.original);
   }

   @Override
   public void setMaxStepHeight(double stepHeight) {
      this.entityHandler.setStepHeight(this.original, stepHeight);
   }

   @Override
   public int getRenderRadius() {
      return this.data.getTracked().getBaseRange();
   }

   @Override
   public void setRenderRadius(int radius) {
      this.data.getTracked().setBaseRange(radius);
   }

   @Override
   public void setCollidableWith(Entity entity, boolean flag) {
      if (this.original instanceof LivingEntity livingEntity) {
         if (ServerInfo.IS_FOLIA) {
            this.original.getScheduler().run(ModelEngineAPI.getAPI(), task -> {
               if (this.original.isValid()) {
                  Set<UUID> set = livingEntity.getCollidableExemptions();
                  if (set != null) {
                     if (flag) {
                        set.remove(entity.getUniqueId());
                     } else {
                        set.add(entity.getUniqueId());
                     }
                  }
               }
            }, null);
         } else {
            Set<UUID> set = livingEntity.getCollidableExemptions();
            if (set == null) {
               return;
            }

            if (flag) {
               set.remove(entity.getUniqueId());
            } else {
               set.add(entity.getUniqueId());
            }
         }
      }
   }

   @Override
   public boolean isGlowing() {
      return ServerInfo.IS_FOLIA ? this.data.isBaseGlowing() : this.original.isGlowing();
   }

   @Override
   public int getGlowColor() {
      return this.data.getGlowColor();
   }

   @Override
   public boolean isOnFire() {
      return ServerInfo.IS_FOLIA ? this.data.isOnFire() : this.original.isVisualFire() || this.original.getFireTicks() > 0;
   }

   @Override
   public boolean hurt(@Nullable HumanEntity player, Object nmsDamageCause, float damage) {
      return this.entityHandler.hurt(this.original, nmsDamageCause, damage);
   }

   @Override
   public boolean hurt(HitboxEntity hitbox, @Nullable HumanEntity player, Object nmsDamageCause, float damage) {
      if (this.original.hasMetadata("skill-damage")) {
         List<MetadataValue> metadataList = this.original.getMetadata("skill-damage");
         MetadataValue metadata = metadataList.get(0);
         this.original.setMetadata("skill-damage", metadata);
         this.original.setMetadata("hitbox", new FixedMetadataValue(ModelEngineAPI.getAPI(), hitbox.getBone().getBoneId()));

         try {
            return this.entityHandler.hurt(this.original, nmsDamageCause, damage);
         } finally {
            this.original.removeMetadata("hitbox", ModelEngineAPI.getAPI());
            this.original.removeMetadata("skill-damage", metadata.getOwningPlugin());
         }
      } else {
         this.original.setMetadata("hitbox", new FixedMetadataValue(ModelEngineAPI.getAPI(), hitbox.getBone().getBoneId()));

         try {
            return this.entityHandler.hurt(this.original, nmsDamageCause, damage);
         } finally {
            this.original.removeMetadata("hitbox", ModelEngineAPI.getAPI());
         }
      }
   }

   @Override
   public EntityHandler.InteractionResult interact(HumanEntity player, EquipmentSlot slot) {
      if (player instanceof Player serverPlayer) {
         PlayerInteractAtEntityEvent event = new PlayerInteractAtEntityEvent(serverPlayer, this.original, new Vector(0, 0, 0), slot);
         Bukkit.getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return EntityHandler.InteractionResult.FAIL;
         }
      }

      return this.entityHandler.interact(this.original, player, slot);
   }

   @Override
   public EntityHandler.InteractionResult interact(HitboxEntity hitbox, HumanEntity player, EquipmentSlot slot) {
      this.original.setMetadata("hitbox", new FixedMetadataValue(ModelEngineAPI.getAPI(), hitbox.getBone().getBoneId()));

      try {
         return this.interact(player, slot);
      } finally {
         this.original.removeMetadata("hitbox", ModelEngineAPI.getAPI());
      }
   }

   @Override
   public float getYRot() {
      return ServerInfo.IS_FOLIA ? this.data.getYRot() : this.entityHandler.getYRot(this.original);
   }

   @Override
   public float getYHeadRot() {
      return ServerInfo.IS_FOLIA ? this.data.getYHeadRot() : this.entityHandler.getYHeadRot(this.original);
   }

   @Override
   public float getXHeadRot() {
      return ServerInfo.IS_FOLIA ? this.data.getXHeadRot() : this.entityHandler.getXHeadRot(this.original);
   }

   @Override
   public float getYBodyRot() {
      return ServerInfo.IS_FOLIA ? this.data.getYBodyRot() : this.entityHandler.getYBodyRot(this.original);
   }

   @Override
   public boolean isWalking() {
      return ServerInfo.IS_FOLIA ? this.data.isWalking() : this.entityHandler.isWalking(this.original);
   }

   @Override
   public boolean isStrafing() {
      return ServerInfo.IS_FOLIA ? this.data.isStrafing() : this.entityHandler.isStrafing(this.original);
   }

   @Override
   public boolean isJumping() {
      return ServerInfo.IS_FOLIA ? this.data.isJumping() : this.entityHandler.isJumping(this.original);
   }

   @Override
   public boolean isFlying() {
      return ServerInfo.IS_FOLIA ? this.data.isFlying() : this.entityHandler.isFlying(this.original);
   }

   @Override
   public float getHealth() {
      return ServerInfo.IS_FOLIA ? this.data.getHealth() : this.entityHandler.getHealth(this.original);
   }

   @Override
   public float getMaxHealth() {
      return ServerInfo.IS_FOLIA ? this.data.getMaxHealth() : this.entityHandler.getMaxHealth(this.original);
   }

   @Override
   public BoundingBox getBoundingBox() {
      return ServerInfo.IS_FOLIA ? this.data.getBoundingBox() : this.original.getBoundingBox().clone();
   }

   @Override
   public void save(SavedData data) {
      data.putInt("render_radius", this.getRenderRadius());
      double step = this.data.getStepHeight();
      data.putDouble("step", Double.isNaN(step) ? this.getMaxStepHeight() : step);
      this.getData().save().ifPresent(entityData -> data.putData("entity_data", entityData));
      this.getBodyRotationController().save().ifPresent(controllerData -> data.putData("body_rotation", controllerData));
      Set<UUID> exemptions = this.data.getCollidableExemptions();
      if (exemptions != null) {
         data.putList("collide_exemption", exemptions);
      }
   }

   @Override
   public void load(SavedData data) {
      BaseEntity.super.load(data);
      Set<UUID> exemptions = this.entityHandler.getCollidableExemptions(this.original);
      if (exemptions != null) {
         exemptions.addAll(data.getList("collide_exemption"));
      }

      this.data.refreshSaveSnapshots();
   }
}
