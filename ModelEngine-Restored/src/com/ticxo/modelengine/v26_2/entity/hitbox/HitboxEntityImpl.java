package com.ticxo.modelengine.v26_2.entity.hitbox;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import com.ticxo.modelengine.api.utils.promise.Promise;
import com.ticxo.modelengine.v26_2.NMSFields;
import com.ticxo.modelengine.v26_2.entity.EntityUtils;
import java.util.UUID;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class HitboxEntityImpl extends LivingEntity implements HitboxEntity {
   private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
   private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
   @Getter
   private final ModelBone bone;
   @Getter
   private final SubHitbox subHitbox;
   private OBB obb;
   private volatile Vector3f location;
   private boolean markRemoved;

   public HitboxEntityImpl(Level world, @NotNull ModelBone bone, @NotNull SubHitbox subHitbox) {
      super(EntityTypes.SILVERFISH, world);
      this.bone = bone;
      this.subHitbox = subHitbox;
      this.setInvulnerable(true);
      this.setNoGravity(true);
      this.noPhysics = true;
   }

   public boolean canBeHitByProjectile() {
      return this.subHitbox.isCanBeHitByProjectile();
   }

   @NotNull
   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

   public boolean isInvisible() {
      return true;
   }

   public void push(@NotNull Entity entity) {
   }

   @NotNull
   protected AABB makeBoundingBox(Vec3 position) {
      if (this.subHitbox == null) {
         return super.makeBoundingBox(position);
      } else {
         Vector3f pos = this.subHitbox.getLocation();
         Vector3f dim = this.subHitbox.getDimension();
         if (this.subHitbox.isOBB()) {
            float halfX = dim.x * 0.5F;
            float halfY = dim.y * 0.5F;
            float halfZ = dim.z * 0.5F;
            Quaternionf rot = this.subHitbox.getRotation();
            float yaw = this.subHitbox.getYaw();
            this.obb = new OBB(pos.x - halfX, pos.y - halfY, pos.z - halfZ, pos.x + halfX, pos.y + halfY, pos.z + halfZ, rot, yaw);
            return this.obb;
         } else {
            float halfX = dim.x * 0.5F;
            float halfZ = dim.z * 0.5F;
            return new AABB(pos.x - halfX, pos.y, pos.z - halfZ, pos.x + halfX, pos.y + dim.y, pos.z + halfZ);
         }
      }
   }

   public void tick() {
      if (this.markRemoved) {
         this.discard();
      } else {
         super.tick();
         if (this.bone == null || this.subHitbox == null) {
            this.discard();
         } else if (!this.bone.getActiveModel().getModeledEntity().getBase().isAlive()) {
            this.discard();
         } else {
            Vector3f loc = this.location;
            if (loc != null && loc.isFinite()) {
               Vec3 vec = new Vec3(loc);
               this.setPos(vec);

               for (org.bukkit.entity.Entity entity : this.subHitbox.getBoundEntities().values()) {
                  EntityUtils.nms(entity).setPos(vec);
               }
            }
         }
      }
   }

   public boolean fireImmune() {
      return true;
   }

   protected void pushEntities() {
   }

   protected void doPush(@NotNull Entity entity) {
   }

   public void aiStep() {
   }

   public boolean isPushable() {
      return false;
   }

   public boolean hurtServer(@NotNull ServerLevel world, @NotNull DamageSource source, float amount) {
      for (org.bukkit.entity.Entity entity : this.subHitbox.getBoundEntities().values()) {
         EntityUtils.nms(entity).hurtServer(world, source, amount);
      }

      if (this.subHitbox.getDamageMultiplier() <= 1.0E-5F) {
         return false;
      }

      BaseEntity<?> baseEntity = this.bone.getActiveModel().getModeledEntity().getBase();
      CraftHumanEntity cause = source.getEntity() instanceof Player player ? player.getBukkitEntity() : null;
      return baseEntity.hurt(this, cause, source, amount * this.subHitbox.getDamageMultiplier());
   }

   @NotNull
   public InteractionResult interact(Player player, @NotNull InteractionHand hand, @NonNull Vec3 location) {
      if (player.getBukkitEntity() instanceof org.bukkit.entity.Player craftPlayer) {
         for (org.bukkit.entity.Entity entity : this.subHitbox.getBoundEntities().values()) {
            PlayerInteractAtEntityEvent event = new PlayerInteractAtEntityEvent(
               craftPlayer, entity, new Vector(0, 0, 0), hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND
            );
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               EntityUtils.nms(entity).interact(player, hand, location);
            }
         }
      }

      if (this.subHitbox.getDamageMultiplier() <= 1.0E-5F) {
         return InteractionResult.PASS;
      }

      EntityHandler.InteractionResult result = this.bone
         .getActiveModel()
         .getModeledEntity()
         .getBase()
         .interact(this, player.getBukkitEntity(), hand == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);

      return (InteractionResult)(switch (result) {
         case SUCCESS -> InteractionResult.SUCCESS_SERVER;
         case SUCCESS_NO_ITEM_USED -> InteractionResult.TRY_WITH_EMPTY_HAND;
         case CONSUME -> InteractionResult.CONSUME;
         case CONSUME_PARTIAL -> InteractionResult.SUCCESS;
         case PASS -> InteractionResult.PASS;
         case FAIL -> InteractionResult.FAIL;
      });
   }

   public boolean shouldBeSaved() {
      return false;
   }

   @NotNull
   public CraftEntity getBukkitEntity() {
      CraftEntity bukkitEntity = this.getBukkitEntityR();
      if (bukkitEntity == null) {
         synchronized (this) {
            CraftEntity craftEntity = new CraftLivingEntity(this.level().getCraftServer(), this);
            this.setBukkitEntityR(craftEntity);
            return craftEntity;
         }
      } else {
         return bukkitEntity;
      }
   }

   private void setBukkitEntityR(CraftEntity craftEntity) {
      ReflectionUtils.set(this, NMSFields.ENTITY_bukkitEntity, craftEntity);
   }

   private CraftEntity getBukkitEntityR() {
      return this.getBukkitEntityRaw();
   }

   protected int decreaseAirSupply(int air) {
      return air;
   }

   protected int increaseAirSupply(int air) {
      return air;
   }

   public void remove(@NotNull RemovalReason reason) {
      super.remove(reason);
      ModelEngineAPI.setRenderCanceled(this.getId(), false);
      ModelEngineAPI.getInteractionTracker().removeHitbox(this.getEntityId());
   }

   @Override
   public int getEntityId() {
      return this.getId();
   }

   @Override
   public UUID getUniqueId() {
      return this.getUUID();
   }

   @Override
   public void queueLocation(Vector3f location) {
      this.location = location;
   }

   @Override
   public Location getLocation() {
      return CraftLocation.toBukkit(this.position(), this.level().getWorld(), this.getBukkitYaw(), this.getXRot());
   }

   @Nullable
   @Override
   public OrientedBoundingBox getOrientedBoundingBox() {
      return this.obb == null ? null : this.obb.getBukkitOBB();
   }

   @Override
   public boolean isValid() {
      return !this.isRemoved() && !this.markRemoved;
   }

   @Override
   public void markRemoved() {
      if (!this.markRemoved) {
         this.markRemoved = true;
         ModelEngineAPI.setRenderCanceled(this.getId(), false);
         ModelEngineAPI.getInteractionTracker().removeHitbox(this.getEntityId());

         try {
            Promise.start(this.getBukkitEntity()).thenRunSync(() -> {
               if (!this.isRemoved()) {
                  this.discard();
               }
            });
         } catch (Throwable e) {
         }
      }
   }
}
