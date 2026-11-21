package com.ticxo.modelengine.v1_20_R3.entity.hitbox;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import com.ticxo.modelengine.v1_20_R3.NMSFields;
import com.ticxo.modelengine.v1_20_R3.entity.EntityUtils;
import java.util.UUID;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HitboxEntityImpl extends EntityLiving implements HitboxEntity {
   private final NonNullList<ItemStack> handItems = NonNullList.a(2, ItemStack.f);
   private final NonNullList<ItemStack> armorItems = NonNullList.a(4, ItemStack.f);
   @Getter
   private final ModelBone bone;
   @Getter
   private final SubHitbox subHitbox;
   private OBB obb;
   private volatile Vector3f location;
   private boolean markRemoved;

   public HitboxEntityImpl(World world, @NotNull ModelBone bone, @NotNull SubHitbox subHitbox) {
      super(EntityTypes.aJ, world);
      this.bone = bone;
      this.subHitbox = subHitbox;
      this.m(true);
      this.e(true);
      this.af = true;
   }

   public EnumMainHand fm() {
      return EnumMainHand.b;
   }

   public Iterable<ItemStack> bL() {
      return this.armorItems;
   }

   public ItemStack c(EnumItemSlot slot) {
      return switch (slot.a()) {
         case a -> this.handItems.get(slot.b());
         case b -> this.armorItems.get(slot.b());
         default -> throw new IncompatibleClassChangeError();
      };
   }

   public void a(EnumItemSlot slot, ItemStack stack) {
      this.e(stack);
      switch (slot.a()) {
         case a:
            this.a(slot, this.handItems.set(slot.b(), stack), stack);
            break;
         case b:
            this.a(slot, this.armorItems.set(slot.b(), stack), stack);
      }
   }

   public boolean ce() {
      return true;
   }

   public void g(Entity entity) {
   }

   @NotNull
   protected AxisAlignedBB aq() {
      if (this.subHitbox == null) {
         return super.aq();
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
            return new AxisAlignedBB(pos.x - halfX, pos.y, pos.z - halfZ, pos.x + halfX, pos.y + dim.y, pos.z + halfZ);
         }
      }
   }

   public void l() {
      if (this.markRemoved) {
         this.am();
      } else {
         super.l();
         if (this.bone == null || this.subHitbox == null) {
            this.am();
         } else if (!this.bone.getActiveModel().getModeledEntity().getBase().isAlive()) {
            this.am();
         } else {
            Vector3f loc = this.location;
            if (loc != null && loc.isFinite()) {
               Vec3D vec = new Vec3D(loc);
               this.b(vec);

               for (org.bukkit.entity.Entity entity : this.subHitbox.getBoundEntities().values()) {
                  EntityUtils.nms(entity).b(vec);
               }
            }
         }
      }
   }

   public boolean aY() {
      return true;
   }

   protected void M_() {
   }

   protected void D(Entity entity) {
   }

   public void d_() {
   }

   public boolean bu() {
      return false;
   }

   public boolean a(DamageSource source, float amount) {
      for (org.bukkit.entity.Entity entity : this.subHitbox.getBoundEntities().values()) {
         EntityUtils.nms(entity).a(source, amount);
      }

      if (this.subHitbox.getDamageMultiplier() <= 1.0E-5F) {
         return false;
      }

      CraftHumanEntity cause = source.d() instanceof EntityHuman player ? player.getBukkitEntity() : null;
      return this.bone.getActiveModel().getModeledEntity().getBase().hurt(this, cause, source, amount * this.subHitbox.getDamageMultiplier());
   }

   @NotNull
   public EnumInteractionResult a(EntityHuman player, EnumHand hand) {
      if (player.getBukkitEntity() instanceof Player craftPlayer) {
         for (org.bukkit.entity.Entity entity : this.subHitbox.getBoundEntities().values()) {
            PlayerInteractAtEntityEvent event = new PlayerInteractAtEntityEvent(
               craftPlayer, entity, new Vector(0, 0, 0), hand == EnumHand.b ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND
            );
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               EntityUtils.nms(entity).a(player, hand);
            }
         }
      }

      if (this.subHitbox.getDamageMultiplier() <= 1.0E-5F) {
         return EnumInteractionResult.d;
      }

      EntityHandler.InteractionResult result = this.bone
         .getActiveModel()
         .getModeledEntity()
         .getBase()
         .interact(this, player.getBukkitEntity(), hand == EnumHand.a ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);

      return switch (result) {
         case SUCCESS, SUCCESS_NO_ITEM_USED -> EnumInteractionResult.a;
         case CONSUME -> EnumInteractionResult.b;
         case CONSUME_PARTIAL -> EnumInteractionResult.c;
         case PASS -> EnumInteractionResult.d;
         case FAIL -> EnumInteractionResult.e;
      };
   }

   public boolean dK() {
      return false;
   }

   public CraftEntity getBukkitEntity() {
      CraftEntity bukkitEntity = this.getBukkitEntityR();
      if (bukkitEntity == null) {
         synchronized (this) {
            CraftEntity craftEntity = new CraftLivingEntity(this.dM().getCraftServer(), this);
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

   protected int m(int air) {
      return air;
   }

   protected int n(int air) {
      return air;
   }

   public void a(RemovalReason reason) {
      super.a(reason);
      ModelEngineAPI.setRenderCanceled(this.aj(), false);
      ModelEngineAPI.getInteractionTracker().removeHitbox(this.getEntityId());
   }

   @Override
   public int getEntityId() {
      return this.aj();
   }

   @Override
   public UUID getUniqueId() {
      return this.cw();
   }

   @Override
   public void queueLocation(Vector3f location) {
      this.location = location;
   }

   @Override
   public Location getLocation() {
      return CraftLocation.toBukkit(this.dk(), this.dM().getWorld(), this.getBukkitYaw(), this.dE());
   }

   @Nullable
   @Override
   public OrientedBoundingBox getOrientedBoundingBox() {
      return this.obb == null ? null : this.obb.getBukkitOBB();
   }

   @Override
   public boolean isValid() {
      return !this.dH() && !this.markRemoved;
   }

   @Override
   public void markRemoved() {
      this.markRemoved = true;
      ModelEngineAPI.setRenderCanceled(this.aj(), false);
      ModelEngineAPI.getInteractionTracker().removeHitbox(this.getEntityId());
   }
}
