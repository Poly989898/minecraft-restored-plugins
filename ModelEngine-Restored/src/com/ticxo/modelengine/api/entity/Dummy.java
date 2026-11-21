package com.ticxo.modelengine.api.entity;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.data.DummyEntityData;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.impl.DefaultBodyRotationController;
import com.ticxo.modelengine.api.nms.impl.EmptyLookController;
import com.ticxo.modelengine.api.nms.impl.EmptyMoveController;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

@Setter
public class Dummy<T> implements BaseEntity<T> {
   @Getter
   protected final int entityId;
   protected final UUID uuid;
   @Getter
   protected final T original;
   @Getter
   protected final DummyEntityData<T> data;
   @Getter
   protected final DefaultBodyRotationController bodyRotationController;
   @Getter
   protected final MoveController moveController;
   @Getter
   protected final LookController lookController;
   @Getter
   protected boolean detectingPlayers = true;
   @Getter
   protected boolean isRemoved;
   @Getter
   protected boolean isWalking;
   @Getter
   protected boolean isStrafing;
   @Getter
   protected boolean isJumping;
   @Getter
   protected boolean isFlying;
   @Getter
   protected boolean isGlowing;
   @Getter
   protected int glowColor;
   @Getter
   protected boolean isOnFire;
   @Getter
   protected float yHeadRot;
   @Getter
   protected float xHeadRot;
   @Getter
   protected float yBodyRot;
   @Getter
   protected Hitbox hitbox = new Hitbox(0.0, 0.0, 0.0, 0.0);
   @Getter
   protected BoundingBox boundingBox = new BoundingBox();

   public Dummy() {
      this(null);
   }

   public Dummy(T original) {
      this(UUID.randomUUID(), original);
   }

   public Dummy(UUID uuid, T original) {
      this(ModelEngineAPI.getEntityHandler().getNextEntityId(), uuid, original);
   }

   public Dummy(int id, UUID uuid, T original) {
      this.entityId = id;
      this.uuid = uuid;
      this.original = original;
      this.data = new DummyEntityData<>(this);
      this.bodyRotationController = new DefaultBodyRotationController(this);
      this.moveController = new EmptyMoveController();
      this.lookController = new EmptyLookController();
   }

   @Override
   public boolean isAlive() {
      return !this.isRemoved;
   }

   @Override
   public UUID getUUID() {
      return this.uuid;
   }

   @Override
   public int getRenderRadius() {
      return this.data.getRenderRadius();
   }

   @Override
   public void setRenderRadius(int radius) {
      this.data.setRenderRadius(radius);
   }

   @Override
   public float getYRot() {
      return this.yHeadRot;
   }

   @Override
   public float getHealth() {
      return 20.0F;
   }

   @Override
   public float getMaxHealth() {
      return 20.0F;
   }

   public void setLocation(Location location) {
      this.data.setLocation(location);
      this.boundingBox = this.hitbox.createBoundingBox(location.toVector());
   }

   public void syncLocation(Location location) {
      this.setLocation(location);
      this.setYBodyRot(location.getYaw());
      this.setYHeadRot(location.getYaw());
      this.setXHeadRot(location.getPitch());
      this.bodyRotationController.syncFromBase();
   }

   public void setForceViewing(Player player, boolean flag) {
      if (flag) {
         this.setForceHidden(player, false);
         this.data.getTracked().addForcedPairing(player.getUniqueId());
      } else {
         this.data.getTracked().removeForcedPairing(player.getUniqueId());
      }
   }

   public void setForceHidden(Player player, boolean flag) {
      if (flag) {
         this.setForceViewing(player, false);
         this.data.getTracked().addForcedHidden(player.getUniqueId());
      } else {
         this.data.getTracked().removeForcedHidden(player.getUniqueId());
      }
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
   public boolean isForcedAlive() {
      return false;
   }

   @Override
   public void setForcedAlive(boolean flag) {
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
   public boolean hurt(@Nullable HumanEntity player, Object nmsDamageCause, float damage) {
      return false;
   }

   @Override
   public boolean hurt(HitboxEntity hitbox, @Nullable HumanEntity player, Object nmsDamageCause, float damage) {
      return false;
   }

   @Override
   public EntityHandler.InteractionResult interact(HumanEntity player, EquipmentSlot slot) {
      return EntityHandler.InteractionResult.SUCCESS_NO_ITEM_USED;
   }

   @Override
   public EntityHandler.InteractionResult interact(HitboxEntity hitbox, HumanEntity player, EquipmentSlot slot) {
      return EntityHandler.InteractionResult.SUCCESS_NO_ITEM_USED;
   }
}
